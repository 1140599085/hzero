package org.hzero.generator.service;

import java.util.List;
import org.hzero.generator.dto.ServiceDTO;

/**
 * 生成初始化数据Excel服务接口
 *
 * @author wanshun.zhang@hand-china.com
 */
public interface ExportDataService {

    /**
     * 获取服务列表数据
     * @param env 环境
     * @param version
     * @return
     */
    List<ServiceDTO> getExportServices(String env, String version);

    /**
     * 导出服务数据
     * @param env 环境
     * @param serviceList 需要导出的服务列表
     */
    void exportInitData(String env, List<ServiceDTO> serviceList);
}
