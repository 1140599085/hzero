package org.hzero.generator.export.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置
 *
 * @author zhangwanshun 2019/12/4
 */
public class Constants {

    /**
     * <p>
     * 临时导出路径
     * </p>
     * 
     * @author zhangwanshun 2019/12/11 10:34
     */
    public static final String BASE_OUTPUT_PATH = "src/main/resources/data/";

    /**
     * <p>
     * xml文件存放路径
     * </p>
     *
     * @author zhangwanshun 2019/12/13 09:51
     */
    public static final String XML_PATH = "src/main/resources/xml/services";

    /**
     * <p>
     * 服务映射文件存放路径
     * </p>
     *
     * @author zhangwanshun 2019/12/17 09:51
     */
    public static final String MAPPING_FILE = "src/main/resources/xml/mapping/service-mapping.xml";
    /**
     * <p>
     * 导出作者
     * </p>
     * 
     * @author zhangwanshun 2019/12/11 10:34
     */
    public static final String AUTHOR = "hzero";
    /**
     * <p>
     * 多语言列表
     * </p>
     * 
     * @author zhangwanshun 2019/12/11 10:34
     */
    public static List<String> LANG = new ArrayList<>();

    static {
        LANG.add("zh_CN");
        LANG.add("en_US");
    }
}
