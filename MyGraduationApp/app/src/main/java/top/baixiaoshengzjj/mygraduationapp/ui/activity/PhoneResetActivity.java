package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.view.CountdownView;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.aop.DebugLog;
import top.baixiaoshengzjj.mygraduationapp.aop.SingleClick;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.helper.InputTextHelper;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetPhoneCodeApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.PhoneApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.PhoneOtpCodeBean;
import top.baixiaoshengzjj.mygraduationapp.other.IntentKey;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERID;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 设置手机号
 */
public final class PhoneResetActivity extends MyActivity {

    @DebugLog
    public static void start(Context context, String code) {
        Intent intent = new Intent(context, PhoneResetActivity.class);
        intent.putExtra(IntentKey.CODE, code);
        context.startActivity(intent);
    }

    private EditText mPhoneView;
    private EditText mCodeView;
    private CountdownView mCountdownView;
    private Button mCommitView;

    /** 验证码 */
    private String otpCode;

    @Override
    protected int getLayoutId() {
        return R.layout.phone_reset_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_phone_reset_phone);
        mCodeView = findViewById(R.id.et_phone_reset_code);
        mCountdownView = findViewById(R.id.cv_phone_reset_countdown);
        mCommitView = findViewById(R.id.btn_phone_reset_commit);
        setOnClickListener(mCountdownView, mCommitView);

        InputTextHelper.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        otpCode = getString(IntentKey.CODE);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v == mCountdownView) {

            if (mPhoneView.getText().toString().length() != 11) {
                toast(R.string.common_phone_input_error);
                return;
            }

            if (true) {
                toast(R.string.common_code_send_hint);
                mCountdownView.start();
                return;
            }

            // 获取验证码
            EasyHttp.post(this)
                    .api(new GetPhoneCodeApi()
                            .setPhone(mPhoneView.getText().toString()))
                    .request(new HttpCallback<HttpData<PhoneOtpCodeBean>>(this) {

                        @Override
                        public void onSucceed(HttpData<PhoneOtpCodeBean> data) {
                            otpCode = data.getData().getOtpCode();
                            toast(R.string.common_code_send_hint);
                            mCountdownView.start();

                        }
                    });
        } else if (v == mCommitView) {

            //判断是否为手机号
            if (mPhoneView.getText().toString().length() != 11) {
                toast(R.string.common_phone_input_error);
                return;
            }

            if (TextUtils.equals(otpCode, mCodeView.getText().toString())) {
                ToastUtils.show(R.string.common_code_error_hint);
                return;
            }else {
                toast(R.string.phone_reset_commit_succeed);
                finish();
            }

            // 更换手机号
            EasyHttp.post(this)
                    .api(new PhoneApi()
                            .setuId(USERID)
                            .setPhone(mPhoneView.getText().toString())
                            .setCode(mCodeView.getText().toString()))
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            toast(R.string.phone_reset_commit_succeed);
                            finish();
                        }
                    });
        }
    }
}