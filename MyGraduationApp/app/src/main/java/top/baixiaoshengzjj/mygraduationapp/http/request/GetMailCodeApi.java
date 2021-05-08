package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 *功能描述 获取邮箱验证码
 * @author 百xiao生
 * @date 2021/4/15 22:40
 * @param  * @param null
 * @return
*/
public class GetMailCodeApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/getMailOtp";
    }

    /** 邮箱 */
    private String email;

    public GetMailCodeApi setEmail(String  email) {
        this.email = email;
        return this;
    }
}
