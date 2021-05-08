package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 * 获取邮箱验证码
 * */
public final class UpdateFulladdressApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/update/updateFulladdress";
    }

    /** 用户id */
    private Integer uId;
    /** 详细地址 */
    private String fulladdress;

    public UpdateFulladdressApi setUid(Integer  uId) {
        this.uId = uId;
        return this;
    }

    public UpdateFulladdressApi setFulladdress(String  fulladdress) {
        this.fulladdress = fulladdress;
        return this;
    }
}