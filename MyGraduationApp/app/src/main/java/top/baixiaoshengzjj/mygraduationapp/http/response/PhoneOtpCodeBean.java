package top.baixiaoshengzjj.mygraduationapp.http.response;

/**
 *功能描述 手机验证码返回参数
 * @author 百xiao生
 * @date 2021/4/15 22:44
 * @param  * @param null
 * @return
*/
public class PhoneOtpCodeBean {
    private String phone;
    private String otpCode;



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
