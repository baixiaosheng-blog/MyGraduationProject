package top.baixiaoshengzjj.mygraduationapp.constant;

/**
 *功能描述
 * @author 百xiao生
 * @date 2021/3/21 14:30
 * @param  * @param null
 * @return
*/

public class NetConstant {
    public static final String baseService = "http://101.201.255.67:8090/";     //后端地址

    private static final String getOtpCodeURL       = "user/getOtp";               //获取otp验证码的URL
    private static final String loginURL            = "user/login";                //登录URL
    private static final String registerURL         = "user/register";             //注册URL

    public static String getBaseService() {
        return baseService;
    }

    public static String getGetOtpCodeURL() {
        return getOtpCodeURL;
    }

    public static String getLoginURL() {
        return loginURL;
    }

    public static String getRegisterURL() {
        return registerURL;
    }
}
