package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.SettingBar;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.aop.SingleClick;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.http.glide.GlideApp;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetAvatarApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetAvatarPathApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetPersonalDataApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.UpdateAddressApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.UpdateAgeApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.UpdateAvatarApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.UpdateContactApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.UpdateFulladdressApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.UpdateGenderApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.UpdateIdentityApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.UpdateNameApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.GetAvatarBean;
import top.baixiaoshengzjj.mygraduationapp.http.response.GetPersonalDataBean;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.AddressDialog;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.InputDialog;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.MenuDialog;
import top.baixiaoshengzjj.mygraduationapp.ui.dialog.WaitDialog;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.IDENTITY;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERGENDER;
import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERID;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : 个人资料
 */
public final class PersonalDataActivity extends MyActivity {

    private ViewGroup mAvatarLayout;
    private ImageView mAvatarView;
    private SettingBar mIDView;
    private SettingBar mNameView;
    private SettingBar mAgeView;
    private SettingBar mGenderView;
    private SettingBar mAddressView;
    private SettingBar mFullAddressView;
    private SettingBar mIdentityView;
    private SettingBar mEmergencyContactView;



    /** 省 */
    private String mProvince = "广东省";
    /** 市 */
    private String mCity = "广州市";
    /** 区 */
    private String mArea = "天河区";
    /** 身份*/
    private String normalUser = "普通用户";
    private String nurseUser = "院前医护人员";
    private String doctorUser = "院内医护人员";
    /** 头像地址 */
    private String mAvatarUrl;
    /** 姓名 */
    private String mName;
    /** 年龄 */
    private Integer mAge;
    /** 地区 */
    private String mAddress;
    /** 详细地址 */
    private String mFullAddress;
    /** 紧急联系人 */
    private String mEmergencyContact;



    @Override
    protected int getLayoutId() {
        return R.layout.personal_data_activity;
    }

    @Override
    protected void initView() {
//        checkNeedPermissions();
        mAvatarLayout = findViewById(R.id.fl_person_data_avatar);
        mAvatarView = findViewById(R.id.iv_person_data_avatar);
        mIDView = findViewById(R.id.sb_person_data_id);
        mNameView = findViewById(R.id.sb_person_data_name);
        mAgeView = findViewById(R.id.sb_person_data_age);
        mGenderView = findViewById(R.id.sb_person_data_sex);
        mAddressView = findViewById(R.id.sb_person_data_address);
        mFullAddressView = findViewById(R.id.sb_person_data_fulladdress);
        mIdentityView = findViewById(R.id.sb_person_data_identity);
        mEmergencyContactView = findViewById(R.id.sb_person_data_contact);
        setOnClickListener(mAvatarLayout, mAvatarView, mNameView, mAgeView, mAddressView,
                mFullAddressView, mIdentityView, mGenderView, mEmergencyContactView);
    }

    @Override
    protected void initData() {
        String address = mProvince + mCity + mArea;
        if(IDENTITY == 1){
            //用户身份（1代表普通用户，2代表护士，3代表医生），默认为1
            mIdentityView.setRightText(normalUser);
        }else if (IDENTITY == 2){
            mIdentityView.setRightText(nurseUser);
        }else if (IDENTITY == 3){
            mIdentityView.setRightText(doctorUser);
        }
        mAddressView.setRightText(address);         //地区
        mIDView.setRightText(USERID.toString());    //用户id

        //查询数据库获取用户信息并显示在对应位置
        loadAvatar();
        loadPersonalData(USERID);


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
                        if(data == null || data.getData() == null)
                            return;
                        mName = data.getData().getName();
                        mAge = data.getData().getAge();
                        mAddress = data.getData().getAddress();
                        mFullAddress = data.getData().getFulladdress();
                        mEmergencyContact = data.getData().getContact();
                        //性别
                        if (USERGENDER == 0){
                            mGenderView.setRightText("女");
                        }else if (USERGENDER == 1){
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
                        super.onFail(e);
                    }
                });
    }

    /**
     *功能描述 加载头像数据
     * @author 百xiao生
     * @date 2021/4/20 16:51
     * @param  * @param
     * @return void
    */
    private void loadAvatar() {

        EasyHttp.post(this)
                .api(new GetAvatarPathApi()
                        .setUId(USERID))
                .request(new HttpCallback<HttpData<String>>(this){
                    @Override
                    public void onSucceed(HttpData<String> data) {
                        Log.i("头像path加载", "onSucceed: 头像path获取成功");
                        if (data.getData() == null){
                            GlideApp.with(getActivity())
                                    .load(R.drawable.avatar_placeholder_ic)
                                    .placeholder(R.drawable.avatar_placeholder_ic)
                                    .error(R.drawable.avatar_placeholder_ic)
                                    .circleCrop()
                                    .into(mAvatarView);
                            return;
                        }
                        mAvatarUrl = data.getData();
                        File file = new File(mAvatarUrl);
                        if (!file.isFile()){
                            Log.i("加载头像path", "onSucceed: 头像在本地被删除");
                            mAvatarUrl = null;
                            loadAvatarRes();
                        }
                        GlideApp.with(getActivity())
                                .load(mAvatarUrl)
                                .placeholder(R.drawable.avatar_placeholder_ic)
                                .error(R.drawable.avatar_placeholder_ic)
                                .circleCrop()
                                .into(mAvatarView);
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.d("加载头像文件的路径", "onFail: "+e);
                        toast("未上传过头像！");

                        //未上传过头像或加载头像路径失败，则显示默认头像
                        GlideApp.with(getActivity())
                                .load(R.drawable.avatar_placeholder_ic)
                                .placeholder(R.drawable.avatar_placeholder_ic)
                                .error(R.drawable.avatar_placeholder_ic)
                                .circleCrop()
                                .into(mAvatarView);
                    }
                });



    }

    /**
     * 加载头像数据
     */
    private void loadAvatarRes() {
        //上传下载慢，可能是服务器带宽问题
        //测试用的natapp内网穿透，1m带宽 ≈ 125k/s

        EasyHttp.post(this)
                .api(new GetAvatarApi()
                        .setUId(USERID))
                .request(new HttpCallback<HttpData<GetAvatarBean>>(this) {

                    @Override
                    public void onSucceed(HttpData<GetAvatarBean> data) {
                        Log.d("头像加载", "onSucceed: 头像数据获取成功");
                        if (data.getData() == null){
                            //没上传头像数据
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
                        GlideApp.with(getActivity())
                                .load(R.drawable.avatar_placeholder_ic)
                                .placeholder(R.drawable.avatar_placeholder_ic)
                                .error(R.drawable.avatar_placeholder_ic)
                                .circleCrop()
                                .into(mAvatarView);
                        //super.onFail(e);
                    }
                });
    }


    //各个条目的点击事件
    @SingleClick
    @Override
    public void onClick(View v) {
        if (v == mAvatarLayout) {
            setAvatarLayout();
        } else if (v == mAvatarView) {
            if (!TextUtils.isEmpty(mAvatarUrl)) {
                // 查看头像
                ImagePreviewActivity.start(getActivity(), mAvatarUrl);
            } else {
                // 选择头像
                onClick(mAvatarLayout);
            }
        } else if (v == mNameView) {
            setName(this, USERID);//设置更新姓名的方法
        } else if (v == mAgeView){
            setAge(this, USERID);//设置更新年龄的方法
        } else if (v == mGenderView){
            setGender(this, USERID);//设置更新性别的方法
        } else if (v == mAddressView) {
            setAddress(this);           //设置更新地区的方法
        }else if (v == mFullAddressView){
            setFullAddress(this);       //设置更新详细地址的方法
        }else if (v == mIdentityView){
            setIdentity(this);          //设置更新身份的方法
        }else if (v == mEmergencyContactView){
            setEmergencyContact(this, USERID);//设置更新联系人的方法
        }
    }


    //修改紧急联系人
    private void setEmergencyContact(Context context, Integer uid) {
        new InputDialog.Builder(context)
                // 标题可以不用填写
                .setTitle(getString(R.string.personal_data_contact_hint))//对话框标题
                .setContent(mEmergencyContactView.getRightText())
                .setListener((dialog, content) -> {
                    if (!mEmergencyContactView.getRightText().equals(content)) {
                        mEmergencyContactView.setRightText(content);
                        //更新数据库
                        EasyHttp.post(this)
                                .api(new UpdateContactApi()
                                        .setUid(uid)
                                        .setContact(mEmergencyContactView.getRightText().toString()))
                                .request(new HttpCallback<HttpData<String>>(this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("更新联系人为："+ mEmergencyContactView.getRightText().toString());
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("更新联系人", "onFail: "+e);
                                        toast("更新联系人失败！");
                                    }
                                });
                    }
                })
                .show();
    }


    //修改性别
    private void setGender(Context context, Integer uid) {
        List<String> data1 = new ArrayList<>();
        data1.add("男");
        data1.add("女");

        // 居中选择框
        new MenuDialog.Builder(context)
                .setGravity(Gravity.CENTER)
                // 设置 null 表示不显示取消按钮
                //.setCancel(null)
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setList(data1)
                .setListener(new MenuDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, int position, String string) {

                        if (string == "男"){
                            USERGENDER = 1;
                        }else if (string == "女"){
                            USERGENDER = 0;
                        }
                        //更新数据库
                        //updataGender(uid);
                        EasyHttp.post(PersonalDataActivity.this)
                                .api(new UpdateGenderApi()
                                        .setUid(uid)
                                        .setGender(USERGENDER))
                                .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("更新性别为："+string);
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("更新性别", "onFail: "+e);
                                        toast("更新性别失败！");
                                    }
                                });
                        mGenderView.setRightText(string);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("取消了");
                    }
                })
                .show();
    }
    public void updataGender(Integer uid){
        EasyHttp.post(this)
                .api(new UpdateGenderApi()
                        .setUid(uid)
                        .setGender(USERGENDER))
                .request(new HttpCallback<HttpData<String>>(this){
                    @Override
                    public void onSucceed(HttpData<String> result) {
                        toast("更新了性别！");
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.d("更新性别", "onFail: "+e);
                        toast("更新性别失败！");
                    }
                });
    }



    //修改身份并上传
    private void setIdentity(Context context) {
        List<String> data1 = new ArrayList<>();
        data1.add(normalUser);
        data1.add(nurseUser);
        data1.add(doctorUser);

        // 居中选择框
        new MenuDialog.Builder(context)
                .setGravity(Gravity.CENTER)
                // 设置 null 表示不显示取消按钮
                //.setCancel(null)
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setList(data1)
                .setListener(new MenuDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, int position, String string) {

                        if (string == doctorUser){
                            IDENTITY = 3;
                        }else if (string == nurseUser){
                            IDENTITY = 2;
                        }else IDENTITY = 1;
                        //更新数据库
                        EasyHttp.post(PersonalDataActivity.this)
                                .api(new UpdateIdentityApi()
                                        .setUid(USERID)
                                        .setIdentity(IDENTITY))
                                .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("更新身份为："+ string + "，重启App生效");
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("更新身份", "onFail: "+e);
                                        toast("更新身份失败！");
                                    }
                                });

                        mIdentityView.setRightText(string);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("取消了");
                    }
                })
                .show();
    }


    //设置详细地址并上传
    private void setFullAddress(Context context) {
        new InputDialog.Builder(context)
                // 标题可以不用填写
                .setTitle(getString(R.string.personal_data_fulladdress_hint))
                .setContent(mFullAddressView.getRightText())
                //.setHint(getString(R.string.personal_data_name_hint))
                //.setConfirm("确定")
                // 设置 null 表示不显示取消按钮
                //.setCancel("取消")
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setListener((dialog, content) -> {
                    if (!mFullAddressView.getRightText().equals(content)) {

                        //更新数据库
                        EasyHttp.post(PersonalDataActivity.this)
                                .api(new UpdateFulladdressApi()
                                        .setUid(USERID)
                                        .setFulladdress(content))
                                .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("更新详细地址为："+ content);
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("更新详细地址", "onFail: "+e);
                                        toast("更新详细地址失败！");
                                    }
                                });
                        mFullAddressView.setRightText(content);
                    }
                })
                .show();
    }


    //设置地区并上传
    private void setAddress(Context context) {
        new AddressDialog.Builder(context)
                //.setTitle("选择地区")
                // 设置默认省份
                .setProvince(mProvince)
                // 设置默认城市（必须要先设置默认省份）
                .setCity(mCity)
                // 不选择县级区域
                //.setIgnoreArea()
                .setListener((dialog, province, city, area) -> {
                    String address = province + city + area;
                    if (!mAddressView.getRightText().equals(address)) {
                        mProvince = province;
                        mCity = city;
                        mArea = area;
                        //更新数据库
                        EasyHttp.post(PersonalDataActivity.this)
                                .api(new UpdateAddressApi()
                                        .setUid(USERID)
                                        .setAddress(address))
                                .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("更新地区为："+ address);
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("更新地区", "onFail: "+e);
                                        toast("更新地区失败！");
                                    }
                                });
                        mAddressView.setRightText(address);
                    }
                })
                .show();

        //更新数据库



    }


    //设置名字并上传
    private void setName(Context context, Integer uid) {
        new InputDialog.Builder(context)
                // 标题可以不用填写
                .setTitle(getString(R.string.personal_data_name_hint))
                .setContent(mNameView.getRightText())
                //.setHint(getString(R.string.personal_data_name_hint))
                //.setConfirm("确定")
                // 设置 null 表示不显示取消按钮
                //.setCancel("取消")
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setListener((dialog, content) -> {
                    if (!mNameView.getRightText().equals(content)) {
                        mNameView.setRightText(content);
                        //更新数据库
                        EasyHttp.post(this)
                                .api(new UpdateNameApi()
                                        .setUid(uid)
                                        .setName(mNameView.getRightText().toString()))
                                .request(new HttpCallback<HttpData<String>>(this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("更新名字为："+ mNameView.getRightText().toString());
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("更新姓名", "onFail: "+e);
                                        toast("更新姓名失败！");
                                    }
                                });
                    }
                })
                .show();


    }

    //设置年龄并上传
    private void setAge(Context context, Integer uid) {
        new InputDialog.Builder(context)
                // 标题可以不用填写
                .setTitle(getString(R.string.personal_data_age_hint))
                .setContent(mAgeView.getRightText())
                //.setHint(getString(R.string.personal_data_name_hint))
                //.setConfirm("确定")
                // 设置 null 表示不显示取消按钮
                //.setCancel("取消")
                // 设置点击按钮后不关闭对话框
                //.setAutoDismiss(false)
                .setListener((dialog, content) -> {
                    if (!mAgeView.getRightText().equals(content)) {
                        mAgeView.setRightText(content);
                        //更新数据库
                        EasyHttp.post(this)
                                .api(new UpdateAgeApi()
                                        .setUid(uid)
                                        .setAge(Integer.valueOf(mAgeView.getRightText().toString())))
                                .request(new HttpCallback<HttpData<String>>(this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("更新年龄为："+ mAgeView.getRightText().toString());
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("更新年龄", "onFail: "+e);
                                        toast("更新年龄失败！");
                                    }
                                });
                    }
                })
                .show();


    }


    //设置头像并上传
    private void setAvatarLayout() {
        ImageSelectActivity.start(this, data -> {

            if (true) {

                mAvatarUrl = data.get(0);
                Log.d("头像", "setAvatarLayout: "+ mAvatarUrl);
                long length = new File(data.get(0)).length();
                if ( length > 16*1024*1024){
                    Log.d("头像", "setAvatarLayout: 照片大于"+length+"B");
                    toast("上传头像不能大于16MB");
                    return;
                }
                GlideApp.with(getActivity())
                        .load(mAvatarUrl)
                        .into(mAvatarView);

            }
            // 上传头像
            EasyHttp.post(this)
                    .api(new UpdateAvatarApi()
                            .setUId(USERID)
                            .setAvatarPath(mAvatarUrl)
                            .setAvatar(new File(data.get(0))))
                    .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this) {
                        @Override
                        public void onSucceed(HttpData<String> data) {
                            toast("头像上传成功！");
                            GlideApp.with(getActivity())
                                    .load(mAvatarUrl)
                                    .into(mAvatarView);
                        }

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            Log.d("头像上传", "onFail: "+e);
                        }
                    });
        });
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }


}