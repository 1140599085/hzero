package org.hzero.generator.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.hzero.generator.entity.*;
import org.hzero.generator.export.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 描述:解析xml文件
 *
 * @author wanshun.zhang@hand-china.com
 * @date 2019/12/02 16:01
 */


@Component
public class XmlUtils {

    public static final List<Service> serviceList = new ArrayList<>();
    public static final List<Mapping> mappingList = new ArrayList<>();
    private static final Map<String,String> mappingMap = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);
    /**
     * 需要过滤掉的文件
     */
    private static List<String> SKIP_FILE = new ArrayList<>();



    static {
        // 忽略模板文件
        SKIP_FILE.add("template-vacancy.xml");
        SKIP_FILE.add("template-demo.xml");
    }


    /**
     * 扫描解析xml文件
     */
    public static void resolver() {
        File mapping = new File(Constants.MAPPING_FILE);
        File file = new File(Constants.XML_PATH);
        try {
            createMapping(mapping);
            mappingList.forEach(m-> mappingMap.put(m.getName(),m.getDescription()));
            handleFile(file);
        } catch (DocumentException e) {
            LOGGER.error("解析XML文件出错 >>>>> {}", e.getMessage());
        }
    }

    private static void handleFile(File file) throws DocumentException {
        //处理目录
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    handleFile(subFile);
                }
            }
        } else {
            // 过滤忽略文件
            if (SKIP_FILE.contains(file.getName())) {
                return;
            }
            Service service = createService(file);
            if (serviceList.size() > 0) {
                for (int i = 0; i < serviceList.size(); i++) {
                    // 服务合并
                    if (StringUtils.equals(serviceList.get(i).getName(), service.getName())) {
                        serviceList.get(i).getExcelList().addAll(service.getExcelList());
                        return;
                    }
                }
                serviceList.add(service);
            } else {
                serviceList.add(service);
            }
        }
    }

    /**
     * 解析xml文件封装成Service
     *
     * @param file
     * @return Service
     * @throws DocumentException
     */
    private static Service createService(File file) throws DocumentException {
        LOGGER.info("开始解析文件：{}",file.getName());
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element rootElement = document.getRootElement();
        Service service = new Service();
        service.setName(rootElement.attributeValue("name"));
        service.setOrder(Integer.parseInt(rootElement.attributeValue("order")));
        service.setDescription(rootElement.attributeValue("description"));
        service.setDescription(mappingMap.get(service.getName()));
        List<Excel> excels = new ArrayList<>();
        rootElement.elements().forEach(excelElement -> {
            Excel excel = new Excel();
            excel.setName(((DefaultElement) excelElement).attributeValue("name"));
            excel.setFileName(((DefaultElement) excelElement).attributeValue("fileName"));
            excel.setDescription(((DefaultElement) excelElement).attributeValue("description"));
            excel.setSchema(((DefaultElement) excelElement).attributeValue("schema"));
            List<Sheet> sheets = new ArrayList<>();
            ((DefaultElement) excelElement).elements().forEach(sheetElement -> {
                Sheet sheet = new Sheet();
                sheet.setName(((DefaultElement) sheetElement).attributeValue("name"));
                sheet.setDescription(((DefaultElement) sheetElement).attributeValue("description"));
                List<Table> tables = new ArrayList<>();
                ((DefaultElement) sheetElement).elements().forEach(tableElement -> {
                    Table table = new Table();
                    table.setName(((DefaultElement) tableElement).attributeValue("name"));
                    table.setDescription(((DefaultElement) tableElement).attributeValue("description"));
                    table.setId(((DefaultElement) tableElement).elementTextTrim("id"));
                    table.setSql(((DefaultElement) tableElement).elementTextTrim("sql").trim());
                    table.setCited(((DefaultElement) tableElement).elementTextTrim("cited"));
                    table.setUnique(((DefaultElement) tableElement).elementTextTrim("unique"));
                    List<Reference> references = new ArrayList<>();
                    List<Lang> langs = new ArrayList<>();
                    ((DefaultElement) tableElement).elements().forEach(tableContent -> {
                        String field = ((DefaultElement) tableContent).elementTextTrim("field");
                        Lang lang = new Lang();
                        lang.setField(field);
                        lang.setPkName(((DefaultElement) tableContent).elementTextTrim("pkName"));
                        if (lang.getPkName() != null) {
                            langs.add(lang);
                        }
                        Reference reference = new Reference();
                        reference.setField(field);
                        reference.setColumnName(((DefaultElement) tableContent).elementTextTrim("columnName"));
                        reference.setSheetName(((DefaultElement) tableContent).elementTextTrim("sheetName"));
                        reference.setTableName(((DefaultElement) tableContent).elementTextTrim("tableName"));
                        if (reference.getColumnName() != null) {
                            references.add(reference);
                        }
                    });
                    table.setLangs(langs);
                    table.setReferences(references);
                    tables.add(table);
                });
                sheet.setTableList(tables);
                sheets.add(sheet);
            });
            excel.setSheetList(sheets);
            excels.add(excel);
        });
        service.setExcelList(excels);
        return service;
    }

    /**
     * 解析xml映射文件封装成Mapping
     *
     * @param file
     * @return Service
     * @throws DocumentException
     */
    private static void createMapping(File file) throws DocumentException {
        LOGGER.info("开始解析文件：{}",file.getName());
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element rootElement = document.getRootElement();
        rootElement.elements().forEach(element ->{
            Mapping mapping = new Mapping();
            mapping.setName(((DefaultElement) element).attributeValue("name"));
            mapping.setSchema(((DefaultElement) element).attributeValue("schema"));
            mapping.setDescription(((DefaultElement) element).attributeValue("description"));
            mappingList.add(mapping);
        });
    }
}
