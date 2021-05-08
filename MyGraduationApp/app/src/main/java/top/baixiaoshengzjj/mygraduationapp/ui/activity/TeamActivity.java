package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.helper.InputTextHelper;

public class TeamActivity extends MyActivity implements EMMessageListener {

    private Button msgSend;
    private EditText msgInput;
    private TextView msgShow;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_team;
    }

    @Override
    protected void initView() {
        msgSend = (Button) findViewById(R.id.team_btn_send);
        msgInput = (EditText) findViewById(R.id.team_et_input);
        msgShow = (TextView) findViewById(R.id.team_tv_msgshow);
        setOnClickListener(msgSend);

        InputTextHelper.with(this)
                .addView(msgInput)
                .setMain(msgSend)
                .build();
    }

    @Override
    protected void initData() {
        //        EMConversation conversation = EMClient.getInstance().chatManager().getConversation("145220081614849");
//        //获取此会话的所有消息
//        List<EMMessage> chatRecord = conversation.getAllMessages();
//        for (final EMMessage emMessage : chatRecord) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    msgShow.setText(msgShow.getText()+"\n"+
//                            ((EMTextMessageBody)emMessage.getBody()).getMessage());
//                }
//            });
//        }


        Intent intent = getIntent();
        String nurseSubmit = intent.getStringExtra("nurseSubmit");
        Log.i("TAG", "群聊获得了护士输入的内容: "+nurseSubmit);
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        if (nurseSubmit != null) {
            EMMessage message = EMMessage.createTxtSendMessage(nurseSubmit, "145220081614849");
            //如果是群聊，设置chattype，默认是单聊
            message.setChatType(EMMessage.ChatType.GroupChat);
            //发送消息
            EMClient.getInstance().chatManager().sendMessage(message);
            updateUI(nurseSubmit);
        }


        //加载聊天记录
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation("145220081614849");
        List<EMMessage> chatRecord = conversation.loadMoreMsgFromDB("0", 20);
        Log.i("聊天记录", "initData: "+chatRecord.toString());
        for (final EMMessage emMessage : chatRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgShow.setText(msgShow.getText()+"\n"+
                            ((EMTextMessageBody)emMessage.getBody()).getMessage());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        String msg = msgInput.getText().toString();




        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(msg, "145220081614849");
        //如果是群聊，设置chattype，默认是单聊
            message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i("环信", "onSuccess: 发送消息成功");

                //需要在UI线程中更新ui，否则会报错，当前activity崩溃返回上一层
                updateUI(msg);

            }

            @Override
            public void onError(int code, String error) {
                Log.i("环信", "onError: 发送消息失败");
                toast("发送消息失败");
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });


    }


    //
    private void updateUI (String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //清空输入框内容
                msgInput.setText("");
                //显示发送的短信
                msgShow.setText(msgShow.getText() + "\n"
                        + "我：" + msg+"\n");
            }
        });
    }


    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }


    /**
     * 消息的各个回调方法
     * @author
     * @date 2021/4/10 17:01
     * @param  * @param messages
     * @return void
    */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        //收到消息
        for (final EMMessage emMessage : messages) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgShow.setText(msgShow.getText()+"\n"+
                             emMessage.getFrom() + "：" +
                            ((EMTextMessageBody)emMessage.getBody()).getMessage()+"\n");
                }
            });
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        //收到透传消息
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        //收到已读回执
    }

    @Override
    public void onMessageDelivered(List<EMMessage> message) {
        //收到已送达回执
    }
    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        //消息被撤回
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        //消息状态变动
    }


    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    //销毁时移除消息监听
    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

}