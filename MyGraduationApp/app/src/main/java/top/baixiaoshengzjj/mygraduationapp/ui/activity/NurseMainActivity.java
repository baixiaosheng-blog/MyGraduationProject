package top.baixiaoshengzjj.mygraduationapp.ui.activity;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseUI;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.databinding.ActivityMainNurseBinding;
import top.baixiaoshengzjj.mygraduationapp.helper.ActivityStackManager;
import top.baixiaoshengzjj.mygraduationapp.helper.DoubleClickHelper;
import top.baixiaoshengzjj.mygraduationapp.other.KeyboardWatcher;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.InfoInputFragment;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.MeFragment;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.PropagandFragment;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.RiskAssessmentFragment;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERAGE;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERMANE;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERPHONE;


public class NurseMainActivity extends MyActivity implements KeyboardWatcher.SoftKeyboardStateListener {
    private static final String TAG = NurseMainActivity.class.getSimpleName();
    private ActivityMainNurseBinding bind;
    private VpAdapter adapter;

    // collections
    private List<Fragment> fragments;// used for ViewPager adapter
    private BottomNavigationViewEx mBottomNavigationView;
    private EMConversation conversation;
    private EMMessageListener msgListener;
    private NotificationManager notificationManager;
    private Notification notification;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_nurse;
    }


    /**
     * create fragments
     */
    @Override
     protected void initData() {
        if(USERPHONE == null || USERMANE == null || USERAGE == null){
            toast("请及时完善个人信息！");
        }

        bind = DataBindingUtil.setContentView(this, R.layout.activity_main_nurse);

        fragments = new ArrayList<>(4);
        InfoInputFragment infoInputFragment = new InfoInputFragment();

        PropagandFragment propagandFragment =  new PropagandFragment();

        RiskAssessmentFragment riskAssessmentFragment =  new RiskAssessmentFragment();

        MeFragment meFragment =  new MeFragment();
        fragments.add(infoInputFragment);
        fragments.add(propagandFragment);
        fragments.add(riskAssessmentFragment);
        fragments.add(meFragment);
        bind.nursemainBnve.enableItemShiftingMode(false);
        bind.nursemainBnve.enableShiftingMode(false);
        bind.nursemainBnve.enableAnimation(false);

        // set adapter
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        bind.nursemainVp.setAdapter(adapter);

        initEvent();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    EMClient.getInstance().groupManager().joinGroup("145220081614849");//需异步处理
//                } catch (HyphenateException e) {
//                    Log.i("加入环信群聊", "加入群聊失败: ");
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        EaseUI.getInstance().getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
//            @Override
//            public String getDisplayedText(EMMessage message) {
//                Log.i(TAG, "getDisplayedText: 通知栏显示消息");
//                return message.getBody().toString();
//            }
//
//            @Override
//            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
//                Log.i(TAG, "getLatestText: 通知栏消息");
//                return null;
//            }
//
//            @Override
//            public String getTitle(EMMessage message) {
//                return message.getFrom();
//            }
//
//            @Override
//            public int getSmallIcon(EMMessage message) {
//                return 0;
//            }
//
//            @Override
//            public Intent getLaunchIntent(EMMessage message) {
//                return null;
//            }
//        });

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        notificationManager.notify(1, notification);

        msgListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                Log.i(TAG, "onMessageReceived: 收收收到新消息"+messages);
                for (EMMessage message : messages) {
                    //新消息声音、振动提醒
                    EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                    //新消状态栏提醒
                    notification = new NotificationCompat.Builder(getContext(), "chat")
                            .setAutoCancel(true)
                            .setContentTitle(message.getFrom())
                            .setContentText(message.getBody().toString())
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            //.setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            //在build()方法之前还可以添加其他方法
                            .build();
                    notificationManager.notify(1, notification);

                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

                //已读消除状态栏通知（不起作用）
                notificationManager.cancel(1);
            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };
        conversation = EMClient.getInstance().chatManager().getConversation("145220081614849", EMConversation.EMConversationType.GroupChat,true);
        //conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        

    }


    /**
     * change BottomNavigationViewEx style
     */
    @Override
    protected void initView() {
        mBottomNavigationView = findViewById(R.id.nursemain_bnve);
    }



    private void initEvent() {
        // set listener to change the current item of view pager when click bottom nav item
        bind.nursemainBnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = 0;
                switch (item.getItemId()) {
                    case R.id.nurse_infoinput:
                        position = 0;
                        break;
                    case R.id.nurse_backup:
                        position = 1;
                        break;
                    case R.id.nurse_favor:
                        position = 2;
                        break;
                    case R.id.nurse_visibility:
                        position = 3;
                        break;
                    case R.id.nurse_empty: {
                        return false;
                    }
                }
                if(previousPosition != position) {
                    bind.nursemainVp.setCurrentItem(position, false);
                    previousPosition = position;
                    Log.i(TAG, "-----bnve-------- previous item:" + bind.nursemainBnve.getCurrentItem() + " current item:" + position + " ------------------");
                }

                return true;
            }
        });
        // set listener to change the current checked item of bottom nav when scroll view pager
        bind.nursemainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "-----ViewPager-------- previous item:" + bind.nursemainBnve.getCurrentItem() + " current item:" + position + " ------------------");
                if (position >= 2)// 2 is center
                    position++;// if page is 2, need set bottom item to 3, and the same to 3 -> 4
                bind.nursemainBnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //中间悬浮按钮——> 从主界面（医生、护士端）跳转到我的团队
        bind.nursemainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(NurseMainActivity.this, "Center", Toast.LENGTH_SHORT).show();
                //如果群开群是自由加入的，即group.isMembersOnly()为false，直接join
                // ！！！！！！！！有重复加入群聊的bug，应判断是否已加入群聊！！！！！


                Intent intent = new Intent(NurseMainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }





    /**
     * view pager adapter
     */
    private static class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> data;

        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        if (DoubleClickHelper.isOnDoubleClick()) {
            // 移动到上一个任务栈，避免侧滑引起的不良反应
            moveTaskToBack(false);
            postDelayed(() -> {

                // 进行内存优化，销毁掉所有的界面
                ActivityStackManager.getInstance().finishAllActivities();
                // 销毁进程（注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
                // System.exit(0);

            }, 300);
        } else {
            toast("再按一次退出");
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }


    @Override
    protected void onDestroy() {
        //退出环信，否则切换环信账号失败
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: 退出环信登录成功");
            }

            @Override
            public void onError(int code, String error) {
                Log.i(TAG, "onError: 退出环信登录失败");
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        super.onDestroy();
    }

    //不起作用
    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {
        //Log.i(TAG, "onSoftKeyboardOpened: 软键盘打开");
        toast("jianpandakai");
        mBottomNavigationView.setVisibility(View.GONE);

    }

    @Override
    public void onSoftKeyboardClosed() {
        toast("jianpanguanbi");
        Log.i(TAG, "onSoftKeyboardClosed: 软键盘关闭");
        mBottomNavigationView.setVisibility(View.VISIBLE);
    }


}
