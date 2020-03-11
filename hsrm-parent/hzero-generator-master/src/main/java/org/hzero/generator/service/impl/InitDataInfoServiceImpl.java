package org.hzero.generator.service.impl;

import java.util.List;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.hzero.generator.service.InitDataInfoService;
import org.hzero.generator.util.ScriptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述:
 *
 * @author wanshun.zhang@hand-china.com
 * @date 2019/12/04 18:03
 */
@Service
public class InitDataInfoServiceImpl implements InitDataInfoService {

    @Autowired
    ScriptUtils scriptUtils;

    @DS(ENV_DEV)
    @Override
    public void exportDevData(List<org.hzero.generator.entity.Service> serviceList) {
        scriptUtils.create(serviceList);
    }

    @DS(ENV_TST)
    @Override
    public void exportTstData(List<org.hzero.generator.entity.Service> serviceList) {
        scriptUtils.create(serviceList);
    }

    @DS(ENV_UAT)
    @Override
    public void exportUatData(List<org.hzero.generator.entity.Service> serviceList) {
        scriptUtils.create(serviceList);
    }

    @DS(ENV_PRD)
    @Override
    public void exportPrdData(List<org.hzero.generator.entity.Service> serviceList) {
        scriptUtils.create(serviceList);
    }
}
