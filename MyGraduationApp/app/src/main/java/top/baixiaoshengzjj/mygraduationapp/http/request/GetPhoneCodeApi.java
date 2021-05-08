package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;

/**
 *功能描述 获取手机验证码
 * @author 百xiao生
 * @date 2021/4/15 22:40
 * @param  * @param null
 * @return
*/
public class GetPhoneCodeApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/getPhoneOtp";
    }

    /** 手机号 */
    @HttpRename("phone")
    private String phone;

    public GetPhoneCodeApi setPhone(String  phone) {
        this.phone = phone;
        return this;
    }
}
