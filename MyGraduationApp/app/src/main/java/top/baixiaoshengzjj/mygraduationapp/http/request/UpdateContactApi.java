package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 * 获取邮箱验证码
 * */
public final class UpdateContactApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/update/updateContact";
    }

    /** 用户id */
    private Integer uId;
    /** 联系人 */
    private String contact;

    public UpdateContactApi setUid(Integer  uId) {
        this.uId = uId;
        return this;
    }

    public UpdateContactApi setContact(String  contact) {
        this.contact = contact;
        return this;
    }
}