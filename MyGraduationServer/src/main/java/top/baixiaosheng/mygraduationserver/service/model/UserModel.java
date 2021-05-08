package top.baixiaosheng.mygraduationserver.service.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 因为业务需要，有些字段并不能在同一张表里
 * model层就是整合业务需要的所有字段
 */
public class UserModel {
    private Integer uid;
    //@NotBlank(message = "用户名不能为空")
    private String name;
    //@NotNull(message = "必须填写性别")
    private Integer gender;
    //@NotNull(message = "必须填写年龄")
    //@Min(value = 0, message = "年龄必须大于0")
    //@Max(value = 150, message = "年龄必须小于150")
    private Integer age;
    //@NotNull(message = "手机号不能为空")
    private String telephone;
    private String registMode;
    private Integer identity;
    private String thirdPartId;
    /* 整合加密后的密码字段 */
    @NotNull(message = "密码不能为空")
    private String encryptPassword;

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public String getRegisterMode() {
        return registMode;
    }

    public void setRegisterMode(String registMode) {
        this.registMode = registMode;
    }

    public String getThirdPartId() {
        return thirdPartId;
    }

    public void setThirdPartId(String thirdPartId) {
        this.thirdPartId = thirdPartId;
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = encryptPassword;
    }
}
