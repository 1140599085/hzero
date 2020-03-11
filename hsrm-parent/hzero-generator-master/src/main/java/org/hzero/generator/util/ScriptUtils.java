package org.hzero.generator.util;

import static java.util.Optional.ofNullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hzero.generator.entity.Lang;
import org.hzero.generator.entity.Reference;
import org.hzero.generator.entity.Service;
import org.hzero.generator.export.constants.Constants;
import org.hzero.generator.export.helper.LiquibaseEngine;
import org.hzero.generator.export.helper.entity.Column;
import org.hzero.generator.export.helper.entity.Data;
import org.hzero.generator.export.helper.entity.DataGroup;
import org.hzero.generator.export.helper.enums.LiquibaseEngineMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 描述:装配script脚本数据
 *
 * @author wanshun.zhang@hand-china.com
 * @date 2019/12/02 16:01
 */
@Component
public class ScriptUtils {

    /**
     * <p>
     * 主键非自增标记前缀
     * </p>
     *
     * @author Andy 2019/12/20 15:23
     */
    private final String TAG = "*";
    private final Logger LOGGER = LoggerFactory.getLogger(ScriptUtils.class);

    public void create(List<Service> serviceList) {
        serviceList.forEach(service -> {
            LOGGER.info("开始处理：{}", service.getDescription());
            service.getExcelList().forEach(excel -> {
                LOGGER.info("开始处理：[{}]，使用数据库：[{}]", excel.getDescription(), excel.getSchema());
                LiquibaseEngine liquibaseEngine = LiquibaseEngine.createEngine(Constants.BASE_OUTPUT_PATH + service.getName() + "/" + excel.getSchema() + "/" + excel.getName() + ".xlsx", LiquibaseEngineMode.OVERRIDE);
                List<DataGroup> dataGroupList = new LinkedList<>();
                excel.getSheetList().forEach(sheet -> {
                    DataGroup dataGroup = new DataGroup();
                    dataGroup.setSheetName(sheet.getDescription());
                    List<Data> dataList = new LinkedList<>();
                    sheet.getTableList().forEach(table -> {
                        LOGGER.info("主表：[{}]，SQL：{}", table.getName(), table.getSql());
                        String where = ofNullable(StringUtils.substringAfter(table.getSql(), "WHERE")).orElse("1=1");
                        if (StringUtils.isNotBlank(where) && !StringUtils.equals("1=1", where.replaceAll(" ", ""))) {
                            LOGGER.info("[where]条件：{}", where);
                        }
                        List<String> columns = Arrays
                                .asList(StringUtils.substringBetween(table.getSql(), "SELECT", " FROM ").replaceAll(" ", "").split(","));
                        String id = table.getId();

                        String cited = table.getCited();
                        List<Lang> langs = table.getLangs();
                        List<Reference> references = table.getReferences();
                        Data data = new Data();
                        // 数据封装
                        data.setSchemaName(excel.getSchema());
                        data.setTableName(table.getName());
                        data.setAuthor(Constants.AUTHOR);
                        data.setDescription(table.getDescription());
                        data.setWhere(where);
                        List<Column> columnList = new ArrayList<>();
                        columns.forEach(columnItem -> {
                            Column column = new Column();
                            column.setColumnName(columnItem);
                            if (StringUtils.equals(columnItem, id)) {
                                column.setAutoGenerate(true);
                            }
                            if (StringUtils.equals(TAG + columnItem, id)) {
                                column.setId(true);
                            }
                            if (StringUtils.equals(columnItem, cited)) {
                                column.setCited(true);
                            }
                            if (table.getUnique() != null) {
                                List<String> uniques = Arrays.asList(table.getUnique().replaceAll(" ", "").split(","));
                                if (uniques.contains(columnItem)) {
                                    column.setUnique(true);
                                }
                            }
                            langs.forEach(lang -> {
                                if (lang != null && columnItem.equals(lang.getField())) {
                                    column.setLang(Constants.LANG);
                                    column.setPkName(lang.getPkName());
                                }
                            });
                            references.forEach(reference -> {
                                if (reference != null && columnItem.equals(reference.getField())) {
                                    // 没有获取sheetName属性
                                    column.setReference(reference.getReference());
                                }
                            });
                            columnList.add(column);
                        });
                        data.setColumnList(columnList);
                        dataList.add(data);
                    });
                    dataGroup.setDataList(dataList);
                    dataGroupList.add(dataGroup);
                });
                liquibaseEngine.setDataGroupList(dataGroupList);
                liquibaseEngine.generate();
            });
        });
    }

}
