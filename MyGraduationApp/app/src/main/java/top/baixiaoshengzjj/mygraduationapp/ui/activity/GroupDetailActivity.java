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

    //?????????????????????
    private boolean isOwner() {
        //??????????????? true

        //?????????????????? flase
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_group_refund ://????????????
                showConfirmDialog();
                break;

        }
    }



    private void showConfirmDialog() {

        new MessageDialog.Builder(this)
                // ????????????????????????
                .setTitle("????????????")
                // ?????????????????????
                .setMessage("????????????????????????")
                // ??????????????????
                .setConfirm(getString(R.string.common_confirm))
                // ?????? null ???????????????????????????
                .setCancel(getString(R.string.common_cancel))
                // ???????????????????????????????????????
                //.setAutoDismiss(true)
                .setListener(new MessageDialog.OnListener() {

                    @Override
                    public void onConfirm(BaseDialog dialog) {
                        //????????????????????????????????????????????????????????????
//                        EMClient.getInstance().groupManager().asyncLeaveGroup(groupId, new EMCallBack() {
//                            @Override
//                            public void onSuccess() {
//                                toast("??????????????????");
//                            }
//
//                            @Override
//                            public void onError(int code, String error) {
//                                toast("??????????????????!"+error);
//                            }
//
//                            @Override
//                            public void onProgress(int progress, String status) {
//
//                            }
//                        });
                        ChatActivity.instance.finish(); //??????????????????
                        finish();                       //???????????????????????????
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("?????????");
                        dialog.dismiss();
                    }
                })
                .show();

    }

    @Override
    public boolean isStatusBarEnabled() {
        // ????????????????????????
        return !super.isStatusBarEnabled();
    }
}
