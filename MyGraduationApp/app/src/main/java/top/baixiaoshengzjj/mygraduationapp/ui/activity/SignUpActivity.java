package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.view.CountdownView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.helper.InputTextHelper;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetCodeApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.RegisterApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.UserInfoBean;
import top.baixiaoshengzjj.mygraduationapp.other.IntentKey;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.WaitDialog;

public class SignUpActivity extends MyActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private LinearLayout signupLinearLayout;
    private EditText signupuserPasswordET;
    private EditText signupPassET;
    private EditText signupuserET;
    private Button signupBtn;
    private Button signChooseBtn;
    private EditText signupOtpET;
    private CountdownView signupOtpCV;
    private Dialog mDialog;

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    MyLoadingDialog.closeDialog(mDialog);
//                    Toast.makeText(SignUpActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
////                    Intent signupToSigninintent = new Intent(SignUpActivity.this, SignInActivity.class);
////                    startActivity(signupToSigninintent);
////                    finish();
//                    animateRevealClose();
//                    break;
//                case 0:
//                    MyLoadingDialog.closeDialog(mDialog);
//                    Toast.makeText(SignUpActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
//                    break;
//                case 2:
//                    MyLoadingDialog.closeDialog(mDialog);
//                    Toast.makeText(SignUpActivity.this, "用户已存在！", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };


    @Override
    protected int getLayoutId() {
        return R.layout.signup_page;
    }

    @Override
    protected void initView() {
        signChooseBtn = (Button) findViewById(R.id.signup_btn_chose);
        signupBtn = (Button) findViewById(R.id.sign_btn_signup);
        signupuserET = (EditText) findViewById(R.id.signup_et_usr);
        signupOtpCV = (CountdownView) findViewById(R.id.signup_cv_countdown);
        signupOtpET = (EditText) findViewById(R.id.signup_et_otp);
        signupPassET = (EditText) findViewById(R.id.signup_et_pass);
        signupuserPasswordET = (EditText) findViewById(R.id.signup_et_surepass);
        signupLinearLayout = (LinearLayout) findViewById(R.id.signup_lilayout);
        setOnClickListener(signChooseBtn,signupBtn,signupOtpCV);

        //检测输入框状态，设置注册按钮是否可点击
        InputTextHelper.with(this)
                .addView(signupuserET)
                .addView(signupOtpET)
                .addView(signupPassET)
                .addView(signupuserPasswordET)
                .setMain(signupBtn)
                .build();
        InputTextHelper.with(this)
                .addView(signupuserET)
                .setMain(signupOtpCV)
                .build();
    }

    @Override
    protected void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
    }


    @Override
    public void onClick(View v){

        String userEmail = signupuserET.getText().toString();
        String userPassword = signupPassET.getText().toString();
        String userPassword2 = signupuserPasswordET.getText().toString();
        String otpCode = signupOtpET.getText().toString();
        String regx = "\\w+@\\w+(\\.[a-zA-Z]+)+";
        boolean match = userEmail.matches(regx);
        switch (v.getId()){

            case R.id.signup_btn_chose:     //注册界面 -->登录界面 按钮
                animateRevealClose();
                break;

            case R.id.signup_cv_countdown:  //发送验证码按钮
                //对邮箱判空
                if (userEmail.isEmpty()){
                    toast("邮箱不能为空");
                }else if (!match){
                    toast("请输入正确的邮箱！");
                }else {
                    asyncGetOtpCode(userEmail,signupOtpCV);     //通过邮箱获取验证码
                }
                if (true) {
                    //toast(R.string.common_code_send_hint);
                    signupOtpCV.start();    //发送验证码按钮——>倒计时（禁用60s）

                }


                break;

            case R.id.sign_btn_signup:
                if (otpCode.isEmpty()){
                    toast("请输入验证码！");
                }else if (userEmail.isEmpty()) {    //账号、密码的非空校验
                    toast("请输入用户名！");
                } else if (userPassword.isEmpty()) {
                    toast("请输入密码！");
                } else if (userPassword2.isEmpty()) {
                    toast("请确认密码！");
                } else if (!(userPassword.equals(userPassword2))) {
                    toast("两次输入的密码不一致");
                } else {
                    //mDialog = MyLoadingDialog.showWaitDialog(SignUpActivity.this, "注册中...", false, true);   //注册等待界面
                    asyncRegister(userEmail, otpCode ,userPassword);

                    //mHandler.sendEmptyMessageDelayed(2,1000);   //用户已存在，停止等待动画并给出提示信息
                }
        }
    }


    /**
     *功能描述 用户注册
     * @author 百xiao生
     * @date 2021/3/21 23:19
     * @param  * @param
     * @return void
    */
    private void asyncRegister(String email, String otpCode, String password) {
        BaseDialog waitDialog = new WaitDialog.Builder(this)
                // 消息文本可以不用填写
                .setMessage("注册中...")
                .show();
        EasyHttp.post(this)
                .api(new RegisterApi()
                        .setEmail(email)
                        .setCode(otpCode)
                        .setPassword(password))
                .request(new HttpCallback<HttpData<UserInfoBean>>(this) {
                    @Override
                    public void onSucceed(HttpData<UserInfoBean> data) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    EMClient.getInstance().createAccount(data.getData().getUid().toString(), password);
                                    Log.i("环信注册", "注册环信成功");
                                } catch (HyphenateException e) {
                                    Log.i("环信注册", "环信注册失败: ");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        toast("注册成功！");
                        postDelayed(waitDialog::dismiss, 0);
                        setResult(RESULT_OK, new Intent()
                                .putExtra(IntentKey.EMAIL, email)
                                .putExtra(IntentKey.PASSWORD, password));
                        animateRevealClose();   //注册成功，自动跳转到登录界面
                    }
                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        postDelayed(waitDialog::dismiss, 0);
                    }
                });
    }


    /**
     *功能描述 发送验证码
     * @author 百xiao生
     * @date 2021/4/21 21:34
     * @param  * @param
     * @return void
    */
    private void asyncGetOtpCode(String email, CountdownView countdownView) {
        Log.i("TAG", "发送验证码: ");
        EasyHttp.post(this)
                .api(new GetCodeApi().setEmail(email))
                .request(new HttpCallback<HttpData<String>>(SignUpActivity.this) {
                @Override
                public void onSucceed(HttpData<String> data) {
                  toast(R.string.common_code_send_hint);
                  //mCountdownView.start();
                }

                @Override
                public void onFail(Exception e) {
                    super.onFail(e);
                    //mCountdownView.start();
                }
                });


    }











    /*
    * 登录-注册切换动画的相关方法
    * */
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                signupLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }

        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(signupLinearLayout, signupLinearLayout.getWidth() / 2,
                0, signChooseBtn.getWidth() / 2, signupLinearLayout.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                signupLinearLayout.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(signupLinearLayout, signupLinearLayout.getWidth() / 2,
                0, signupLinearLayout.getHeight(), signChooseBtn.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                signupLinearLayout.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                //fab.setImageResource(R.drawable.plus);
                SignUpActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

//    @NonNull
//    @Override
//    protected ImmersionBar createStatusBarConfig() {
//        return super.createStatusBarConfig()
//                // 不要把整个布局顶上去
//                .keyboardEnable(true);
//    }

}