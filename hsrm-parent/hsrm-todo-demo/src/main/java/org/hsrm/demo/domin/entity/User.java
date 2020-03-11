package org.hsrm.demo.domin.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.core.util.Regexs;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author 张洪涛
 * @create 2020-03-08 17:44
 */
@ApiModel("用户信息") // Swagger 实体描述
@VersionAudit  /*类上启用，插入一条数据版本号加1*/
@ModifyAudit
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name="todo_user")
public class User extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    @Length(max = 30)
    @NotBlank
    @ApiModelProperty("员工姓名")
    private String employeeName;
    @Length(max = 30)
    @NotBlank
    @Pattern(regexp = Regexs.CODE, message = "htdo.warn.user.numberFormatIncorrect") // 格式控制
    @ApiModelProperty("员工编号")
    private String employeeNumber;
    @Length(max = 60)
    @Pattern(regexp = Regexs.EMAIL, message = "htdo.warn.user.emailFormatIncorrect")
    @ApiModelProperty("员工邮箱")
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
