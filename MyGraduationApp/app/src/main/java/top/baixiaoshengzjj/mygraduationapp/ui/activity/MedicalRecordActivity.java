package top.baixiaoshengzjj.mygraduationapp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import top.baixiaoshengzjj.mygraduationapp.R;
import top.baixiaoshengzjj.mygraduationapp.common.MyActivity;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetMedicalRecordApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetMedicalRecordPathApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.GetMedicalRecordPathBean;
import top.baixiaoshengzjj.mygraduationapp.ui.adapter.StatusAdapter;
import top.baixiaoshengzjj.mygraduationapp.ui.fragment.StatusFragment;

public class MedicalRecordActivity extends MyActivity implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    private TextView headerView;
    private Bundle fragmentArgs;
    private Integer patientId;
    private List<Bitmap> data ;

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private FrameLayout mframeLayout;
    private TextView mTextView;

    private StatusAdapter mAdapter;

    private StatusFragment statusFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_medical_record;
    }

    @Override
    protected void initView() {
//        statusFragment = new StatusFragment();
//        Bundle args = new Bundle();
//        Intent intent = getIntent();

//        args.putInt("patientId",intent.getIntExtra("patientId", 0));
//        statusFragment.setArguments(args);
//        getSupportFragmentManager().beginTransaction().add(R.id.medical_record_fragment, statusFragment).commit();
        Intent intent = getIntent();
        patientId = intent.getIntExtra("patientId", 0);

        mRefreshLayout = findViewById(R.id.rl_status_refresh);
        mRecyclerView = findViewById(R.id.rv_status_list);
        mframeLayout = findViewById(R.id.hl_status_hint);
        mTextView = findViewById(R.id.tv_status_hint);

        mAdapter = new StatusAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        headerView = mRecyclerView.addHeaderView(R.layout.picker_item);
        headerView.setText("????????????");
        headerView.setOnClickListener(v -> toast("???????????????"));

        mRefreshLayout.setOnRefreshLoadMoreListener(this);

    }

    @Override
    protected void initData() {
        data = new ArrayList<>();
        mframeLayout.setForeground(getDrawable(R.drawable.hint_empty_ic));

        //????????????
        loadMedicalRecord();
    }


    /**
     * ??????????????????????????????
     */
    private List<Bitmap> analogDataByUri(String pathName) {

        data.add(BitmapFactory.decodeFile(pathName));

        return data;
    }

    /**
     * ??????bitmap??????????????????
     */
    private List<Bitmap> analogDataByBitmap(Bitmap bitmap) {
        data.add(Bitmap.createBitmap(bitmap));
        return data;
    }

    /**
     * ??????????????????
     * */
    public void loadMedicalRecord(){
        EasyHttp.post(this)
                .api(new GetMedicalRecordPathApi()
                        .setUId(patientId))
                .request(new HttpCallback<HttpData<GetMedicalRecordPathBean[]>>(this){
                    @Override
                    public void onSucceed(HttpData<GetMedicalRecordPathBean[]> result) {
                        Log.i("??????path??????", "onSucceed: ??????path????????????");
                        //????????????????????????????????????????????????????????????
                        if(result.getData() == null)
                        {
                            mframeLayout.setVisibility(View.VISIBLE);
                            //mTextView.setVisibility(View.VISIBLE);
                            toast("???????????????");
                            mRefreshLayout.finishRefresh();
                            return;
                        }

                        mframeLayout.setVisibility(View.INVISIBLE);
                        //mTextView.setVisibility(View.INVISIBLE);

                        //???????????????????????????????????????????????????
                        GetMedicalRecordPathBean[] getMedicalRecordPathBeans = result.getData();
                        //????????????????????????????????????????????????????????????
                        // ??????????????????????????????
                        for (int i = 0; i < result.getData().length; i++) {
                            Log.i("??????path??????", "onSucceed: "+getMedicalRecordPathBeans[i].getMedicalRecordPath());
                            if (new File(getMedicalRecordPathBeans[i].getMedicalRecordPath()).isFile()){
                                //?????????????????????
                                mAdapter.setData(analogDataByUri(getMedicalRecordPathBeans[i].getMedicalRecordPath()));
                                Log.d("??????path??????", "onSucceed: "+getMedicalRecordPathBeans[i].getMedicalRecordPath());
                            }else {
                                getMedicalRecord(getMedicalRecordPathBeans[i].getId());
                            }
                        }

                        mRefreshLayout.finishRefresh();
                        toast("????????????");
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.i("????????????", "onFail: ??????????????????"+e);
                        toast("???????????????");
                        mRefreshLayout.finishRefresh();
                    }
                });
    }

    /**
     *???????????? ????????????????????????????????????
     * @author ???xiao???
     * @date 2021/4/29 15:51
     * @param  * @param id
     * @return void
     */
    private void getMedicalRecord(Integer id) {

//        try {
//            HttpData<String> result = EasyHttp.post(getAttachActivity())
//                    .api(new GetMedicalRecordApi()
//                            .setId(id))
//                    .execute(new ResponseClass<HttpData<String>>() {});
//            if (result.getData() == null){
//                toast("???????????????");
//                mframeLayout.setVisibility(View.VISIBLE);
//                mTextView.setVisibility(View.VISIBLE);
//                return;
//            }
//            //????????????????????????????????????
//            mframeLayout.setVisibility(View.INVISIBLE);
//            mTextView.setVisibility(View.INVISIBLE);
//
//            byte[] record = new byte[0];
//            try {
//                //??????????????????????????????????????????byte[]???String????????????????????????
//                record = result.getData().getBytes("ISO_8859_1");
//            } catch (UnsupportedEncodingException e) {
//                toast("?????????????????????");
//                e.printStackTrace();
//            }
//
//            Bitmap bitmap = BitmapFactory.decodeByteArray(record,0,record.length);
//            mAdapter.setData(analogDataByBitmap(bitmap));
//
//        } catch (Exception e) {
//            toast("?????????????????????");
//            e.printStackTrace();
//        }

        EasyHttp.post(this)
                .api(new GetMedicalRecordApi()
                        .setId(id))
                .request(new HttpCallback<HttpData<String>>(this){
                    @Override
                    public void onSucceed(HttpData<String> result) {
                        if (result.getData() == null){
                            toast("???????????????");
                            return;
                        }

                        byte[] record = new byte[0];
                        try {
                            //??????????????????????????????????????????byte[]???String????????????????????????
                            record = result.getData().getBytes("ISO_8859_1");
                        } catch (UnsupportedEncodingException e) {
                            toast("?????????????????????");
                            e.printStackTrace();
                        }

                        Bitmap bitmap = BitmapFactory.decodeByteArray(record,0,record.length);

                        mAdapter.setData(analogDataByBitmap(bitmap));
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.i("????????????", "onFail: ??????????????????"+e);
                        toast("???????????????");
                        mRefreshLayout.finishRefresh();
                    }
                });
    }


    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView      RecyclerView??????
     * @param itemView          ????????????????????????
     * @param position          ????????????????????????
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        //toast(mAdapter.getItem(position));
        //?????????????????????????????????????????????
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     *????????????
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (headerView != null){
            Log.d("TAG", "onRefresh: ?????????");
            mRecyclerView.removeHeaderView(headerView);
            headerView = null;
        }
        mAdapter.clearData();
        loadMedicalRecord();


    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
//            mframeLayout.setVisibility(View.INVISIBLE);
//            mAdapter.addData(analogData());
            mRefreshLayout.finishLoadMore();
            toast("????????????");
        }, 0);
    }


    @Override
    public boolean isStatusBarEnabled() {
        // ????????????????????????
        return !super.isStatusBarEnabled();
    }

}