package org.hzero.generator.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * description
 *
 * @author wanshun.zhang@hand-china.com
 * @date 2019/11/11 20:01
 */
@Mapper
public interface InitDataMapper {
    /**
     *
     * 查询数据库
     *
     * @return
     */
    List<String> selectDatabase();
}
