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
            toast("??????????????????????????????");
        }

        groupId = "145220081614849";

    }


    /**
     * change BottomNavigationViewEx style
     */
    @Override
    protected void initView() {

        //??????????????????
        //??????LocationClient???
        mLocatoinClient = new LocationClient(getApplicationContext());
        //??????????????????
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

        // ????????????
        bind.patientmainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PatientMainActivity.this, "Center", Toast.LENGTH_SHORT).show();
                //???????????????????????????????????????group.isMembersOnly()???false?????????join
                // ????????????????????????????????????????????????bug????????????????????????????????????????????????
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().groupManager().joinGroup(groupId);//???????????????
                        } catch (HyphenateException e) {
                            Log.i("??????????????????", "??????????????????: ");
                            e.printStackTrace();
                        }
                    }
                }).start();

                //??????????????????

                Log.i(TAG, "onClick: ????????????"+locationAddress);
                Log.i(TAG, "onClick: ????????????"+longitude);
                Log.i(TAG, "onClick: ????????????"+latitude);

                //??????????????????
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
            Log.i(TAG, "onResume: ????????????");
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
        //????????????????????????qpp?????????????????????
        if (mLocatoinClient != null)
            mLocatoinClient.stop();
        //????????????
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


    //????????????????????????
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
//        String addr = location.getAddrStr();    //????????????????????????
//        String country = location.getCountry();    //????????????
//        String province = location.getProvince();    //????????????
//        String city = location.getCity();    //????????????
//        String district = location.getDistrict();    //????????????
//        String street = location.getStreet();    //??????????????????
//        String adcode = location.getAdCode();    //??????adcode
//        String town = location.getTown();    //??????????????????
            locationAddress = location.getAddrStr();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // ????????????????????????
        return !super.isStatusBarEnabled();
    }

}
