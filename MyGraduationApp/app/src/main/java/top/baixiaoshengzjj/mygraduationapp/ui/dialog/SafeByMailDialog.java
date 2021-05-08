package top.baixiaoshengzjj.mygraduationapp.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.ToastUtils;
import com.hjq.widget.view.CountdownView;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.aop.SingleClick;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetMailCodeApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.MailOtpCodeBean;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.PasswordResetActivity;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USEREMAIL;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/02/06
 *    desc   : 身份校验对话框
 */
public final class SafeByMailDialog {

    public static final class Builder
            extends UIDialog.Builder<Builder> {

        //private final TextView mPhoneView;
        private final TextView mEmailView;
        private final EditText mCodeView;
        private final CountdownView mCountdownView;

        private OnListener mListener;

        /** 当前手机号 */
        //private final String mPhoneNumber;
        /** 当前邮箱 */
        private final String mEmail;

        private String otpCode;

        public Builder(Context context) {
            super(context);
            setTitle(R.string.safe_title);
            setCustomView(R.layout.safe_bymail_dialog);
            mEmailView = findViewById(R.id.tv_safe_phone);
            mCodeView = findViewById(R.id.et_safe_code);
            mCountdownView = findViewById(R.id.cv_safe_countdown);
            setOnClickListener(mCountdownView);

            //mPhoneNumber = "18100001413";
            // 为了保护用户的隐私，不明文显示中间四个数字
            mEmail = USEREMAIL;
            if (mEmail.length() < 11){
                mEmailView.setText(mEmail);
            }else {
                mEmailView.setText(String.format("%s****%s", mEmail.substring(0, 3), mEmail.substring(mEmail.length() - 4)));
            }
        }

        public Builder setCode(String code) {
            mCodeView.setText(code);
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        @SingleClick
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cv_safe_countdown:

                    //ToastUtils.show(R.string.common_code_send_hint);
                    mCountdownView.start();
                    setCancelable(false);

                    // 获取验证码
                    EasyHttp.post(this)
                            .api(new GetMailCodeApi()
                                    .setEmail(mEmail))
                            .request(new OnHttpListener<HttpData<MailOtpCodeBean>>() {

                                @Override
                                public void onSucceed(HttpData<MailOtpCodeBean> data) {
                                    ToastUtils.show(R.string.common_code_send_hint);
                                    mCountdownView.start();
                                    setCancelable(false);
                                    otpCode = data.getData().getOtpCode();
                                }

                                @Override
                                public void onFail(Exception e) {
                                    ToastUtils.show(e.getMessage());
                                }
                            });
                    break;
                case R.id.tv_ui_confirm:
                    // 验证码校验
                    if (!TextUtils.equals(otpCode, mCodeView.getText().toString())) {
                        ToastUtils.show(R.string.common_code_error_hint);
                    }else {
                        PasswordResetActivity.start(getActivity(), USEREMAIL, otpCode);
                    }

//                    if (true) {
//                        autoDismiss();
//                        if (mListener != null) {
//                            mListener.onConfirm(getDialog(), mEmail, mCodeView.getText().toString());
//                        }
//                        return;
//                    }
//
//
//                    EasyHttp.post(this)
//                            .api(new VerifyCodeApi()
//                                    .setPhone(mPhoneNumber)
//                                    .setCode(mCodeView.getText().toString()))
//                            .request(new OnHttpListener<HttpData<Void>>() {
//
//                                @Override
//                                public void onSucceed(HttpData<Void> data) {
//                                    autoDismiss();
//                                    if (mListener != null) {
//                                        mListener.onConfirm(getDialog(), mPhoneNumber, mCodeView.getText().toString());
//                                    }
//                                }
//
//                                @Override
//                                public void onFail(Exception e) {
//                                    ToastUtils.show(e.getMessage());
//                                }
//                            });
                    break;
                case R.id.tv_ui_cancel:
                    autoDismiss();
                    if (mListener != null) {
                        mListener.onCancel(getDialog());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, String phone, String code);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}
    }
}