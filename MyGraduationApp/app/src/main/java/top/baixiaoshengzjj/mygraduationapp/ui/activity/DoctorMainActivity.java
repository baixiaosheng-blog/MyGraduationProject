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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.databinding.ActivityMainDoctorBinding;
import top.baixiaoshengzjj.mygraduationapp.helper.ActivityStackManager;
import top.baixiaoshengzjj.mygraduationapp.helper.DoubleClickHelper;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.MeFragment;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.MusicFragment;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.PropagandFragment;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.RiskAssessmentFragment;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERAGE;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERMANE;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERPHONE;

public class DoctorMainActivity extends MyActivity {
    private static final String TAG = DoctorMainActivity.class.getSimpleName();
    private ActivityMainDoctorBinding bind;
    private VpAdapter adapter;

    // collections
    private List<Fragment> fragments;// used for ViewPager adapter



    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_doctor;
    }


    /**
     * create fragments
     */
    @Override
     protected void initData() {

        if(USERPHONE == null || USERMANE == null || USERAGE == null){
            toast("请及时完善个人信息！");
        }


        bind = DataBindingUtil.setContentView(this, R.layout.activity_main_doctor);
        //fragments = new ArrayList<>(4);
        
        fragments = new ArrayList<>(4);
        MusicFragment musicFragment = new MusicFragment();

        PropagandFragment propagandFragment =  new PropagandFragment();

        RiskAssessmentFragment riskAssessmentFragment =  new RiskAssessmentFragment();
        
        MeFragment meFragment =  new MeFragment();
        fragments.add(musicFragment);
        fragments.add(propagandFragment);
        fragments.add(riskAssessmentFragment);
        fragments.add(meFragment);
        bind.doctormainBnve.enableItemShiftingMode(false);
        bind.doctormainBnve.enableShiftingMode(false);
        bind.doctormainBnve.enableAnimation(false);
        
        // set adapter
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        bind.doctormainVp.setAdapter(adapter);
        
        initEvent();
    }


    /**
     * change BottomNavigationViewEx style
     */
    @Override
    protected void initView() {


    }



    private void initEvent() {
        // set listener to change the current item of view pager when click bottom nav item
        bind.doctormainBnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = 0;
                switch (item.getItemId()) {
                    case R.id.doctor_infoinput:
                        position = 0;
                        break;
                    case R.id.doctor_backup:
                        position = 1;
                        break;
                    case R.id.doctor_favor:
                        position = 2;
                        break;
                    case R.id.doctor_visibility:
                        position = 3;
                        break;
                    case R.id.doctor_empty: {
                        return false;
                    }
                }
                if(previousPosition != position) {
                    bind.doctormainVp.setCurrentItem(position, false);
                    previousPosition = position;
                    Log.i(TAG, "-----bnve-------- previous item:" + bind.doctormainBnve.getCurrentItem() + " current item:" + position + " ------------------");
                }

                return true;
            }
        });
        // set listener to change the current checked item of bottom nav when scroll view pager
        bind.doctormainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "-----ViewPager-------- previous item:" + bind.doctormainBnve.getCurrentItem() + " current item:" + position + " ------------------");
                if (position >= 2)// 2 is center
                    position++;// if page is 2, need set bottom item to 3, and the same to 3 -> 4
                bind.doctormainBnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //中间悬浮按钮——> 从主界面（医生、护士端）跳转到我的团队
        bind.doctormainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DoctorMainActivity.this, "Center", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DoctorMainActivity.this, TeamActivity.class);
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
}
