package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 用户登录
 */
public final class LoginApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/login";
    }

    /** 邮箱 */
    @HttpRename("email")
    private String email;
    /** 登录密码 */
    @HttpRename("password")
    private String password;
    /** 登录类型 */
    @HttpRename("type")
    private String type;

    public LoginApi setEmail(String email) {
        this.email = email;
        return this;
    }

    public LoginApi setPassword(String password) {
        this.password = password;
        return this;
    }

    public LoginApi setType(String type) {
        this.type = type;
        return this;
    }
}