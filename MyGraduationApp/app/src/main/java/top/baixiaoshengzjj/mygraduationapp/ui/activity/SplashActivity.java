package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.LoginApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.UserInfoBean;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.AUTOLOGIN;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.IDENTITY;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.PASSWORDSTRENGTH;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USEREMAIL;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERGENDER;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERID;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERMANE;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERPHONE;
import static top.baixiaoshengzjj.mygraduationapp.ui.activity.SignInActivity.checkPassword;

/**
 *  闪屏界面
 */
public final class SplashActivity extends MyActivity {

    //private LottieAnimationView mLottieView;
    private Handler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.splash_activity;
    }

    @Override
    protected void initView() {
        //mLottieView = findViewById(R.id.iv_splash_lottie);

        // 设置动画监听
//        mLottieView.addAnimatorListener(new AnimatorListenerAdapter() {
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                startActivity(MainActivity.class);
//                finish();
//            }
//        });
    }

    @Override
    protected void initData() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }else if (msg.what == 2) {
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, PatientMainActivity.class);
                    startActivity(intent);
                    finish();
                }else if (msg.what == 3) {
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, NurseMainActivity.class);
                    startActivity(intent);
                    finish();
                }else if (msg.what == 4) {
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, DoctorMainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };

        Log.d("自动登录", "自动登录:"+AUTOLOGIN);
        if (!AUTOLOGIN) {
            //不自动登录
            mHandler.postDelayed(mRunnable, 2000);
        }else {
            SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
            String userEmail = sp.getString("email", "");
            String userPassword = sp.getString("encryptedPassword", "");

            Log.i("TAG", "邮箱: "+userEmail + " 密码："+userPassword);
            EasyHttp.post(this)
                    .api(new LoginApi()
                            .setEmail(userEmail)
                            .setPassword(userPassword)
                            .setType("autoLogin"))
                    .request(new HttpCallback<HttpData<UserInfoBean>>(this) {
                        @Override
                        public void onSucceed(HttpData<UserInfoBean> data) {
                            USERMANE = data.getData().getName();
                            USERID = data.getData().getUid();
                            USEREMAIL = data.getData().getEmail();
                            USERGENDER = data.getData().getGender();
                            USERPHONE = data.getData().getTelephone();
                            PASSWORDSTRENGTH = checkPassword(userPassword);

                            IDENTITY = data.getData().getIdentity();
                            if (IDENTITY == 1){
                                mHandler.postDelayed(mRunnable2, 2000);
                            }else if (IDENTITY == 2){
                                mHandler.postDelayed(mRunnable3, 2000);
                            }else if (IDENTITY == 3){
                                mHandler.postDelayed(mRunnable4, 2000);
                            }

                        }

                        @Override
                        public void onFail(Exception e) {
                            mHandler.postDelayed(mRunnable, 2000);
                        }

                    });

        }




    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            mHandler.sendEmptyMessage(1);
        }
    };

    private Runnable mRunnable2 = new Runnable() {
        public void run() {
            mHandler.sendEmptyMessage(2);
        }
    };

    private Runnable mRunnable3 = new Runnable() {
        public void run() {
            mHandler.sendEmptyMessage(3);
        }
    };

    private Runnable mRunnable4 = new Runnable() {
        public void run() {
            mHandler.sendEmptyMessage(4);
        }
    };

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 隐藏状态栏和导航栏
                .hideBar(BarHide.FLAG_HIDE_BAR);
    }

    @Override
    public void onBackPressed() {
        //禁用返回键
        //super.onBackPressed();
    }

    @Override
    public boolean isSwipeEnable() {
        return false;
    }

    @Override
    protected void onDestroy() {
        //mLottieView.removeAllAnimatorListeners();
        super.onDestroy();
    }
}