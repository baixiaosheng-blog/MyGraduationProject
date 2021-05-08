package top.baixiaoshengzjj.mygraduationapp.ui.activity;


import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.databinding.ActivityMainPatientBinding;
import top.baixiaoshengzjj.mygraduationapp.helper.ActivityStackManager;
import top.baixiaoshengzjj.mygraduationapp.helper.DoubleClickHelper;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.AddMedicalRecordFragment;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.MeFragment;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.PropagandFragment;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.RiskAssessmentFragment;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERAGE;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERMANE;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERPHONE;

public class PatientMainActivity extends MyActivity {
    private static final String TAG = PatientMainActivity.class.getSimpleName();
    private ActivityMainPatientBinding bind;
    private VpAdapter adapter;

    private LocationClient mLocatoinClient = null;
    private MyLocationListener mLocationListener = new MyLocationListener();

    // collections
    private List<Fragment> fragments;// used for ViewPager adapter
    private String groupId;
    private String locationAddress;
    private double latitude;
    private double longitude;

    @Override
    protected void initActivity() {
        //initialize SDK with context, should call this before setContentView
        SDKInitializer.initialize(getApplicationContext());
        super.initActivity();
    }

    @Override
    protected int getLayoutId() {

//        switch (IDENTITY){
//            case 3:
//                return R.layout.activity_main_doctor;
//            case 2:
//                return R.layout.activity_main_nurse;
//        }
        return R.layout.activity_main_patient;
    }


    /**
     * create fragments
     */
    @Override
     protected void initData() {
        if(USERPHONE == null || USERMANE == null || USERAGE == null){
            toast("请及时完善个人信息！");
        }

        groupId = "145220081614849";

    }


    /**
     * change BottomNavigationViewEx style
     */
    @Override
    protected void initView() {

        //百度地图定位
        //声明LocationClient类
        mLocatoinClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocatoinClient.registerLocationListener(mLocationListener);
//        mLocClient = new LocationClient(this);
//        mLocClient.registerLocationListener(myListener);
//
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// open gps
        // option.setCoorType("bd09ll");
        // Johnson change to use gcj02 coordination. chinese national standard
        // so need to conver to bd09 everytime when draw on baidu map
        option.setCoorType("gcj02");
        option.setScanSpan(30000);
        option.setAddrType("all");
        mLocatoinClient.setLocOption(option);

        bind = DataBindingUtil.setContentView(this, R.layout.activity_main_patient);
        //fragments = new ArrayList<>(4);

        fragments = new ArrayList<>(4);

        AddMedicalRecordFragment statusFragment = new AddMedicalRecordFragment();
        //StatusFragment statusFragment = new StatusFragment();

        PropagandFragment propagandFragment = new PropagandFragment();

        RiskAssessmentFragment riskAssessmentFragment = new RiskAssessmentFragment();

        MeFragment meFragment = new MeFragment();
        fragments.add(statusFragment);
        fragments.add(propagandFragment);
        fragments.add(riskAssessmentFragment);
        fragments.add(meFragment);
        bind.patientmainBnve.enableItemShiftingMode(false);
        bind.patientmainBnve.enableShiftingMode(false);
        bind.patientmainBnve.enableAnimation(false);

        // set adapter
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        bind.patientmainVp.setAdapter(adapter);

        initEvent1();

    }



    private void initEvent1() {
        // set listener to change the current item of view pager when click bottom nav item
        bind.patientmainBnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = 0;
                switch (item.getItemId()) {
                    case R.id.patient_music:
                        position = 0;
                        break;
                    case R.id.patient_backup:
                        position = 1;
                        break;
                    case R.id.patient_favor:
                        position = 2;
                        break;
                    case R.id.patient_visibility:
                        position = 3;
                        break;
                    case R.id.patient_empty: {
                        return false;
                    }
                }
                if(previousPosition != position) {
                    bind.patientmainVp.setCurrentItem(position, false);
                    previousPosition = position;
                    Log.i(TAG, "-----bnve-------- previous item:" + bind.patientmainBnve.getCurrentItem() + " current item:" + position + " ------------------");
                }

                return true;
            }
        });
        // set listener to change the current checked item of bottom nav when scroll view pager
        bind.patientmainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "-----ViewPager-------- previous item:" + bind.patientmainBnve.getCurrentItem() + " current item:" + position + " ------------------");
                if (position >= 2)// 2 is center
                    position++;// if page is 2, need set bottom item to 3, and the same to 3 -> 4
                bind.patientmainBnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 一键呼救
        bind.patientmainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PatientMainActivity.this, "Center", Toast.LENGTH_SHORT).show();
                //如果群开群是自由加入的，即group.isMembersOnly()为false，直接join
                // ！！！！！！！！有重复加入群聊的bug，应判断是否已加入群聊！！！！！
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().groupManager().joinGroup(groupId);//需异步处理
                        } catch (HyphenateException e) {
                            Log.i("加入环信群聊", "加入群聊失败: ");
                            e.printStackTrace();
                        }
                    }
                }).start();

                //获取定位信息

                Log.i(TAG, "onClick: 定位信息"+locationAddress);
                Log.i(TAG, "onClick: 定位信息"+longitude);
                Log.i(TAG, "onClick: 定位信息"+latitude);

                //发送定位信息
                //if (locationAddress != null && !locationAddress.equals("")) {
                    EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, groupId);
                    message.setChatType(EMMessage.ChatType.GroupChat);
                    EMClient.getInstance().chatManager().sendMessage(message);;
                //}

                Intent intent = new Intent(PatientMainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * set listeners
     */
//    private void initEvent() {
//        // set listener to change the current item of view pager when click bottom nav item
//        bind.bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            private int previousPosition = -1;
//
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int position = 0;
//                switch (item.getItemId()) {
//                    case R.id.i_music:
//                        position = 0;
//                        break;
//                    case R.id.i_backup:
//                        position = 1;
//                        break;
//                    case R.id.i_favor:
//                        position = 2;
//                        break;
//                    case R.id.i_visibility:
//                        position = 3;
//                        break;
//                    case R.id.i_empty: {
//                        return false;
//                    }
//                }
//                if(previousPosition != position) {
//                    bind.vp.setCurrentItem(position, false);
//                    previousPosition = position;
//                    Log.i(TAG, "-----bnve-------- previous item:" + bind.bnve.getCurrentItem() + " current item:" + position + " ------------------");
//                }
//
//                return true;
//            }
//        });
//
//        // set listener to change the current checked item of bottom nav when scroll view pager
//        bind.vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Log.i(TAG, "-----ViewPager-------- previous item:" + bind.bnve.getCurrentItem() + " current item:" + position + " ------------------");
//                if (position >= 2)// 2 is center
//                    position++;// if page is 2, need set bottom item to 3, and the same to 3 -> 4
//                bind.bnve.setCurrentItem(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        // center item click listener
//        bind.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Center", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

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
    protected void onResume() {
        
        if (mLocatoinClient != null) {
            Log.i(TAG, "onResume: 开始定位");
            mLocatoinClient.start();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mLocatoinClient != null)
            mLocatoinClient.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //结束主界面（退出qpp）时，关闭定位
        if (mLocatoinClient != null)
            mLocatoinClient.stop();
        //退出环信
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


    //百度定位监听事件
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
//        String addr = location.getAddrStr();    //获取详细地址信息
//        String country = location.getCountry();    //获取国家
//        String province = location.getProvince();    //获取省份
//        String city = location.getCity();    //获取城市
//        String district = location.getDistrict();    //获取区县
//        String street = location.getStreet();    //获取街道信息
//        String adcode = location.getAdCode();    //获取adcode
//        String town = location.getTown();    //获取乡镇信息
            locationAddress = location.getAddrStr();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

}
