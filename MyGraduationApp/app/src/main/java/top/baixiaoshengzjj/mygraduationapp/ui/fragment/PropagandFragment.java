package top.baixiaoshengzjj.mygraduationapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.SwitchButton;

import top.baixiaoshengzjj.mygraduationapp.R;

public class PropagandFragment extends Fragment {

    private SettingBar mLanguageView;
    private SettingBar mPhoneView;
    private SettingBar mPasswordView;
    private SettingBar mCleanCacheView;
    private SwitchButton mAutoSwitchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.frag_propagand, null);
        // bind view

        initView(view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void initView(View view) {




//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.sb_setting_language:
//                        Toast.makeText(getActivity(),"点击了！！！",Toast.LENGTH_SHORT).show();
//                    break;
//                    case R.id.sb_setting_language:
//                        // 底部选择框
//                        new MenuDialog.Builder(this)
//                                // 设置点击按钮后不关闭对话框
//                                //.setAutoDismiss(false)
//                                .setList(R.string.setting_language_simple, R.string.setting_language_complex)
//                                .setListener((MenuDialog.OnListener<String>) (dialog, position, string) -> {
//                                    mLanguageView.setRightText(string);
//                                    BrowserActivity.start(getActivity(), "https://github.com/getActivity/MultiLanguages");
//                                })
//                                .setGravity(Gravity.BOTTOM)
//                                .setAnimStyle(BaseDialog.ANIM_BOTTOM)
//                                .show();
//                        break;
//                    case R.id.sb_setting_update:
//                        // 本地的版本码和服务器的进行比较
//                        if (20 > AppConfig.getVersionCode()) {
//                            new UpdateDialog.Builder(this)
//                                    // 版本名
//                                    .setVersionName("2.0")
//                                    // 是否强制更新
//                                    .setForceUpdate(false)
//                                    // 更新日志
//                                    .setUpdateLog("修复Bug\n优化用户体验")
//                                    // 下载 url
//                                    .setDownloadUrl("https://raw.githubusercontent.com/getActivity/AndroidProject/master/AndroidProject.apk")
//                                    .show();
//                        } else {
//                            toast(R.string.update_no_update);
//                        }
//                        break;
//                    case R.id.sb_setting_phone:
//                        new SafeDialog.Builder(this)
//                                .setListener((dialog, phone, code) -> PhoneResetActivity.start(getActivity(), code))
//                                .show();
//                        break;
//                    case R.id.sb_setting_password:
//                        new SafeDialog.Builder(this)
//                                .setListener((dialog, phone, code) -> PasswordResetActivity.start(getActivity(), phone, code))
//                                .show();
//                        break;
//                    case R.id.sb_setting_agreement:
//                        BrowserActivity.start(this, "https://github.com/getActivity/Donate");
//                        break;
//                    case R.id.sb_setting_about:
//                        startActivity(AboutActivity.class);
//                        break;
//                    case R.id.sb_setting_auto:
//                        // 自动登录
//                        mAutoSwitchView.setChecked(!mAutoSwitchView.isChecked());
//                        break;
//                    case R.id.sb_setting_cache:
//                        // 清除内存缓存（必须在主线程）
//                        GlideApp.get(getActivity()).clearMemory();
//                        new Thread(() -> {
//                            CacheDataManager.clearAllCache(this);
//                            // 清除本地缓存（必须在子线程）
//                            GlideApp.get(getActivity()).clearDiskCache();
//                            post(() -> {
//                                // 重新获取应用缓存大小
//                                mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(getActivity()));
//                            });
//                        }).start();
//                        break;
//                    case R.id.sb_setting_exit:
//                        if (true) {
//                            startActivity(LoginActivity.class);
//                            // 进行内存优化，销毁除登录页之外的所有界面
//                            ActivityStackManager.getInstance().finishAllActivities(LoginActivity.class);
//                            return;
//                        }
//
//                        // 退出登录
//                        EasyHttp.post(this)
//                                .api(new LogoutApi())
//                                .request(new HttpCallback<HttpData<Void>>(this) {
//
//                                    @Override
//                                    public void onSucceed(HttpData<Void> data) {
//                                        startActivity(LoginActivity.class);
//                                        // 进行内存优化，销毁除登录页之外的所有界面
//                                        ActivityStackManager.getInstance().finishAllActivities(LoginActivity.class);
//                                    }
//                                });
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
    }



}
