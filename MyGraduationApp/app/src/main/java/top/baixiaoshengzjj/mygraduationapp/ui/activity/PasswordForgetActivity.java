package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.view.CountdownView;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.aop.SingleClick;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.helper.InputTextHelper;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetCodeApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.MailOtpCodeBean;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 忘记密码
 */
public final class PasswordForgetActivity extends MyActivity {

    private EditText mEmailView;
    private EditText mCodeView;
    private CountdownView mCountdownView;
    private Button mCommitView;
    private String otpCode;

    @Override
    protected int getLayoutId() {
        return R.layout.password_forget_activity;
    }

    @Override
    protected void initView() {
        mEmailView = findViewById(R.id.et_password_forget_email);
        mCodeView = findViewById(R.id.et_password_forget_code);
        mCountdownView = findViewById(R.id.cv_password_forget_countdown);
        mCommitView = findViewById(R.id.btn_password_forget_commit);
        setOnClickListener(mCountdownView, mCommitView);

        InputTextHelper.with(this)
                .addView(mEmailView)
                .addView(mCodeView)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v == mCountdownView) {
            if (mEmailView.getText().toString().length() != 11) {
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
                    .api(new GetCodeApi()
                            .setEmail(mEmailView.getText().toString()))
                    .request(new HttpCallback<HttpData<MailOtpCodeBean>>(this) {

                        @Override
                        public void onSucceed(HttpData<MailOtpCodeBean> data) {
                            otpCode = data.getData().getOtpCode();
                            toast(R.string.common_code_send_hint);
                            mCountdownView.start();
                        }
                    });
        } else if (v == mCommitView) {

            if (mEmailView.getText().toString().length() != 11) {
                toast(R.string.common_email_input_error);
                return;
            }

            //验证码本地校验
            if (!TextUtils.equals(otpCode, mCodeView.getText().toString())) {
                ToastUtils.show(R.string.common_code_error_hint);
                return;
            }else {
                PasswordResetActivity.start(getActivity(), mEmailView.getText().toString(), mCodeView.getText().toString());
                finish();
            }

            // 验证码校验
//            EasyHttp.post(this)
//                    .api(new VerifyCodeApi()
//                            .setPhone(mEmailView.getText().toString())
//                            .setCode(mCodeView.getText().toString()))
//                    .request(new HttpCallback<HttpData<Void>>(this) {
//
//                        @Override
//                        public void onSucceed(HttpData<Void> data) {
//                            PasswordResetActivity.start(getActivity(), mEmailView.getText().toString(), mCodeView.getText().toString());
//                            finish();
//                        }
//                    });
        }
    }
}