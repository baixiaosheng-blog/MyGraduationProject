package top.baixiaoshengzjj.mygraduationapp.ui.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.king.zxing.CameraScan;
import com.king.zxing.CaptureActivity;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyFragment;
import top.baixiaoshengzjj.mygraduationapp.helper.InputTextHelper;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.ChatActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.NurseMainActivity;

import static android.app.Activity.RESULT_OK;

public class InfoInputFragment extends MyFragment<NurseMainActivity> {

    private String t = "体温：";
    private String p = "血压：";
    private String s = "血氧：";
    private String h = "心率：";
    private String r = "呼吸：";

    private EditText tempura;       //体温输入框
    private ImageView scan;         //扫码录入图标
    private EditText pressure;      //血压输入框
    private EditText spo2;          //血氧输入框
    private EditText heartRate;     //心率输入框
    private EditText RR;            //呼吸输入框
    private Button submit;          //提交按钮

    @Override
    protected int getLayoutId() {
        return R.layout.frag_infoinput;
    }

    @Override
    protected void initView() {
        tempura = (EditText) findViewById(R.id.infoinput_et_temp);
        scan = (ImageView) findViewById(R.id.infoinput_iv_scan);
        pressure = (EditText) findViewById(R.id.infoinput_et_pressure);
        spo2 = (EditText) findViewById(R.id.infoinput_et_SpO2);
        heartRate = (EditText) findViewById(R.id.infoinput_et_heartrate);
        RR = (EditText) findViewById(R.id.infoinput_et_RR);
        submit = (Button) findViewById(R.id.infoinput_btn_submit);
        setOnClickListener(scan, submit);

        InputTextHelper.with(getAttachActivity())
                .addView(tempura)
                .addView(pressure)
                .addView(spo2)
                .addView(heartRate)
                .addView(RR)
                .setMain(submit)
                .build();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.infoinput_iv_scan:    //扫码录入业务逻辑
                //new com.google.zxing.integration.android.IntentIntegrator(getAttachActivity()).initiateScan();
                startActivityForResult(new Intent(getAttachActivity(), CaptureActivity.class), 1);

                break;

            case R.id.infoinput_btn_submit: //提交按钮逻辑
                nurseSubmit();


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Log.i("扫码", "onActivityResult: 扫码成功！");
            toast("扫码成功！\n");
            String s = CameraScan.parseScanResult(data);
            String[] strings = s.split("\n");
            tempura   .setText(strings[0]);
            pressure  .setText(strings[1]);
            spo2      .setText(strings[2]);
            heartRate .setText(strings[3]);
            RR        .setText(strings[4]);

        }
    }
    /**
     * 功能描述 提交测得的生理参数
     *
     * @param * @param
     * @return void
     * @author 百xiao生
     * @date 2021/4/11 18:30
     */
    private void nurseSubmit() {
        String mtempura = tempura.getText().toString();
        String mpressure = pressure.getText().toString();
        String mspo2 = spo2.getText().toString();
        String mheartRate = heartRate.getText().toString();
        String mRR = RR.getText().toString();

        String physiologicalParameters = t + mtempura + "\n"
                + p + mpressure + "\n"
                + s + mspo2 + "\n"
                + h + mheartRate + "\n"
                + r + mRR + "\n";

        EMMessage message = EMMessage.createTxtSendMessage(physiologicalParameters, "145220081614849");
        //如果是群聊，设置chattype，默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);

        Intent intent = new Intent(getAttachActivity().getContext(), ChatActivity.class);
//        Intent intent = new Intent(getAttachActivity().getContext(), TeamActivity.class);
//        intent.putExtra("nurseSubmit", physiologicalParameters);
        startActivity(intent);
    }
}
