package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.SettingBar;

import java.io.UnsupportedEncodingException;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.http.glide.GlideApp;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetAvatarApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetPersonalDataApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.GetAvatarBean;
import top.baixiaoshengzjj.mygraduationapp.http.response.GetPersonalDataBean;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.WaitDialog;

public class MemberDetailsActivity extends MyActivity {
    private ViewGroup mAvatarLayout;
    private ImageView mAvatarView;
    private SettingBar mNameView;
    private SettingBar mAgeView;
    private SettingBar mGenderView;
    private SettingBar mAddressView;
    private SettingBar mFullAddressView;
    private SettingBar mEmergencyContactView;
    private SettingBar mRecord;

    private Integer uId;

    /** 姓名 */
    private String mName;
    /** 年龄 */
    private Integer mAge;
    /** 性别 */
    private Integer mGender;
    /** 地区 */
    private String mAddress;
    /** 详细地址 */
    private String mFullAddress;
    /** 紧急联系人 */
    private String mEmergencyContact;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_member_details;
    }

    @Override
    protected void initView() {
        mAvatarLayout = findViewById(R.id.fl_member_details_avatar);
        mAvatarView = findViewById(R.id.iv_member_details_avatar);
        mNameView = findViewById(R.id.sb_member_details_name);
        mAgeView = findViewById(R.id.sb_member_data_age);
        mGenderView = findViewById(R.id.sb_member_details_sex);
        mAddressView = findViewById(R.id.sb_member_details_address);
        mFullAddressView = findViewById(R.id.sb_member_details_fulladdress);
        mEmergencyContactView = findViewById(R.id.sb_member_details_contact);
        mRecord = findViewById(R.id.sb_member_details_record);
        setOnClickListener(mRecord);

        GlideApp.with(getActivity())
                .load(R.drawable.avatar_placeholder_ic)
                .placeholder(R.drawable.avatar_placeholder_ic)
                .error(R.drawable.avatar_placeholder_ic)
                .circleCrop()
                .into(mAvatarView);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        uId = Integer.valueOf(uid);
        Log.d("详细资料", "initData: uId" + uId);
        //加载头像
        loadAvatarRes(uId);
        //加载个人信息并显示到对应位置
        loadPersonalData(uId);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sb_member_details_record :
                Intent intent = new Intent(MemberDetailsActivity.this, MedicalRecordActivity.class);
                intent.putExtra("patientId", uId);
                startActivity(intent);
                break;

        }
    }



    /**
     *功能描述 加载用户数据并显示在对应位置
     * @author 百xiao生
     * @date 2021/4/23 1:23
     * @param  * @param userid
     * @return void
     */
    private void loadPersonalData(Integer userid) {
        BaseDialog waitDialog = new WaitDialog.Builder(this)    //等待对话框
                // 消息文本可以不用填写
                .setMessage("加载中...")
                .show();
        EasyHttp.post(this)
                .api(new GetPersonalDataApi()
                        .setUId(userid))
                .request(new HttpCallback<HttpData<GetPersonalDataBean>>(this){
                    @Override
                    public void onSucceed(HttpData<GetPersonalDataBean> data) {
                        if(data == null || data.getData() == null){
                            toast("获取个人信息失败！");
                            return;
                        }
                        mName = data.getData().getName();
                        mAge = data.getData().getAge();
                        mAddress = data.getData().getAddress();
                        mFullAddress = data.getData().getFulladdress();
                        mEmergencyContact = data.getData().getContact();
                        mGender = data.getData().getGender();
                        //性别
                        if (mGender == 0){
                            mGenderView.setRightText("女");
                        }else if (mGender == 1){
                            mGenderView.setRightText("男");
                        }
                        //姓名
                        mNameView.setRightText(mName);
                        //年龄
                        mAgeView.setRightText(mAge.toString());
                        //地区
                        mAddressView.setRightText(mAddress);
                        //详细地址
                        mFullAddressView.setRightText(mFullAddress);
                        //紧急联系人
                        mEmergencyContactView.setRightText(mEmergencyContact);
                        postDelayed(waitDialog::dismiss, 100);
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.d("获取个人信息", "onFail: "+e);
                        toast("获取个人信息失败！");
                    }
                });
    }


    //加载头像
    private void loadAvatarRes(Integer uId) {
        //上传下载慢，可能是服务器带宽问题
        //测试用的natapp内网穿透，1m带宽 ≈ 125k/s

        EasyHttp.post(this)
                .api(new GetAvatarApi()
                        .setUId(uId))
                .request(new HttpCallback<HttpData<GetAvatarBean>>(this) {

                    @Override
                    public void onSucceed(HttpData<GetAvatarBean> data) {
                        Log.d("头像加载", "onSucceed: 头像数据获取成功");

                        if (data.getData() == null){
                            GlideApp.with(getActivity())
                                    .load(R.drawable.avatar_placeholder_ic)
                                    .placeholder(R.drawable.avatar_placeholder_ic)
                                    .error(R.drawable.avatar_placeholder_ic)
                                    .circleCrop()
                                    .into(mAvatarView);
                            return;
                        }
                        byte[] avatar = new byte[0];
                        try {
                            //统一编码，以免图片信息在多次byte[]和String转过程中产生错误
                            avatar = data.getData().getAvatar().getBytes("ISO_8859_1");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.d("头像加载", "onSucceed: 数组长度"+ avatar.length);


                        Bitmap bitmap = BitmapFactory.decodeByteArray(avatar,0,avatar.length);
                        Log.d("头像加载", "onSucceed:bitmap "+bitmap);
                        if (bitmap!= null) {
                            GlideApp.with(getActivity())
                                    .load(bitmap)
                                    .placeholder(R.drawable.avatar_placeholder_ic)
                                    .error(R.drawable.avatar_placeholder_ic)
                                    .circleCrop()
                                    .into(mAvatarView);
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.d("头像加载", "onFail: 头像加载失败"+e);
                        super.onFail(e);
                    }
                });
    }
}