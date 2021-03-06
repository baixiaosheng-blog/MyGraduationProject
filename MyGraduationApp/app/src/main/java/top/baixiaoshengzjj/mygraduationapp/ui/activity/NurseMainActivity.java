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
            toast("??????????????????????????????");
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
//                    EMClient.getInstance().groupManager().joinGroup("145220081614849");//???????????????
//                } catch (HyphenateException e) {
//                    Log.i("??????????????????", "??????????????????: ");
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        EaseUI.getInstance().getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
//            @Override
//            public String getDisplayedText(EMMessage message) {
//                Log.i(TAG, "getDisplayedText: ?????????????????????");
//                return message.getBody().toString();
//            }
//
//            @Override
//            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
//                Log.i(TAG, "getLatestText: ???????????????");
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
                Log.i(TAG, "onMessageReceived: ?????????????????????"+messages);
                for (EMMessage message : messages) {
                    //??????????????????????????????
                    EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                    //?????????????????????
                    notification = new NotificationCompat.Builder(getContext(), "chat")
                            .setAutoCancel(true)
                            .setContentTitle(message.getFrom())
                            .setContentText(message.getBody().toString())
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            //.setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            //???build()???????????????????????????????????????
                            .build();
                    notificationManager.notify(1, notification);

                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

                //?????????????????????????????????????????????
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

        //????????????????????????> ?????????????????????????????????????????????????????????
        bind.nursemainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(NurseMainActivity.this, "Center", Toast.LENGTH_SHORT).show();
                //???????????????????????????????????????group.isMembersOnly()???false?????????join
                // ????????????????????????????????????????????????bug????????????????????????????????????????????????


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
            // ???????????????????????????????????????????????????????????????
            moveTaskToBack(false);
            postDelayed(() -> {

                // ?????????????????????????????????????????????
                ActivityStackManager.getInstance().finishAllActivities();
                // ????????????????????????????????? API ?????????????????? Activity onDestroy ???????????????????????????
                // System.exit(0);

            }, 300);
        } else {
            toast("??????????????????");
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // ????????????????????????
        return !super.isStatusBarEnabled();
    }


    @Override
    protected void onDestroy() {
        //?????????????????????????????????????????????
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: ????????????????????????");
            }

            @Override
            public void onError(int code, String error) {
                Log.i(TAG, "onError: ????????????????????????");
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        super.onDestroy();
    }

    //????????????
    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {
        //Log.i(TAG, "onSoftKeyboardOpened: ???????????????");
        toast("jianpandakai");
        mBottomNavigationView.setVisibility(View.GONE);

    }

    @Override
    public void onSoftKeyboardClosed() {
        toast("jianpanguanbi");
        Log.i(TAG, "onSoftKeyboardClosed: ???????????????");
        mBottomNavigationView.setVisibility(View.VISIBLE);
    }


}
