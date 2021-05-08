package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;

/**
 * 获取邮箱验证码(注册)
 * */
public final class GetCodeApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/getOtp";
    }

    /** 邮箱 */
    @HttpRename("email")
    private String email;

    public GetCodeApi setEmail(String  email) {
        this.email = email;
        return this;
    }
}