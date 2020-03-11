package org.hzero.generator.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hzero.generator.entity.Mapping;
import org.hzero.generator.liquibase.LiquibaseExecutor;
import org.hzero.generator.service.ImportDataService;
import org.hzero.generator.util.DBConfigUtils;
import org.hzero.generator.util.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description 导入数据实现
 * @Date 2019/12/16 15:04
 * @Author wanshun.zhang@hand-china.com
 */
@Service
public class ImportDataServiceImpl implements ImportDataService {

    @Autowired
    DBConfigUtils dbConfigUtils;
    /**
     * 服务列表
     */
    private List<Mapping> mappingList = XmlUtils.mappingList;
    private LiquibaseExecutor liquibaseExecutor = new LiquibaseExecutor();

    /**
     * 获取服务列表
     *
     * @param dir 文件路径
     * @return 服务列表
     */
    @Override
    public List<Mapping> getDataServices(String dir) {
        File file = new File(dir);
        List<Mapping> mappings = null;
        if (file.isDirectory()) {
            List<String> fileNames = Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(f -> !f.isFile()).map(File::getName).collect(Collectors.toList());
            mappings = mappingList.stream().filter(mapping -> fileNames.contains(mapping.getName())).collect(Collectors.toList());
        }
        return mappings;
    }

    /**
     * 获取服务列表
     *
     * @param dir 文件路径
     * @return 服务列表
     */
    @Override
    public List<Mapping> getGroovyServices(String dir) {
        File file = new File(dir);
        List<Mapping> mappings = null;
        if (file.isDirectory()) {
            List<String> fileNames = Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(f -> !f.isFile()).map(File::getName).collect(Collectors.toList());
            mappings = mappingList.stream().filter(mapping -> fileNames.contains(mapping.getSchema())).collect(Collectors.toList());
        }
        return mappings;
    }

    /**
     * 导入数据和更新脚本
     *
     * @param services 服务列表
     * @param dir      文件路径
     * @param env      环境
     */
    @Override
    public void importData(List<String> services, String dir, String env) {
        services.forEach(service -> {
            String rootPath = dir + "/" + service;
            File file = new File(rootPath);
            if (file.isDirectory()) {
                List<String> schemaList = Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(f -> !f.isFile()).map(File::getName).collect(Collectors.toList());
                schemaList.forEach(schema -> executor(rootPath + "/" + schema, env, schema));
            }
        });
    }

    @Override
    public void updateGroovy(List<String> services, String dir, String env) {
        services.forEach(service -> {
            executor(dir + "/" + service, env, service);
        });
    }

    public void executor(String dir, String env, String schema) {
        final Map<String, String> dataSource = dbConfigUtils.getMapByEnv(env);
        String url = dataSource.get("url");
        String username = dataSource.get("username");
        String password = dataSource.get("password");
        liquibaseExecutor.setDsUrl(StringUtils.replace(url, "?", "/" + schema + "?"));
        liquibaseExecutor.setDsUserName(username);
        liquibaseExecutor.setDsPassword(password);
        liquibaseExecutor.setDefaultDir(dir);
        liquibaseExecutor.execute();
    }
}
