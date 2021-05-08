package top.baixiaoshengzjj.mygraduationapp.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import top.baixiaoshengzjj.mygraduationapp.common.MyFragment;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetMedicalRecordApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetMedicalRecordPathApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.GetMedicalRecordPathBean;
import top.baixiaoshengzjj.mygraduationapp.ui.adapter.StatusAdapter;

/**
 *功能描述 加载病历
 * @author 百xiao生
 * @date 2021/4/29 21:39
 * @param  * @param null
 * @return
*/
public final class StatusFragment extends MyFragment<MyActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    private TextView headerView;
    private Bundle fragmentArgs;
    private Integer patientId;
    private List<Bitmap> data ;
    private boolean isLoaded;

    public static StatusFragment newInstance() {
        return new StatusFragment();
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private FrameLayout mframeLayout;
    private TextView mTextView;

    private StatusAdapter mAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        fragmentArgs = getArguments();
        patientId = fragmentArgs.getInt("patientId");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.status_fragment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_status_refresh);
        mRecyclerView = findViewById(R.id.rv_status_list);
        mframeLayout = findViewById(R.id.hl_status_hint);
        mTextView = findViewById(R.id.tv_status_hint);

        mAdapter = new StatusAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        headerView = mRecyclerView.addHeaderView(R.layout.picker_item);
        headerView.setText("下拉刷新");
        headerView.setOnClickListener(v -> toast("点击了头部"));

//        TextView footerView = mRecyclerView.addFooterView(R.layout.picker_item);
//        footerView.setText("添加");
//        footerView.setOnClickListener(v -> toast("点击了尾部"));

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        data = new ArrayList<>();
        mframeLayout.setForeground(getDrawable(R.drawable.hint_empty_ic));

        //加载病历
        loadMedicalRecord();
    }

    /**
     * 通过路径添加显示病历
     */
    private List<Bitmap> analogDataByUri(String pathName) {

        data.add(BitmapFactory.decodeFile(pathName));

        return data;
    }

    /**
     * 通过bitmap添加显示病历
     */
    private List<Bitmap> analogDataByBitmap(Bitmap bitmap) {
        data.add(Bitmap.createBitmap(bitmap));
        return data;
    }

    /**
     * 加载显示病历
     * */
    public void loadMedicalRecord(){
        isLoaded = false;
        EasyHttp.post(getAttachActivity())
                .api(new GetMedicalRecordPathApi()
                        .setUId(patientId))
                .request(new HttpCallback<HttpData<GetMedicalRecordPathBean[]>>(getAttachActivity()){
                    @Override
                    public void onSucceed(HttpData<GetMedicalRecordPathBean[]> result) {
                        Log.i("病历path加载", "onSucceed: 病历path获取成功");
                        //判空，保证健壮性，以免空指针异常导致闪退
                        if(result.getData() == null)
                        {
                            mframeLayout.setVisibility(View.VISIBLE);
                            mTextView.setVisibility(View.VISIBLE);
                            toast("加载完成！");
                            mRefreshLayout.finishRefresh();
                            return;
                        }

                        mframeLayout.setVisibility(View.INVISIBLE);
                        mTextView.setVisibility(View.INVISIBLE);

                        //接收回传的数据，以便下一步遍历数据
                        GetMedicalRecordPathBean[] getMedicalRecordPathBeans = result.getData();
                        //遍历数组，判断每张病历图片是否存在本地，
                        // 存在则直接从本地加载
                        for (int i = 0; i < result.getData().length; i++) {
                            Log.i("病历path加载", "onSucceed: "+getMedicalRecordPathBeans[i].getMedicalRecordPath());
                            if (new File(getMedicalRecordPathBeans[i].getMedicalRecordPath()).isFile()){
                                //直接从本地加载
                                mAdapter.setData(analogDataByUri(getMedicalRecordPathBeans[i].getMedicalRecordPath()));
                                Log.d("病历path加载", "onSucceed: "+getMedicalRecordPathBeans[i].getMedicalRecordPath());
                            }else {
                                getMedicalRecord(getMedicalRecordPathBeans[i].getId());
                            }
                        }

                        mRefreshLayout.finishRefresh();
                        toast("刷新完成");
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.i("病历加载", "onFail: 加载病历路径"+e);
                        toast("加载失败！");
                        mRefreshLayout.finishRefresh();
                    }
                });
    }

    /**
     *功能描述 从数据库获取病历图片数据
     * @author 百xiao生
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
//                toast("加载完成！");
//                mframeLayout.setVisibility(View.VISIBLE);
//                mTextView.setVisibility(View.VISIBLE);
//                return;
//            }
//            //有数据不显示“空空如也”
//            mframeLayout.setVisibility(View.INVISIBLE);
//            mTextView.setVisibility(View.INVISIBLE);
//
//            byte[] record = new byte[0];
//            try {
//                //统一编码，以免图片信息在多次byte[]和String转过程中产生错误
//                record = result.getData().getBytes("ISO_8859_1");
//            } catch (UnsupportedEncodingException e) {
//                toast("加载病历失败！");
//                e.printStackTrace();
//            }
//
//            Bitmap bitmap = BitmapFactory.decodeByteArray(record,0,record.length);
//            mAdapter.setData(analogDataByBitmap(bitmap));
//
//        } catch (Exception e) {
//            toast("加载病历失败！");
//            e.printStackTrace();
//        }

        EasyHttp.post(getAttachActivity())
                .api(new GetMedicalRecordApi()
                        .setId(id))
                .request(new HttpCallback<HttpData<String>>(getAttachActivity()){
                    @Override
                    public void onSucceed(HttpData<String> result) {
                        if (result.getData() == null){
                            toast("加载完成！");
                            return;
                        }

                        byte[] record = new byte[0];
                        try {
                            //统一编码，以免图片信息在多次byte[]和String转过程中产生错误
                            record = result.getData().getBytes("ISO_8859_1");
                        } catch (UnsupportedEncodingException e) {
                            toast("加载病历失败！");
                            e.printStackTrace();
                        }

                        Bitmap bitmap = BitmapFactory.decodeByteArray(record,0,record.length);

                        mAdapter.setData(analogDataByBitmap(bitmap));
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.i("病历加载", "onFail: 加载病历数据"+e);
                        toast("加载失败！");
                        mRefreshLayout.finishRefresh();
                    }
                });
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        //toast(mAdapter.getItem(position));
        //可进一步优化，实现点击查看大图
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     *下拉刷新
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (headerView != null){
            Log.d("TAG", "onRefresh: 不为空");
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
            toast("加载完成");
        }, 0);
    }

}