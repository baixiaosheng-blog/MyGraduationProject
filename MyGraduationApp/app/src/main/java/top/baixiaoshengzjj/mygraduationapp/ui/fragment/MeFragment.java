package top.baixiaoshengzjj.mygraduationapp.ui.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.SwitchButton;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.aop.SingleClick;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.common.MyFragment;
import top.baixiaoshengzjj.mygraduationapp.helper.ActivityStackManager;
import top.baixiaoshengzjj.mygraduationapp.helper.CacheDataManager;
import top.baixiaoshengzjj.mygraduationapp.http.glide.GlideApp;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.LogoutApi;
import top.baixiaoshengzjj.mygraduationapp.other.AppConfig;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.BrowserActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.PasswordResetActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.PersonalDataActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.PhoneResetActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.SignInActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.MenuDialog;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.SafeByMailDialog;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.SafeByPhoneDialog;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.UpdateDialog;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.AUTOLOGIN;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.BTNCHECK;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.PASSWORDSTRENGTH;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERPHONE;

public class MeFragment extends MyFragment<MyActivity> implements SwitchButton.OnCheckedChangeListener {

    private SettingBar minformationView;
    private SettingBar mLanguageView;
    private SettingBar mPhoneView;
    private SettingBar mPasswordView;
    private SettingBar mCleanCacheView;
    private SwitchButton mAutoSwitchView;



    @Override
    protected int getLayoutId() {
        return R.layout.frag_me;
    }

    @Override
    protected void initView() {
        minformationView = (SettingBar) findViewById(R.id.sb_setting_information);
        mLanguageView = (SettingBar) findViewById(R.id.sb_setting_language);
        mPhoneView = (SettingBar) findViewById(R.id.sb_setting_phone);
        mPasswordView = (SettingBar) findViewById(R.id.sb_setting_password);
        mCleanCacheView = (SettingBar) findViewById(R.id.sb_setting_cache);
        mAutoSwitchView = (SwitchButton) findViewById(R.id.sb_setting_switch);
        mAutoSwitchView.setChecked(BTNCHECK);
        // 设置切换按钮的监听
        mAutoSwitchView.setOnCheckedChangeListener(this);

        setOnClickListener(R.id.sb_setting_information, R.id.sb_setting_language, R.id.sb_setting_update,
                R.id.sb_setting_phone, R.id.sb_setting_password, R.id.sb_setting_agreement, R.id.sb_setting_about,
                R.id.sb_setting_cache, R.id.sb_setting_auto, R.id.sb_setting_exit);

    }

    @Override
    protected void initData() {

        // 获取应用缓存大小
        mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(getAttachActivity()));

        mLanguageView.setRightText("简体中文");
        // 为了保护用户的隐私，不明文显示中间四个数字
        Log.i("手机号", "initData: 手机号为"+USERPHONE);
        if (USERPHONE == null){
            mPhoneView.setRightText("");
        } else if (USERPHONE.length() < 11){
            mPhoneView.setRightText(USERPHONE.toString());
        }else {
            mPhoneView.setRightText(String.format("%s****%s", USERPHONE.substring(0, 3), USERPHONE.substring(USERPHONE.length() - 4)));
        }
        mPasswordView.setRightText(PASSWORDSTRENGTH);
        ToastUtils.init(getAttachActivity().getApplication());
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sb_setting_information:
                startActivity(PersonalDataActivity.class);
                break;

            case R.id.sb_setting_language:
                // 底部选择框
                new MenuDialog.Builder(getAttachActivity())
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setList(R.string.setting_language_simple, R.string.setting_language_complex)
                        .setListener((MenuDialog.OnListener<String>) (dialog, position, string) -> {
                            mLanguageView.setRightText(string);
                            BrowserActivity.start(getAttachActivity(), "https://github.com/getActivity/MultiLanguages");
                        })
                        .setGravity(Gravity.BOTTOM)
                        .setAnimStyle(BaseDialog.ANIM_BOTTOM)
                        .show();
                break;
            case R.id.sb_setting_update:
                //检测服务器是否有新版本
                //检测新版本请求代码
                //获取版本更新信息，版本名、优化信息等

                // 本地的版本码和服务器的进行比较
                if (2 > AppConfig.getVersionCode()) {

                        new UpdateDialog.Builder(getAttachActivity().getContext())
                                // 版本名
                                .setVersionName("2.0")
                                // 是否强制更新
                                .setForceUpdate(false)
                                // 更新日志
                                .setUpdateLog("修复Bug\n优化用户体验")

                                // 下载 url
                                //要VPN.setDownloadUrl("https://raw.githubusercontent.com/baixiaosheng-blog/MaterialLogin/main/%E8%84%91%E5%8D%92%E4%B8%AD%E7%BB%BF%E8%89%B2%E9%80%9A%E9%81%932.0_2021_05_03.apk")
                                //要登录.setDownloadUrl("https://gitee.com/baixiaoshengzjj/graduation-project/raw/master/%E8%84%91%E5%8D%92%E4%B8%AD%E7%BB%BF%E8%89%B2%E9%80%9A%E9%81%932.0_2021_05_03.apk")
                                .setDownloadUrl("http://101.201.255.67:8888/down/JQQZRSivUsQk")
                                .show();

                } else {
                    toast(R.string.update_no_update);
                }
                break;
            case R.id.sb_setting_phone:
                if (USERPHONE.length() == 0){
                    //为空，即之前没绑定手机号，直接跳转绑定手机界面
                    startActivity(new Intent(getAttachActivity(), PhoneResetActivity.class));
                }else {
                    new SafeByPhoneDialog.Builder(getAttachActivity())
                            .setListener((dialog, phone, code) -> PhoneResetActivity.start(getAttachActivity(), code))
                            .show();
                }
                break;
            case R.id.sb_setting_password:
                new SafeByMailDialog.Builder(getAttachActivity())
                        .setListener((dialog, phone, code) -> PasswordResetActivity.start(getAttachActivity(), phone, code))
                        .show();
                break;
            case R.id.sb_setting_agreement:
                BrowserActivity.start(getAttachActivity(), "https://gitee.com/baixiaoshengzjj/my-graduation-project");
                break;
            case R.id.sb_setting_about:
                BrowserActivity.start(getAttachActivity(), "https://baixiaoshengzjj.top");
                //startActivity(AboutActivity.class);
                break;
            case R.id.sb_setting_auto:
                // 自动登录
                mAutoSwitchView.setChecked(!mAutoSwitchView.isChecked());



                break;
            case R.id.sb_setting_cache:
                // 清除内存缓存（必须在主线程）
                GlideApp.get(getAttachActivity()).clearMemory();
                new Thread(() -> {
                    CacheDataManager.clearAllCache(getAttachActivity());
                    // 清除本地缓存（必须在子线程）
                    GlideApp.get(getAttachActivity()).clearDiskCache();
                    post(() -> {
                        // 重新获取应用缓存大小
                        mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(getAttachActivity()));
                    });
                }).start();
                break;
            case R.id.sb_setting_exit:
                if (true) {
                    //手动退出登录，关闭自动登录设置
                    AUTOLOGIN = false;
                    BTNCHECK = false;
                    startActivity(SignInActivity.class);
                    //退出环信，否则切换环信账号失败
                    EMClient.getInstance().logout(true, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Log.i("TAG", "onSuccess: 退出环信登录成功");
                        }

                        @Override
                        public void onError(int code, String error) {
                            Log.i("TAG", "onError: 退出环信登录失败");
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }
                    });
                    // 进行内存优化，销毁除登录页之外的所有界面
                    ActivityStackManager.getInstance().finishAllActivities(SignInActivity.class);
                    return;
                }

                // 退出登录
                EasyHttp.post(this)
                        .api(new LogoutApi())
                        .request(new HttpCallback<HttpData<Void>>(this) {

                            @Override
                            public void onSucceed(HttpData<Void> data) {
                                startActivity(SignInActivity.class);
                                // 进行内存优化，销毁除登录页之外的所有界面
                                ActivityStackManager.getInstance().finishAllActivities(SignInActivity.class);
                            }
                        });
                break;
            default:
                break;
        }
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
        Base64.Encoder base64en = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            base64en = Base64.getEncoder();
            String newstr = base64en.encodeToString(md5.digest(str.getBytes("utf-8")));
            return newstr;
        }
        return str;
    }


    @Override
    public void onCheckedChanged(SwitchButton button, boolean isChecked) {
        button.setChecked(isChecked);
        BTNCHECK = !BTNCHECK;
        AUTOLOGIN = !AUTOLOGIN;

    }
}
