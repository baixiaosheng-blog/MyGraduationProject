package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 * 获取邮箱验证码
 * */
public final class UpdateGenderApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/update/updateGender";
    }

    /** 用户id */
    private Integer uId;
    /** 性别 */
    private Integer gender;

    public UpdateGenderApi setUid(Integer  uId) {
        this.uId = uId;
        return this;
    }

    public UpdateGenderApi setGender(Integer  gender) {
        this.gender = gender;
        return this;
    }
}