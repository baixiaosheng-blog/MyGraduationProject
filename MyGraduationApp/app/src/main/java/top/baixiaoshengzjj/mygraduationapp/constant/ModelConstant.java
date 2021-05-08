package top.baixiaoshengzjj.mygraduationapp.constant;

/**
 *全局常量
 * @author 百xiao生
 * @date 2021/3/28 22:41
 * @param  * @param null
 * @return
*/
public class ModelConstant {

    public static  Boolean AUTOLOGIN = true;           //自动登录(默认自动登录)
    public static  Boolean BTNCHECK = true;           //自动登录按钮(默认自动登录)
    public static final String LOGIN_INFO = "login_info";   //保存自动登录信息
    public static String PASSWORDSTRENGTH = "弱";           //密码强度
    public static Integer IDENTITY = 1;                     //用户身份（1代表普通用户，2代表护士，3代表医生），默认为1
    public static Integer USERID;                           //用户ID
    public static String USERPHONE;                        //用户手机号
    public static String USEREMAIL;                         //用户邮箱
    public static String USERMANE;                         //用户姓名
    public static Integer USERGENDER;                       //用户性别(1代表男性，0代表女性)
    public static Integer USERAGE;                          //用户年龄
    public static final String CONFIG = "config";           // SharedPreferences 配置文件名
    public static final String FIRST_LOGIN = "first_login"; // 首次进入引导页判断
}
