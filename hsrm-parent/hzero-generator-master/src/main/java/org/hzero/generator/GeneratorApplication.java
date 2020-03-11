package org.hzero.generator;

import org.hzero.generator.util.XmlUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * HZero代码生成器
 *
 * @author xianzhi.chen@hand-china.com	2018年6月19日下午2:16:50
 * @version 1.0
 * @name org.hzero.generator.GeneratorApplication
 * @description
 */
@SpringBootApplication
@EnableConfigurationProperties
public class GeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeneratorApplication.class, args);
        // 解析xml文件
        XmlUtils.resolver();
    }
}
