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
import top.baixiaoshengzjj.mygraduationapp.http.request.GetPhoneCodeApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.PhoneOtpCodeBean;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERPHONE;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/02/06
 *    desc   : 身份校验对话框
 */
public final class SafeByPhoneDialog {

    public static final class Builder
            extends UIDialog.Builder<Builder> {

        //private final TextView mPhoneView;
        private final TextView mEmailView;
        private final EditText mCodeView;
        private final CountdownView mCountdownView;

        private OnListener mListener;

        /** 当前手机号 */
        private final String mPhoneNumber;

        private String otpCode;

        public Builder(Context context) {
            super(context);
            setTitle(R.string.safe_title);
            setCustomView(R.layout.safe_byphone_dialog);
            mEmailView = findViewById(R.id.tv_safe_phone);
            mCodeView = findViewById(R.id.et_safe_code);
            mCountdownView = findViewById(R.id.cv_safe_countdown);
            setOnClickListener(mCountdownView);

            mPhoneNumber = USERPHONE;

            // 为了保护用户的隐私，不明文显示中间四个数字
            if (mPhoneNumber.length() < 11){
                mEmailView.setText(mPhoneNumber);
            }else {
                mEmailView.setText(String.format("%s****%s", mPhoneNumber.substring(0, 3), mPhoneNumber.substring(mPhoneNumber.length() - 4)));
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
                    if (true) {
                        ToastUtils.show(R.string.common_code_send_hint);
                        mCountdownView.start();
                        setCancelable(false);
                        return;
                    }

                    // 获取验证码
                    EasyHttp.post(this)
                            .api(new GetPhoneCodeApi()
                                    .setPhone(mPhoneNumber))
                            .request(new OnHttpListener<HttpData<PhoneOtpCodeBean>>() {

                                @Override
                                public void onSucceed(HttpData<PhoneOtpCodeBean> data) {
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
                        return;
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