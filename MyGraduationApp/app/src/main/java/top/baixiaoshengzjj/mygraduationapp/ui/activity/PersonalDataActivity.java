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
 *    author : Android ?????????
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/04/20
 *    desc   : ????????????
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



    /** ??? */
    private String mProvince = "?????????";
    /** ??? */
    private String mCity = "?????????";
    /** ??? */
    private String mArea = "?????????";
    /** ??????*/
    private String normalUser = "????????????";
    private String nurseUser = "??????????????????";
    private String doctorUser = "??????????????????";
    /** ???????????? */
    private String mAvatarUrl;
    /** ?????? */
    private String mName;
    /** ?????? */
    private Integer mAge;
    /** ?????? */
    private String mAddress;
    /** ???????????? */
    private String mFullAddress;
    /** ??????????????? */
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
            //???????????????1?????????????????????2???????????????3???????????????????????????1
            mIdentityView.setRightText(normalUser);
        }else if (IDENTITY == 2){
            mIdentityView.setRightText(nurseUser);
        }else if (IDENTITY == 3){
            mIdentityView.setRightText(doctorUser);
        }
        mAddressView.setRightText(address);         //??????
        mIDView.setRightText(USERID.toString());    //??????id

        //?????????????????????????????????????????????????????????
        loadAvatar();
        loadPersonalData(USERID);


    }



    /**
     *???????????? ??????????????????????????????????????????
     * @author ???xiao???
     * @date 2021/4/23 1:23
     * @param  * @param userid
     * @return void
    */
    private void loadPersonalData(Integer userid) {
        BaseDialog waitDialog = new WaitDialog.Builder(this)    //???????????????
                // ??????????????????????????????
                .setMessage("?????????...")
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
                        //??????
                        if (USERGENDER == 0){
                            mGenderView.setRightText("???");
                        }else if (USERGENDER == 1){
                            mGenderView.setRightText("???");
                        }
                        //??????
                        mNameView.setRightText(mName);
                        //??????
                        mAgeView.setRightText(mAge.toString());
                        //??????
                        mAddressView.setRightText(mAddress);
                        //????????????
                        mFullAddressView.setRightText(mFullAddress);
                        //???????????????
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
     *???????????? ??????????????????
     * @author ???xiao???
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
                        Log.i("??????path??????", "onSucceed: ??????path????????????");
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
                            Log.i("????????????path", "onSucceed: ????????????????????????");
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
                        Log.d("???????????????????????????", "onFail: "+e);
                        toast("?????????????????????");

                        //?????????????????????????????????????????????????????????????????????
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
     * ??????????????????
     */
    private void loadAvatarRes() {
        //????????????????????????????????????????????????
        //????????????natapp???????????????1m?????? ??? 125k/s

        EasyHttp.post(this)
                .api(new GetAvatarApi()
                        .setUId(USERID))
                .request(new HttpCallback<HttpData<GetAvatarBean>>(this) {

                    @Override
                    public void onSucceed(HttpData<GetAvatarBean> data) {
                        Log.d("????????????", "onSucceed: ????????????????????????");
                        if (data.getData() == null){
                            //?????????????????????
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
                            //??????????????????????????????????????????byte[]???String????????????????????????
                            avatar = data.getData().getAvatar().getBytes("ISO_8859_1");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.d("????????????", "onSucceed: ????????????"+ avatar.length);


                        Bitmap bitmap = BitmapFactory.decodeByteArray(avatar,0,avatar.length);
                        Log.d("????????????", "onSucceed:bitmap "+bitmap);
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
                        Log.d("????????????", "onFail: ??????????????????"+e);
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


    //???????????????????????????
    @SingleClick
    @Override
    public void onClick(View v) {
        if (v == mAvatarLayout) {
            setAvatarLayout();
        } else if (v == mAvatarView) {
            if (!TextUtils.isEmpty(mAvatarUrl)) {
                // ????????????
                ImagePreviewActivity.start(getActivity(), mAvatarUrl);
            } else {
                // ????????????
                onClick(mAvatarLayout);
            }
        } else if (v == mNameView) {
            setName(this, USERID);//???????????????????????????
        } else if (v == mAgeView){
            setAge(this, USERID);//???????????????????????????
        } else if (v == mGenderView){
            setGender(this, USERID);//???????????????????????????
        } else if (v == mAddressView) {
            setAddress(this);           //???????????????????????????
        }else if (v == mFullAddressView){
            setFullAddress(this);       //?????????????????????????????????
        }else if (v == mIdentityView){
            setIdentity(this);          //???????????????????????????
        }else if (v == mEmergencyContactView){
            setEmergencyContact(this, USERID);//??????????????????????????????
        }
    }


    //?????????????????????
    private void setEmergencyContact(Context context, Integer uid) {
        new InputDialog.Builder(context)
                // ????????????????????????
                .setTitle(getString(R.string.personal_data_contact_hint))//???????????????
                .setContent(mEmergencyContactView.getRightText())
                .setListener((dialog, content) -> {
                    if (!mEmergencyContactView.getRightText().equals(content)) {
                        mEmergencyContactView.setRightText(content);
                        //???????????????
                        EasyHttp.post(this)
                                .api(new UpdateContactApi()
                                        .setUid(uid)
                                        .setContact(mEmergencyContactView.getRightText().toString()))
                                .request(new HttpCallback<HttpData<String>>(this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("?????????????????????"+ mEmergencyContactView.getRightText().toString());
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("???????????????", "onFail: "+e);
                                        toast("????????????????????????");
                                    }
                                });
                    }
                })
                .show();
    }


    //????????????
    private void setGender(Context context, Integer uid) {
        List<String> data1 = new ArrayList<>();
        data1.add("???");
        data1.add("???");

        // ???????????????
        new MenuDialog.Builder(context)
                .setGravity(Gravity.CENTER)
                // ?????? null ???????????????????????????
                //.setCancel(null)
                // ???????????????????????????????????????
                //.setAutoDismiss(false)
                .setList(data1)
                .setListener(new MenuDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, int position, String string) {

                        if (string == "???"){
                            USERGENDER = 1;
                        }else if (string == "???"){
                            USERGENDER = 0;
                        }
                        //???????????????
                        //updataGender(uid);
                        EasyHttp.post(PersonalDataActivity.this)
                                .api(new UpdateGenderApi()
                                        .setUid(uid)
                                        .setGender(USERGENDER))
                                .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("??????????????????"+string);
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("????????????", "onFail: "+e);
                                        toast("?????????????????????");
                                    }
                                });
                        mGenderView.setRightText(string);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("?????????");
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
                        toast("??????????????????");
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.d("????????????", "onFail: "+e);
                        toast("?????????????????????");
                    }
                });
    }



    //?????????????????????
    private void setIdentity(Context context) {
        List<String> data1 = new ArrayList<>();
        data1.add(normalUser);
        data1.add(nurseUser);
        data1.add(doctorUser);

        // ???????????????
        new MenuDialog.Builder(context)
                .setGravity(Gravity.CENTER)
                // ?????? null ???????????????????????????
                //.setCancel(null)
                // ???????????????????????????????????????
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
                        //???????????????
                        EasyHttp.post(PersonalDataActivity.this)
                                .api(new UpdateIdentityApi()
                                        .setUid(USERID)
                                        .setIdentity(IDENTITY))
                                .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("??????????????????"+ string + "?????????App??????");
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("????????????", "onFail: "+e);
                                        toast("?????????????????????");
                                    }
                                });

                        mIdentityView.setRightText(string);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("?????????");
                    }
                })
                .show();
    }


    //???????????????????????????
    private void setFullAddress(Context context) {
        new InputDialog.Builder(context)
                // ????????????????????????
                .setTitle(getString(R.string.personal_data_fulladdress_hint))
                .setContent(mFullAddressView.getRightText())
                //.setHint(getString(R.string.personal_data_name_hint))
                //.setConfirm("??????")
                // ?????? null ???????????????????????????
                //.setCancel("??????")
                // ???????????????????????????????????????
                //.setAutoDismiss(false)
                .setListener((dialog, content) -> {
                    if (!mFullAddressView.getRightText().equals(content)) {

                        //???????????????
                        EasyHttp.post(PersonalDataActivity.this)
                                .api(new UpdateFulladdressApi()
                                        .setUid(USERID)
                                        .setFulladdress(content))
                                .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("????????????????????????"+ content);
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("??????????????????", "onFail: "+e);
                                        toast("???????????????????????????");
                                    }
                                });
                        mFullAddressView.setRightText(content);
                    }
                })
                .show();
    }


    //?????????????????????
    private void setAddress(Context context) {
        new AddressDialog.Builder(context)
                //.setTitle("????????????")
                // ??????????????????
                .setProvince(mProvince)
                // ??????????????????????????????????????????????????????
                .setCity(mCity)
                // ?????????????????????
                //.setIgnoreArea()
                .setListener((dialog, province, city, area) -> {
                    String address = province + city + area;
                    if (!mAddressView.getRightText().equals(address)) {
                        mProvince = province;
                        mCity = city;
                        mArea = area;
                        //???????????????
                        EasyHttp.post(PersonalDataActivity.this)
                                .api(new UpdateAddressApi()
                                        .setUid(USERID)
                                        .setAddress(address))
                                .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("??????????????????"+ address);
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("????????????", "onFail: "+e);
                                        toast("?????????????????????");
                                    }
                                });
                        mAddressView.setRightText(address);
                    }
                })
                .show();

        //???????????????



    }


    //?????????????????????
    private void setName(Context context, Integer uid) {
        new InputDialog.Builder(context)
                // ????????????????????????
                .setTitle(getString(R.string.personal_data_name_hint))
                .setContent(mNameView.getRightText())
                //.setHint(getString(R.string.personal_data_name_hint))
                //.setConfirm("??????")
                // ?????? null ???????????????????????????
                //.setCancel("??????")
                // ???????????????????????????????????????
                //.setAutoDismiss(false)
                .setListener((dialog, content) -> {
                    if (!mNameView.getRightText().equals(content)) {
                        mNameView.setRightText(content);
                        //???????????????
                        EasyHttp.post(this)
                                .api(new UpdateNameApi()
                                        .setUid(uid)
                                        .setName(mNameView.getRightText().toString()))
                                .request(new HttpCallback<HttpData<String>>(this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("??????????????????"+ mNameView.getRightText().toString());
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("????????????", "onFail: "+e);
                                        toast("?????????????????????");
                                    }
                                });
                    }
                })
                .show();


    }

    //?????????????????????
    private void setAge(Context context, Integer uid) {
        new InputDialog.Builder(context)
                // ????????????????????????
                .setTitle(getString(R.string.personal_data_age_hint))
                .setContent(mAgeView.getRightText())
                //.setHint(getString(R.string.personal_data_name_hint))
                //.setConfirm("??????")
                // ?????? null ???????????????????????????
                //.setCancel("??????")
                // ???????????????????????????????????????
                //.setAutoDismiss(false)
                .setListener((dialog, content) -> {
                    if (!mAgeView.getRightText().equals(content)) {
                        mAgeView.setRightText(content);
                        //???????????????
                        EasyHttp.post(this)
                                .api(new UpdateAgeApi()
                                        .setUid(uid)
                                        .setAge(Integer.valueOf(mAgeView.getRightText().toString())))
                                .request(new HttpCallback<HttpData<String>>(this){
                                    @Override
                                    public void onSucceed(HttpData<String> result) {
                                        toast("??????????????????"+ mAgeView.getRightText().toString());
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        Log.d("????????????", "onFail: "+e);
                                        toast("?????????????????????");
                                    }
                                });
                    }
                })
                .show();


    }


    //?????????????????????
    private void setAvatarLayout() {
        ImageSelectActivity.start(this, data -> {

            if (true) {

                mAvatarUrl = data.get(0);
                Log.d("??????", "setAvatarLayout: "+ mAvatarUrl);
                long length = new File(data.get(0)).length();
                if ( length > 16*1024*1024){
                    Log.d("??????", "setAvatarLayout: ????????????"+length+"B");
                    toast("????????????????????????16MB");
                    return;
                }
                GlideApp.with(getActivity())
                        .load(mAvatarUrl)
                        .into(mAvatarView);

            }
            // ????????????
            EasyHttp.post(this)
                    .api(new UpdateAvatarApi()
                            .setUId(USERID)
                            .setAvatarPath(mAvatarUrl)
                            .setAvatar(new File(data.get(0))))
                    .request(new HttpCallback<HttpData<String>>(PersonalDataActivity.this) {
                        @Override
                        public void onSucceed(HttpData<String> data) {
                            toast("?????????????????????");
                            GlideApp.with(getActivity())
                                    .load(mAvatarUrl)
                                    .into(mAvatarView);
                        }

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            Log.d("????????????", "onFail: "+e);
                        }
                    });
        });
    }

    @Override
    public boolean isStatusBarEnabled() {
        // ????????????????????????
        return !super.isStatusBarEnabled();
    }


}