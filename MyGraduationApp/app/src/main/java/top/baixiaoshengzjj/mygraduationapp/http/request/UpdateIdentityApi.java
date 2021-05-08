package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 * 获取邮箱验证码
 * */
public final class UpdateIdentityApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/update/updateIdentity";
    }

    /** 用户id */
    private Integer uId;
    /** 身份 */
    private Integer identity;

    public UpdateIdentityApi setUid(Integer  uId) {
        this.uId = uId;
        return this;
    }

    public UpdateIdentityApi setIdentity(Integer  identity) {
        this.identity = identity;
        return this;
    }
}