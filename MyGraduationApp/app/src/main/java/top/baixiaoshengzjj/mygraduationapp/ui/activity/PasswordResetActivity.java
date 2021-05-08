package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.aop.DebugLog;
import top.baixiaoshengzjj.mygraduationapp.aop.SingleClick;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.helper.InputTextHelper;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.PasswordApi;
import top.baixiaoshengzjj.mygraduationapp.other.IntentKey;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERID;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 重置密码
 */
public final class PasswordResetActivity extends MyActivity {

    @DebugLog
    public static void start(Context context, String email, String code) {
        Intent intent = new Intent(context, PasswordResetActivity.class);
        intent.putExtra(IntentKey.EMAIL, email);
        intent.putExtra(IntentKey.CODE, code);
        context.startActivity(intent);
    }

    private EditText mPasswordView1;
    private EditText mPasswordView2;
    private Button mCommitView;

    /** 邮箱 */
    private String mEmail;
    /** 手机号 */
    private String mPhoneNumber;
    /** 验证码 */
    private String mVerifyCode;

    @Override
    protected int getLayoutId() {
        return R.layout.password_reset_activity;
    }

    @Override
    protected void initView() {
        mPasswordView1 = findViewById(R.id.et_password_reset_password1);
        mPasswordView2 = findViewById(R.id.et_password_reset_password2);
        mCommitView = findViewById(R.id.btn_password_reset_commit);
        setOnClickListener(mCommitView);

        InputTextHelper.with(this)
                .addView(mPasswordView1)
                .addView(mPasswordView2)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        mPhoneNumber = getString(IntentKey.EMAIL);
        mVerifyCode = getString(IntentKey.CODE);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v == mCommitView) {

            if (!mPasswordView1.getText().toString().equals(mPasswordView2.getText().toString())) {
                toast(R.string.common_password_input_unlike);
                return;
            }

            // 重置密码
            EasyHttp.post(this)
                    .api(new PasswordApi()
                            .setUId(USERID)
                            //.setCode(mVerifyCode)
                            .setPassword(mPasswordView1.getText().toString()))
                    .request(new HttpCallback<HttpData<String>>(this) {

                        @Override
                        public void onSucceed(HttpData<String> data) {
                            toast(R.string.password_reset_success);
                            finish();
                        }
                    });
        }
    }
}