package org.hzero.generator.service;

import java.util.List;
import org.hzero.generator.entity.Service;

/**
 * 描述:
 *
 * @author wanshun.zhang@hand-china.com
 * @date 2019/12/04 17:58
 */
public interface InitDataInfoService {

    /**
     * 开发环境
     */
    String ENV_DEV = "dev";
    /**
     * 测试环境
     */
    String ENV_TST = "tst";
    /**
     * UAT环境
     */
    String ENV_UAT = "uat";
    /**
     * 生产环境
     */
    String ENV_PRD = "prd";

    /**
     * 开发环境
     *
     * @param serviceList 需要导出的列表
     */
    void exportDevData(List<Service> serviceList);

    /**
     * 测试环境
     *
     * @param serviceList 需要导出的列表
     */
    void exportTstData(List<Service> serviceList);

    /**
     * UAT环境
     *
     * @param serviceList 需要导出的列表
     */
    void exportUatData(List<Service> serviceList);

    /**
     * 生产环境
     *
     * @param serviceList 需要导出的列表
     */
    void exportPrdData(List<Service> serviceList);
}
