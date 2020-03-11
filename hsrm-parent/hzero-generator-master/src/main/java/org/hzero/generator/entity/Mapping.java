package org.hzero.generator.entity;

/**
 * @Description 服务映射
 * @Date 2019/12/17 20:41
 * @Author wanshun.zhang@hand-china.com
 */
public class Mapping {

    private String name;
    private String schema;
    private String description;

    public Mapping() {
    }

    public Mapping(String name, String schema, String description) {
        this.name = name;
        this.schema = schema;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
