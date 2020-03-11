package org.hzero.generator.export.helper;

import org.hzero.generator.export.helper.entity.Data;
import org.hzero.generator.export.helper.entity.DataGroup;
import org.hzero.generator.export.helper.enums.LiquibaseEngineMode;
import org.hzero.generator.export.helper.exception.LiquibaseHelperException;
import org.hzero.generator.export.helper.mapper.LiquibaseHelperMapper;
import org.hzero.generator.export.helper.supporter.CellData;
import org.hzero.generator.export.helper.supporter.ExcelEngine;
import org.hzero.generator.export.helper.supporter.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Liquibase Engine
 * </p>
 *
 * @author qingsheng.chen 2018/11/24 星期六 9:32
 */
@Component
public class LiquibaseEngine implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseEngine.class);
    private static final List<CellData> INIT_CELL;
    private static final int START_ROW = 7;
    private static final int START_COLUMN = 5;
    private static int nextStartRow = START_ROW;
    private String filePath;
    private LiquibaseEngineMode engineMode;
    private File excelFile;
    private ExcelEngine excelEngine;
    private List<DataGroup> dataGroupList;
    private Map<String, List<CellData>> sheetTitleMap = new HashMap<>();
    private Map<Object, CellData> refMap = new HashMap<>();
    private static ApplicationContext applicationContext;
    private static LiquibaseHelperMapper liquibaseHelperMapper;

    static {
        INIT_CELL = new ArrayList<>();
        INIT_CELL.add(new CellData("A", 1, "日期", false, CellData.CellStyle.BOLD));
        INIT_CELL.add(new CellData("B", 1, "作者", false, CellData.CellStyle.BOLD));
        INIT_CELL.add(new CellData("C", 1, "说明", false, CellData.CellStyle.BOLD));
        INIT_CELL.add(new CellData("D", 1, "表", false, CellData.CellStyle.BOLD));
        INIT_CELL.add(new CellData("E", 4, "*自动生成", false, CellData.CellStyle.ORANGE));
        INIT_CELL.add(new CellData("F", 4, "#唯一性检查", false, CellData.CellStyle.BLUE));
        INIT_CELL.add(new CellData("G", 4, "公式=外键引用", false, CellData.CellStyle.GREEN));
    }

    private LiquibaseEngine() {
    }

    public static LiquibaseEngine createEngine(String filePath, LiquibaseEngineMode engineMode) {
        return new LiquibaseEngine().setFilePath(filePath).setEngineMode(engineMode).loadFile().loadExcel();
    }

    public LiquibaseEngine loadFile() {
        Assert.notNull(filePath, "指定的文件路径不能为 [NULL]！");
        if (engineMode == LiquibaseEngineMode.OVERRIDE) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException e) {
                throw new LiquibaseHelperException("文件删除失败！");
            }
            engineMode = LiquibaseEngineMode.CREATE;
        }
        this.excelFile = new File(filePath);
        if (engineMode == LiquibaseEngineMode.CREATE) {
            try {
                if (!excelFile.getParentFile().exists()) {
                    excelFile.getParentFile().mkdirs();
                }
                Assert.isTrue(excelFile.createNewFile(), "文件创建失败！");
            } catch (IOException e) {
                throw new LiquibaseHelperException("文件创建失败！", e);
            }
        }
        if (!excelFile.isFile()) {
            excelFile = null;
            throw new LiquibaseHelperException("指定的路径不是一个文件！");
        }
        if (engineMode == LiquibaseEngineMode.CREATE) {
            InputStream in = getClass().getClassLoader().getResourceAsStream("static/liquibase-template.xlsx");
            assert in != null;
            FileHelper.copyFile(in, excelFile);
        }
        return this;
    }

    public LiquibaseEngine loadExcel() {
        Assert.notNull(excelFile, "找不到加载的excel文件！");
        excelEngine = new ExcelEngine(excelFile);
        return this;
    }

    public void generate() {
        logger.debug("Generate Excel : {}", dataGroupList);
        if (CollectionUtils.isEmpty(dataGroupList)) {
            logger.warn("初始化内容为空！");
            return;
        }
        logger.info("开始生成数据....");
        dataGroupList.forEach(this::generateSheet);
        logger.info("开始写入文件....");
        excelEngine.writeFile();
        logger.info("写入文件成功！");
    }

    private void generateSheet(DataGroup dataGroup) {
        Assert.isTrue(StringUtils.hasText(dataGroup.getSheetName()), "Excel Sheet页名称不能为空！");
        // 创建 Sheet 页
        logger.info("开始创建Sheet页 {}...", dataGroup.getSheetName());
        excelEngine.createSheet(dataGroup.getSheetName());
        // 初始化 Sheet 页数据
        excelEngine.writeCell(dataGroup.getSheetName(), INIT_CELL);
        if (StringUtils.isEmpty(dataGroup.getSheetName())) {
            return;
        }
        nextStartRow = START_ROW;
        dataGroup.getDataList().forEach(data -> generateData(dataGroup, data));
    }

    private void generateData(final DataGroup dataGroup, Data data) {
        // 初始化 Title
        logger.info("开始写入标题 {}...", data.getTableName());
        excelEngine.writeCell(dataGroup.getSheetName(), initCell(data));
        // load data
        logger.info("开始查询数据 {}...", data.getTableName());
        LiquibaseHelperMapper liquibaseHelperMapper = getLiquibaseHelperMapper();
        // 选择数据库
        liquibaseHelperMapper.selectSchema(data.getSchemaName());
        List<Map<String, Object>> dataList = liquibaseHelperMapper.selectData(data.getTableName(), data.getColumnList(), data.getWhere());
        logger.info("查询全部数据数据 {} 条记录...", dataList.size());
        AtomicInteger rowIndex = new AtomicInteger(nextStartRow + 1);
        logger.info("写入数据...", dataList.size());
        dataList.forEach(dataMap -> {
            List<CellData> cellDataList = initDataCell(dataGroup.getSheetName(), data.getTableName(), dataMap, rowIndex.get());
            if (!CollectionUtils.isEmpty(cellDataList)) {
                rowIndex.incrementAndGet();
                excelEngine.writeCell(dataGroup.getSheetName(), cellDataList);
            }
        });
        logger.info("写入完成...", dataList.size());
        nextStartRow = rowIndex.incrementAndGet();
    }

    private List<CellData> initCell(Data data) {
        List<CellData> cellData = new LinkedList<>();
        cellData.add(new CellData("A", nextStartRow, data.getCreationDateText()));
        cellData.add(new CellData("B", nextStartRow, data.getAuthor()));
        cellData.add(new CellData("C", nextStartRow, data.getDescription()));
        cellData.add(new CellData("D", nextStartRow, data.getTableName(), false, CellData.CellStyle.BOLD));
        if (!CollectionUtils.isEmpty(data.getColumnList())) {
            AtomicInteger columnIndex = new AtomicInteger(START_COLUMN);
            data.getColumnList().forEach(column -> {
                if (column.isMultiLang()) {
                    Assert.isTrue(StringUtils.hasText(column.getPkName()), "请指定多语言字段的主键名称！");
                    column.getLang().forEach(lang -> {
                        CellData columnCell = new CellData(columnIndex.getAndIncrement(), nextStartRow, column.getColumnName() + ":" + lang, column.isFormula(), column.getCellStyle());
                        cellData.add(columnCell);
                    });
                } else {
                    CellData columnCell = new CellData(columnIndex.getAndIncrement(), nextStartRow, column.getColumnNameText(), column.isFormula(), column.getCellStyle());
                    cellData.add(columnCell.setCited(column.isCited()).setAutoGenerate(column.isAutoGenerate()).setId(column.isId()).setColumnName(column.getColumnName())
                            .setRelTableName(column.getReference() != null ? column.getReference().getTableName() : null));
                }
            });
        }
        sheetTitleMap.put(data.getTableName(), cellData);
        return cellData;
    }

    private List<CellData> initDataCell(String sheetName, String tableName, Map<String, Object> dataMap, int currentRowIndex) {
        List<CellData> cellData = new LinkedList<>();
        Assert.isTrue(sheetTitleMap.containsKey(tableName), "无法找到表名对应的列配置！");
        List<CellData> titleCellList = sheetTitleMap.get(tableName);
        for (int i = START_COLUMN - 1; i < titleCellList.size(); ++i) {
            if (titleCellList.get(i).isCited()) {

                refMap.put(tableName + "-" + dataMap.get(String.valueOf(titleCellList.get(i).getValue())), CellData.copy(titleCellList.get(i)).setSheetName(sheetName).setRow(currentRowIndex));
            }
            if (titleCellList.get(i).isAutoGenerate()) {
                dataMap.put(String.valueOf(titleCellList.get(i).getValue()), "*");
            }
            Object value;
            if (titleCellList.get(i).isCited()) {
                value = tableName + "-" + currentRowIndex;
            } else if (titleCellList.get(i).isFormula()) {
                CellData ref = refMap.get(titleCellList.get(i).getRelTableName() + "-" + dataMap.get(String.valueOf(titleCellList.get(i).getValue())));
                // 自关联根节点处理
                if (ref==null && tableName.equals(titleCellList.get(i).getRelTableName())){
                    value = dataMap.get(String.valueOf(titleCellList.get(i).getValue()));
                }
                else if (ref == null) {
                    // 没有找到关联,去除无用数据
                    return null;
                } else {
                    StringBuilder sb = new StringBuilder("=");
                    if (StringUtils.hasText(ref.getSheetName())) {
                        sb.append(ref.getSheetName()).append("!");
                    }
                    sb.append("$").append(ref.getColumnText()).append("$").append(ref.getRow());
                    value = sb.toString();
                }
            } else {
                value = dataMap.get(String.valueOf(titleCellList.get(i).getValue()));
            }
            CellData newCell = new CellData(i + 1, currentRowIndex, value, titleCellList.get(i).isFormula());

            if (titleCellList.get(i).isFormula() && newCell.getValue() == null) {
                logger.error("无法找到引用 {}", dataMap);
                newCell.setValue(0);
            }
            cellData.add(newCell);
        }
        return cellData;
    }

    public String getFilePath() {
        return filePath;
    }

    public LiquibaseEngine setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public LiquibaseEngineMode getEngineMode() {
        return engineMode;
    }

    public LiquibaseEngine setEngineMode(LiquibaseEngineMode engineMode) {
        this.engineMode = engineMode;
        return this;
    }

    public File getExcelFile() {
        return excelFile;
    }

    public LiquibaseEngine setExcelFile(File excelFile) {
        this.excelFile = excelFile;
        return this;
    }

    public ExcelEngine getExcelEngine() {
        return excelEngine;
    }

    public LiquibaseEngine setExcelEngine(ExcelEngine excelEngine) {
        this.excelEngine = excelEngine;
        return this;
    }

    public List<DataGroup> getDataGroupList() {
        return dataGroupList;
    }

    public LiquibaseEngine setDataGroupList(List<DataGroup> dataGroupList) {
        this.dataGroupList = dataGroupList;
        return this;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LiquibaseEngine.applicationContext = applicationContext;
    }

    private static LiquibaseHelperMapper getLiquibaseHelperMapper() {
        if (liquibaseHelperMapper == null) {
            liquibaseHelperMapper = applicationContext.getBean(LiquibaseHelperMapper.class);
        }
        return liquibaseHelperMapper;
    }
}
