package top.baixiaoshengzjj.mygraduationapp.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupReadAck;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.util.EMLog;

import java.util.List;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.GroupDetailActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.MemberDetailsActivity;

public class ChatFragment extends EaseChatFragment {


    protected EaseChatFragmentHelper mchatFragmentHelper = new EaseChatFragmentHelper() {
        @Override
        public void onSetMessageAttributes(EMMessage message) {

        }

        @Override
        public void onEnterToChatDetails() {
            Log.d(TAG, "onEnterToChatDetails: 群组 "+toChatUsername);
            Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
            intent.putExtra("groupId", toChatUsername);
            startActivity(intent);
        }

        @Override
        public void onAvatarClick(String username) {
            Log.d(TAG, "onAvatarClick: uid:"+username);
            Intent intent = new Intent(getActivity(), MemberDetailsActivity.class);
            intent.putExtra("uid",username);
            startActivity(intent);
        }

        @Override
        public void onAvatarLongClick(String username) {

        }

        @Override
        public boolean onMessageBubbleClick(EMMessage message) {
            return false;
        }

        @Override
        public void onMessageBubbleLongClick(EMMessage message) {

        }

        @Override
        public boolean onExtendMenuItemClick(int itemId, View view) {
            return false;
        }

        @Override
        public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
            return null;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, boolean roaming) {
        return super.onCreateView(inflater, container, savedInstanceState, roaming);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected boolean turnOnTyping() {
        return super.turnOnTyping();
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void setUpView() {
        super.setUpView();
    }

    @Override
    protected void registerExtendMenuItem() {
        super.registerExtendMenuItem();
    }

    @Override
    protected void onConversationInit() {
        super.onConversationInit();
    }

    @Override
    protected void onMessageListInit() {
        super.onMessageListInit();
    }

    @Override
    protected void setRefreshLayoutListener() {
        super.setRefreshLayoutListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onChatRoomViewCreation() {
        super.onChatRoomViewCreation();
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        super.onMessageReceived(messages);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        super.onCmdMessageReceived(messages);
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        super.onMessageRead(messages);
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        super.onMessageDelivered(messages);
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        super.onMessageRecalled(messages);
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        super.onMessageChanged(emMessage, change);
    }

    @Override
    public void onGroupMessageRead(List<EMGroupReadAck> groupReadAcks) {
        super.onGroupMessageRead(groupReadAcks);
    }

    @Override
    public void onReadAckForGroupMessageUpdated() {
        super.onReadAckForGroupMessageUpdated();
    }

    @Override
    protected void inputAtUsername(String username, boolean autoAddAtSymbol) {
        super.inputAtUsername(username, autoAddAtSymbol);
    }

    @Override
    protected void inputAtUsername(String username) {
        super.inputAtUsername(username);
    }

    @Override
    protected void sendTextMessage(String content) {
        super.sendTextMessage(content);
    }

    @Override
    protected void sendBigExpressionMessage(String name, String identityCode) {
        super.sendBigExpressionMessage(name, identityCode);
    }

    @Override
    protected void sendVoiceMessage(String filePath, int length) {
        super.sendVoiceMessage(filePath, length);
    }

    @Override
    protected void sendImageMessage(String imagePath) {
        super.sendImageMessage(imagePath);
    }

    @Override
    protected void sendImageMessage(Uri imageUri) {
        super.sendImageMessage(imageUri);
    }

    @Override
    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        super.sendLocationMessage(latitude, longitude, locationAddress);
    }

    @Override
    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        super.sendVideoMessage(videoPath, thumbPath, videoLength);
    }

    @Override
    protected void sendVideoMessage(Uri videoUri, String thumbPath, int videoLength) {
        super.sendVideoMessage(videoUri, thumbPath, videoLength);
    }

    @Override
    protected void sendFileMessage(String filePath) {
        super.sendFileMessage(filePath);
    }

    @Override
    protected void sendFileMessage(Uri fileUri) {
        super.sendFileMessage(fileUri);
    }

    @Override
    protected void sendMessage(EMMessage message) {
        super.sendMessage(message);
    }

    @Override
    protected void sendPicByUri(Uri selectedImage) {
        super.sendPicByUri(selectedImage);
    }

    @Override
    protected void sendFileByUri(Uri uri) {
        super.sendFileByUri(uri);
    }

    @Override
    protected void selectPicFromCamera() {
        super.selectPicFromCamera();
    }

    @Override
    protected void selectPicFromLocal() {
        super.selectPicFromLocal();
    }

    @Override
    protected void emptyHistory() {
        super.emptyHistory();
    }

    @Override
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "toGroupDetails: "+ mchatFragmentHelper);
            if(mchatFragmentHelper != null){
                Log.d(TAG, "toGroupDetails: chatFragmentHelper != null");
                mchatFragmentHelper.onEnterToChatDetails();
            }
        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            if(mchatFragmentHelper != null){
                mchatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    @Override
    protected void hideKeyboard() {
        super.hideKeyboard();
    }

    @Override
    protected void forwardMessage(String forward_msg_id) {
        super.forwardMessage(forward_msg_id);
    }

    @Override
    public void setChatFragmentHelper(EaseChatFragmentHelper chatFragmentHelper) {
        super.setChatFragmentHelper(chatFragmentHelper);
    }

    @Override
    protected void setListItemClickListener() {
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {
            @Override
            public void onUserAvatarClick(String username) {
                //跳转个人详情界面
                if(mchatFragmentHelper != null){
                    mchatFragmentHelper.onAvatarClick(username);
                }
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                if(mchatFragmentHelper != null){
                    mchatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if(mchatFragmentHelper == null){
                    return false;
                }
                return mchatFragmentHelper.onMessageBubbleClick(message);
            }

            @Override
            public boolean onResendClick(EMMessage message) {
                EMLog.i(TAG, "onResendClick");
                new EaseAlertDialog(getContext(), R.string.resend, R.string.confirm_resend, null, new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }
                        message.setStatus(EMMessage.Status.CREATE);
                        sendMessage(message);
                    }
                }, true).show();
                return true;
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                if(mchatFragmentHelper != null){
                    mchatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public void onMessageInProgress(EMMessage message) {
                message.setMessageStatusCallback(messageStatusCallback);
            }
        });
    }


}
