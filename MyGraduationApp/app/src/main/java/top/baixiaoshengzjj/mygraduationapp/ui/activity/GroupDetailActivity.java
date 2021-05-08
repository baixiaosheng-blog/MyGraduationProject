package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hjq.base.BaseDialog;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.easeui.widget.EaseTitleBar;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.MessageDialog;

public class GroupDetailActivity extends MyActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_ADD_USER = 0;
    private EaseTitleBar titleBar;

    private TextView tvGroupRefund;
    private String groupId;
    private EMGroup group;
    private EMGroupManager repository;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_group_detail;
    }


    @Override
    protected void initView() {
        Intent intent = new Intent();
        groupId = intent.getStringExtra("groupId");
        titleBar = findViewById(R.id.title_bar);
        tvGroupRefund = findViewById(R.id.tv_group_refund);

        group =  EMClient.getInstance().groupManager().getGroup(groupId);

        //initGroupView();

        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setOnClickListener(tvGroupRefund);
    }

    @Override
    protected void initData() {


    }

    private void initGroupView() {
        if(group == null) {
            finish();
            return;
        }

        if (isOwner()) {
            tvGroupRefund.setVisibility(View.VISIBLE);
        } else {
            tvGroupRefund.setVisibility(View.INVISIBLE);
        }
        //tvGroupRefund.setText(getResources().getString(isOwner() ? R.string.chat_group_detail_dissolve : R.string.chat_group_detail_refund));

        //conversation = DemoHelper.getInstance().getConversation(groupId, EMConversation.EMConversationType.GroupChat, true);

    }

    //判断是否为群主
    private boolean isOwner() {
        //是群主返回 true

        //不是群主返回 flase
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_group_refund ://退出群组
                showConfirmDialog();
                break;

        }
    }



    private void showConfirmDialog() {

        new MessageDialog.Builder(this)
                // 标题可以不用填写
                .setTitle("离开群组")
                // 内容必须要填写
                .setMessage("确定离开群组吗？")
                // 确定按钮文本
                .setConfirm(getString(R.string.common_confirm))
                // 设置 null 表示不显示取消按钮
                .setCancel(getString(R.string.common_cancel))
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(true)
                .setListener(new MessageDialog.OnListener() {

                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        //解散群组代码（未能实现，会抛空指针异常）
//                        EMClient.getInstance().groupManager().asyncLeaveGroup(groupId, new EMCallBack() {
//                            @Override
//                            public void onSuccess() {
//                                toast("离开了群组！");
//                            }
//
//                            @Override
//                            public void onError(int code, String error) {
//                                toast("离开群组失败!"+error);
//                            }
//
//                            @Override
//                            public void onProgress(int progress, String status) {
//
//                            }
//                        });
                        ChatActivity.instance.finish(); //结束群聊界面
                        finish();                       //结束当前群详情界面
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("取消了");
                        dialog.dismiss();
                    }
                })
                .show();

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}
