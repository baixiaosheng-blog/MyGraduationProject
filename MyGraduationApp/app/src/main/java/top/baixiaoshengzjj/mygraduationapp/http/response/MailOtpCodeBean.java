package top.baixiaoshengzjj.mygraduationapp.http.response;

/**
 *功能描述 邮箱验证码返回
 * @author 百xiao生
 * @date 2021/3/21 14:35
 * @param  * @param null
 * @return
*/
public class MailOtpCodeBean {
    private String email;
    private String otpCode;
    //private String telephone;




    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
