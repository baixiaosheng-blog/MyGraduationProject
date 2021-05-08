package top.baixiaoshengzjj.mygraduationapp.ui.activity;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.helper.InputTextHelper;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.LoginApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.UserInfoBean;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.WaitDialog;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.IDENTITY;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.PASSWORDSTRENGTH;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USEREMAIL;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERGENDER;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERID;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERMANE;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERPHONE;

public class SignInActivity extends MyActivity {

    //private Button signChangeBtn;
    private Button signChooseBtn;
    private Button signLoginBtn;
    private EditText signLoginETPass;
    private EditText signLoginETUsr;
    private RelativeLayout signLoginReLayout;
    private ImageView signLoginWechat;
    private ImageView signLoginQQ;
    private TextView signForgetpass;
    private RelativeLayout signinReLayoutUsr;
    private RelativeLayout signinReLayoutPass;
    private Dialog mDialog;
    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    MyLoadingDialog.closeDialog(mDialog);
//                    Intent intentSigninToMain = new Intent(SignInActivity.this, MainActivity.class);
//                    startActivity(intentSigninToMain);
//                    Toast.makeText(SignInActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
//                    finish();
//                    break;
//                case 0:
//                    MyLoadingDialog.closeDialog(mDialog);
//                    Toast.makeText(SignInActivity.this,"登录失败！",Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
    private ObjectAnimator animator3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);          // 隐藏标题栏
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.signin_up;
    }

    @Override
    protected void initView() {
        signChooseBtn = (Button) findViewById(R.id.signin_btn_chose);
        signLoginBtn = (Button) findViewById(R.id.signin_btn_login);
        signForgetpass = findViewById(R.id.signin_tv_forgetpass);
        signLoginETUsr = (EditText) findViewById(R.id.login_et_usr);
        signLoginETPass = (EditText) findViewById(R.id.login_et_pass);
        signLoginQQ = (ImageView) findViewById(R.id.signin_iv_qq);
        signLoginWechat = (ImageView) findViewById(R.id.signin_iv_wechat);
        signLoginReLayout = (RelativeLayout) findViewById(R.id.signin_relayout);
        signinReLayoutPass = (RelativeLayout) findViewById(R.id.signin_relayout_pass);
        signinReLayoutUsr = (RelativeLayout) findViewById(R.id.signin_relayout_usr);

        setOnClickListener(signChooseBtn, signLoginBtn, signLoginQQ,
                signLoginWechat, signForgetpass);

        InputTextHelper.with(this)
                .addView(signLoginETUsr)
                .addView(signLoginETPass)
                .setMain(signLoginBtn)
                .build();

        //动态申请权限
        checkNeedPermissions();
    }

    @Override
    protected void initData() {

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.signin_btn_chose:     //登录界面 --> 注册界面 按钮

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this, signChooseBtn, "choseBtn");
                    startActivity(new Intent(SignInActivity.this, SignUpActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                }
                break;

            case R.id.signin_btn_login:     //登录按钮
                Log.i("登录", "onClick: +++++++登录登录登录");


//                Intent intent = null;
//                switch (IDENTITY){
//                    case 3:
//                        intent = new Intent(SignInActivity.this, DoctorMainActivity.class);
//                        break;
//                    case 2:
//                        intent = new Intent(SignInActivity.this, NurseMainActivity.class);
//                        break;
//                    default:
//                        intent = new Intent(SignInActivity.this, PatientMainActivity.class);
//                        break;
//                }
//                if (intent != null) {
//                    startActivity(intent);
//                    finish();
//                }

                Login();
        }
    }

    /**
     * 登录方法
     * @author 百xiao生
     * @date 2021/4/8 22:39
     * @param  * @param
     * @return void
    */
    private void Login() {
        String userEmail = signLoginETUsr.getText().toString();
        String userPassword = signLoginETPass.getText().toString();
        BaseDialog waitDialog = new WaitDialog.Builder(this)    //等待对话框
                // 消息文本可以不用填写
                .setMessage("登录中...")
                .show();

        EasyHttp.post(this)
                .api(new LoginApi()
                        .setEmail(userEmail)
                        .setPassword(userPassword)
                        .setType("login"))
                .request(new HttpCallback<HttpData<UserInfoBean>>(this) {
                    @Override
                    public void onSucceed(HttpData<UserInfoBean> data) {
                        String encryptedPassword = null;
                        try {
                            encryptedPassword = EncodeByMd5(userPassword);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        //全局常量赋值，用于其他功能，如查询病历，修改密码之类
                        USERID = data.getData().getUid();
                        IDENTITY = data.getData().getIdentity();
                        USERMANE = data.getData().getName();
                        USEREMAIL = data.getData().getEmail();
                        USERGENDER = data.getData().getGender();
                        USERPHONE = data.getData().getTelephone();
                        PASSWORDSTRENGTH = checkPassword(userPassword);

                        sp = getSharedPreferences("login_info", MODE_PRIVATE);
                        editor = sp.edit();
                        editor.putString("email", userEmail);
                        editor.putString("encryptedPassword", encryptedPassword);
                        if (editor.commit()){
                            EMClient.getInstance().login(data.getData().getUid().toString(),userPassword,new EMCallBack() {//回调
                                @Override
                                public void onSuccess() {
                                    EMClient.getInstance().groupManager().loadAllGroups();
                                    EMClient.getInstance().chatManager().loadAllConversations();
                                    Log.d("环信登录", "登录聊天服务器成功！");
                                }

                                @Override
                                public void onProgress(int progress, String status) {

                                }

                                @Override
                                public void onError(int code, String message) {
                                    Log.d("环信登录", "登录聊天服务器失败！");
                                }
                            });
                            toast("登录成功！");
                            postDelayed(waitDialog::dismiss, 0);
                            Intent intent = null;
                            switch (IDENTITY){
                                case 3:
                                    intent = new Intent(SignInActivity.this, DoctorMainActivity.class);
                                    break;
                                case 2:
                                    intent = new Intent(SignInActivity.this, NurseMainActivity.class);
                                    break;
                                default:
                                    intent = new Intent(SignInActivity.this, PatientMainActivity.class);
                                    break;
                            }
                            if (intent != null) {
                                startActivity(intent);
                                finish();
                            }

                        }else {
                            toast("账号密码保存失败，请重新登录");
                        }

                    }
                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        postDelayed(waitDialog::dismiss, 0);
                    }
                });
    }

    /**
     * MD5加密+BASE64编码
     *
     * @return 加密后字符串
     */
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        BASE64Encoder base64en = new BASE64Encoder();
//        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        //Android 8才能使用Base64类，所以Android 8以下版本不能进行自动登录
        Base64.Encoder base64en = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            base64en = Base64.getEncoder();
            String newstr = base64en.encodeToString(md5.digest(str.getBytes("utf-8")));
            return newstr;
        }
        return str;
    }

    /**
     *功能描述 动态申请权限
     * @author 百xiao生
     * @date 2021/4/13 13:40
     * @param  * @param
     * @return void
    */
    private void checkNeedPermissions() {
        //6.0以上需要动态申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            //多个权限一起申请
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO
            }, 1);
        }
    }

    /**
     * 密码强度
     *
     * @return Z = 字母 S = 数字 T = 特殊字符
     */

	/*  一、假定密码字符数范围6-16，除英文数字和字母外的字符都视为特殊字符：
    弱：^[0-9A-Za-z]{6,16}$
    中：^(?=.{6,16})[0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*$
    强：^(?=.{6,16})([0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*){2,}$
    二、假定密码字符数范围6-16，密码字符允许范围为ASCII码表字符：
    弱：^[0-9A-Za-z]{6,16}$
    中：^(?=.{6,16})[0-9A-Za-z]*[\x00-\x2f\x3A-\x40\x5B-\xFF][0-9A-Za-z]*$
    强：^(?=.{6,16})([0-9A-Za-z]*[\x00-\x2F\x3A-\x40\x5B-\xFF][0-9A-Za-z]*){2,}$*/
    public static String checkPassword(String passwordStr) {
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            return "弱";
        }
        if (passwordStr.matches(regexS)) {
            return "弱";
        }
        if (passwordStr.matches(regexT)) {
            return "弱";
        }
        if (passwordStr.matches(regexZT)) {
            return "中";
        }
        if (passwordStr.matches(regexST)) {
            return "中";
        }
        if (passwordStr.matches(regexZS)) {
            return "中";
        }
        if (passwordStr.matches(regexZST)) {
            return "强";
        }
        return passwordStr;

    }

}