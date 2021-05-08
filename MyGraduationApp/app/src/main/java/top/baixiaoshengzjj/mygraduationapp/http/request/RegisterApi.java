package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;

/**
 *  用户注册
 */
public final class RegisterApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/register";
    }

    /** 邮箱 */
    @HttpRename("email")
    private String email;
    /** 验证码 */
    @HttpRename("otpCode")
    private String code;
    /** 密码 */
    @HttpRename("password")
    private String password;
//    /** 昵称 */
//    @HttpRename("name")
//    private String name;
//    /** 年龄 */
//    @HttpRename("age")
//    private String age;
//    /** 性别 */
//    @HttpRename("gender")
//    private String gender;
//    /** 电话 */
//    @HttpRename("telephone")
//    private String telephone;

    public RegisterApi setEmail(String email) {
        this.email = email;
        return this;
    }

    public RegisterApi setCode(String code) {
        this.code = code;
        return this;
    }

    public RegisterApi setPassword(String password) {
        this.password = password;
        return this;
    }

//    public RegisterApi setName(String name) {
//        this.name = name;
//        return this;
//    }
//
//    public RegisterApi setAge(String age) {
//        this.age = age;
//        return this;
//    }
//
//    public RegisterApi setGender(String gender) {
//        this.gender = gender;
//        return this;
//    }
//
//    public RegisterApi setTelephone(String telephone) {
//        this.telephone = telephone;
//        return this;
//    }
}