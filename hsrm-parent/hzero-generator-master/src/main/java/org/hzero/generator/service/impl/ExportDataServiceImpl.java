package org.hzero.generator.service.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.lang3.StringUtils;
import org.hzero.generator.dto.ServiceDTO;
import org.hzero.generator.entity.Excel;
import org.hzero.generator.entity.Sheet;
import org.hzero.generator.service.ExportDataService;
import org.hzero.generator.service.IDBDiffService;
import org.hzero.generator.service.InitDataInfoService;
import org.hzero.generator.util.DBConfigUtils;
import org.hzero.generator.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 导出初始化脚本service类
 *
 * @author wanshun.zhang@hand-china.com
 */
@Service
public class ExportDataServiceImpl extends ClassLoaderResourceAccessor implements ExportDataService {

    @Autowired
    IDBDiffService idbDiffService;
    @Autowired
    InitDataInfoService initDataInfoService;
    @Autowired
    DataSource dataSources;
    private Logger LOGGER = LoggerFactory.getLogger(ExportDataServiceImpl.class);
    @Autowired
    DBConfigUtils dbConfigUtils;

    /**
     * 获取当前环境可导出的数据
     *
     * @param env 环境
     * @param version
     * @return
     */
    @Override
    public List<ServiceDTO> getExportServices(String env, String version) {
        // 查询当前环境存在的数据库列表
        List<String> databaseList = idbDiffService.selectDatabase(env);
        // 构建传输对象列表
        List<ServiceDTO> list = new ArrayList<>();
        // 页面复选框是否禁选
        AtomicBoolean disabled = new AtomicBoolean(false);
        // 过滤版本
        String purge = StringUtils.equals(version,"op") ? "SAAS版" : "OP版";
        // 遍历解析出的xml服务，构建传输对象
        XmlUtils.serviceList.forEach(service -> {
            ServiceDTO serviceDTO = new ServiceDTO();
            serviceDTO.setId(service.getName());
            serviceDTO.setField(service.getName());
            serviceDTO.setOrder(service.getOrder());
            serviceDTO.setTitle(service.getDescription());
            List<ServiceDTO.Children> childrenList = new ArrayList<>();
            service.getExcelList().forEach(excel -> {
                if (!databaseList.contains(excel.getSchema())) {
                    disabled.set(true);
                } else {
                    disabled.set(false);
                    serviceDTO.setDisabled(false);
                }
                ServiceDTO.Children children = new ServiceDTO.Children();
                children.setId(excel.getName());
                children.setField(excel.getName());
                children.setTitle(disabled.get() ? excel.getDescription() + " <<< 当前环境没有此数据" : excel.getDescription());
                children.setDisabled(disabled.get());
                List<ServiceDTO.Children> grandChildrens = new ArrayList<>();
                excel.getSheetList().forEach(sheet -> {
                    if (!StringUtils.contains(sheet.getDescription(),purge)) {
                        ServiceDTO.Children grandChildren = new ServiceDTO.Children();
                        grandChildren.setId(sheet.getName());
                        grandChildren.setField(sheet.getName());
                        grandChildren.setTitle(sheet.getDescription());
                        grandChildren.setDisabled(disabled.get());
                        grandChildrens.add(grandChildren);
                    }
                });
                children.setChildren(grandChildrens);
                childrenList.add(children);
            });
            serviceDTO.setChildren(childrenList);
            list.add(serviceDTO);
        });
        list.sort(Comparator.comparing(ServiceDTO::getOrder));
        return list;
    }

    /**
     * 导出数据
     * @param env 环境
     * @param serviceList 需要导出的服务列表
     */
    @Override
    public void exportInitData(@RequestParam String env, @RequestBody List<ServiceDTO> serviceList) {
        List<org.hzero.generator.entity.Service> list = XmlUtils.serviceList;
        List<org.hzero.generator.entity.Service> exportList = new ArrayList<>();
        list.forEach(service -> {
            serviceList.forEach(serviceDTO -> {
                if (StringUtils.equals(service.getName(), serviceDTO.getField())) {
                    org.hzero.generator.entity.Service exportService = new org.hzero.generator.entity.Service();
                    exportService.setName(service.getName());
                    exportService.setOrder(service.getOrder());
                    exportService.setDescription(service.getDescription());
                    List<Excel> excels = new ArrayList<>();
                    service.getExcelList().forEach(excel -> serviceDTO.getChildren().forEach(children -> {
                        if (StringUtils.equals(excel.getName(), children.getField())) {
                            Excel exportExcel = new Excel();
                            exportExcel.setName(excel.getName());
                            exportExcel.setFileName(excel.getFileName());
                            exportExcel.setSchema(excel.getSchema());
                            exportExcel.setDescription(excel.getDescription());
                            List<Sheet> sheets = new ArrayList<>();
                            excel.getSheetList().forEach(sheet -> children.getChildren().forEach(grandChildren -> {
                                if (StringUtils.equals(sheet.getName(), grandChildren.getField())) {
                                    sheets.add(sheet);
                                }
                            }));
                            exportExcel.setSheetList(sheets);
                            excels.add(exportExcel);
                        }
                    }));
                    exportService.setExcelList(excels);
                    exportList.add(exportService);
                }
            });
        });

        switch (env) {
            case InitDataInfoService.ENV_DEV:
                LOGGER.info("使用环境：dev");
                initDataInfoService.exportDevData(exportList);
                break;
            case InitDataInfoService.ENV_TST:
                LOGGER.info("使用环境：tst");
                initDataInfoService.exportTstData(exportList);
                break;
            case InitDataInfoService.ENV_UAT:
                LOGGER.info("使用环境：uat");
                initDataInfoService.exportUatData(exportList);
                break;
            case InitDataInfoService.ENV_PRD:
                LOGGER.info("使用环境：prd");
                initDataInfoService.exportPrdData(exportList);
                break;
            default:
                LOGGER.info("没有配置此环境...");
                break;
        }
    }
}
