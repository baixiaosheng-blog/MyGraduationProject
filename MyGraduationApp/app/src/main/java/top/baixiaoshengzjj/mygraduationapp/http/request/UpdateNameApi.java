package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 * 获取邮箱验证码
 * */
public final class UpdateNameApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/update/updateName";
    }

    /** 用户id */
    private Integer uId;
    /** 姓名 */
    private String name;

    public UpdateNameApi setUid(Integer  uId) {
        this.uId = uId;
        return this;
    }

    public UpdateNameApi setName(String  name) {
        this.name = name;
        return this;
    }
}