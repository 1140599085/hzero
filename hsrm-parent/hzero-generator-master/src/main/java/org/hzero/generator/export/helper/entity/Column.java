package org.hzero.generator.export.helper.entity;

import org.hzero.generator.export.helper.supporter.CellData;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 数据列对应的表列
 * </p>
 *
 * @author qingsheng.chen 2018/11/24 星期六 11:02
 */
public class Column {
    private String columnName;
    private String pkName;
    private boolean autoGenerate = false;
    private boolean id = false;
    private boolean unique = false;
    private boolean cited = false;
    private List<String> lang;
    private String type;
    private ReferenceColumn reference;

    public boolean isFormula() {
        return reference != null;
    }

    public boolean isMultiLang() {
        return !CollectionUtils.isEmpty(lang);
    }

    public CellData.CellStyle getCellStyle() {
        if (autoGenerate) {
            return CellData.CellStyle.ORANGE;
        }
        if (id) {
            return CellData.CellStyle.ORANGE;
        }
        if (unique) {
            return CellData.CellStyle.BLUE;
        }
        if (isFormula()) {
            return CellData.CellStyle.GREEN;
        }
        return null;
    }

    public String getColumnNameText() {
        StringBuilder sb = new StringBuilder();
        if (autoGenerate) {
            sb.append("*").append(columnName);
        }else if(id) {
            sb.append("*").append(columnName);
        }else if (unique) {
            sb.append("#").append(columnName);
        } else {
            sb.append(columnName);
        }
        if (StringUtils.hasText(type)) {
            sb.append("(").append(type).append(")");
        }
        return sb.toString();
    }

    public String getColumnName() {
        return columnName;
    }

    public Column setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public boolean isAutoGenerate() {
        return autoGenerate;
    }

    public Column autoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
        return this;
    }

    public Column setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
        return this;
    }

    public boolean isId() {
        return id;
    }

    public Column id(boolean id){
        this.id = id;
        return this;
    }
    public Column setId(boolean id) {
        this.id = id;
        return this;
    }


    public String getPkName() {
        return pkName;
    }

    public Column setPkName(String pkName) {
        this.pkName = pkName;
        return this;
    }

    public boolean isUnique() {
        return unique;
    }

    public Column setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    public List<String> getLang() {
        return lang;
    }

    public Column setLang(List<String> lang) {
        this.lang = lang;
        return this;
    }

    public ReferenceColumn getReference() {
        return reference;
    }

    public Column setReference(ReferenceColumn reference) {
        this.reference = reference;
        return this;
    }

    public boolean isCited() {
        return cited;
    }

    public Column setCited(boolean cited) {
        this.cited = cited;
        return this;
    }

    public boolean isRef() {
        return reference != null;
    }

    public String getType() {
        return type;
    }

    public Column setType(String type) {
        this.type = type;
        return this;
    }
}
