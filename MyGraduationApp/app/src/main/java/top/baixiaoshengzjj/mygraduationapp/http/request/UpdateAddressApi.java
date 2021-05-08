package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 * 获取邮箱验证码
 * */
public final class UpdateAddressApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/update/updateAddress";
    }

    /** 用户id */
    private Integer uId;
    /** 地区 */
    private String address;

    public UpdateAddressApi setUid(Integer  uId) {
        this.uId = uId;
        return this;
    }

    public UpdateAddressApi setAddress(String  address) {
        this.address = address;
        return this;
    }
}