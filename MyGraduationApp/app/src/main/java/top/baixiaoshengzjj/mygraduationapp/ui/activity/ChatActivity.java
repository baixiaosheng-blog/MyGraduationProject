package top.baixiaoshengzjj.mygraduationapp.ui.activity;


import android.os.Bundle;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.ChatFragment;


public class ChatActivity extends MyActivity {
    public  static  ChatActivity  instance = null;

    private EaseChatFragment chatFragment;
    private String groupId;
    private EaseTitleBar titleBar;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_m_team;
    }

    @Override
    protected void initView() {
        instance = this;
        titleBar = (EaseTitleBar) findViewById(com.hyphenate.easeui.R.id.title_bar);
        groupId = "145220081614849";
        //new出EaseChatFragment或其子类的实例
        chatFragment = new ChatFragment();
        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID, groupId);
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.chat_fragment, chatFragment).commit();

        ;

    }

    @Override
    protected void initData() {

    }


    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}
