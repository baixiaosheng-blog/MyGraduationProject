package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 * 获取邮箱验证码
 * */
public final class UpdateAgeApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/update/updateAge";
    }

    /** 用户id */
    private Integer uId;
    /** 姓名 */
    private Integer age;

    public UpdateAgeApi setUid(Integer  uId) {
        this.uId = uId;
        return this;
    }

    public UpdateAgeApi setAge(Integer  age) {
        this.age = age;
        return this;
    }
}