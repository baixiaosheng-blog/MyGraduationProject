package top.baixiaoshengzjj.mygraduationapp.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import top.baixiaoshengzjj.mygraduationapp.common.MyFragment;
import top.baixiaoshengzjj.mygraduationapp.http.model.HttpData;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetMedicalRecordApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.GetMedicalRecordPathApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.IsMedicalRecordExistApi;
import top.baixiaoshengzjj.mygraduationapp.http.request.UpdateMedicalRecordApi;
import top.baixiaoshengzjj.mygraduationapp.http.response.GetMedicalRecordPathBean;
import top.baixiaoshengzjj.mygraduationapp.ui.activity.ImageSelectActivity;
import top.baixiaoshengzjj.mygraduationapp.ui.adapter.StatusAdapter;

import static top.baixiaoshengzjj.mygraduationapp.constant.ModelConstant.USERID;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/07/10
 *    desc   : 加载使用案例
 */
public final class AddMedicalRecordFragment extends MyFragment<MyActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    private TextView headerView;
    private String medicalRecordUrl;
    private List<Bitmap> data;

    public static AddMedicalRecordFragment newInstance() {
        return new AddMedicalRecordFragment();
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;
    private FrameLayout mframeLayout;

    private StatusAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_medical_record;
    }

    @Override
    protected void initView() {

        mRefreshLayout = findViewById(R.id.rl_record_refresh);
        mRecyclerView = findViewById(R.id.rv_record_list);
        mframeLayout = findViewById(R.id.hl_record_hint);

        mAdapter = new StatusAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        headerView = mRecyclerView.addHeaderView(R.layout.picker_item);
        headerView.setText("下拉刷新");
        headerView.setOnClickListener(v -> toast("点击了头部"));

        TextView footerView = mRecyclerView.addFooterView(R.layout.picker_item);
        footerView.setText("添加");
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicalRecord();
            }
        });

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
     * 加载显示病历
     * */
    public void loadMedicalRecord(){
        EasyHttp.post(getAttachActivity())
                .api(new GetMedicalRecordPathApi()
                        .setUId(USERID))
                .request(new HttpCallback<HttpData<GetMedicalRecordPathBean[]>>(getAttachActivity()){
                    @Override
                    public void onSucceed(HttpData<GetMedicalRecordPathBean[]> result) {
                        Log.i("病历path加载", "onSucceed: 病历path获取成功");
                        //判空，保证健壮性，以免空指针异常导致闪退
                        if(result.getData() == null || result == null)
                        {
                            mframeLayout.setVisibility(View.VISIBLE);
                            toast("加载完成！");
                            mRefreshLayout.finishRefresh();
                            return;
                        }

                        mframeLayout.setVisibility(View.INVISIBLE);

                        //接收回传的数据，以便下一步遍历数据
                        GetMedicalRecordPathBean[] getMedicalRecordPathBeans = result.getData();
                        //遍历数组，判断每张病历图片是否存在本地，
                        // 存在则直接从本地加载
                        for (int i = 0; i < result.getData().length; i++) {
                            Log.i("病历path加载", "onSucceed: "+getMedicalRecordPathBeans[i].getMedicalRecordPath());
                            if (new File(getMedicalRecordPathBeans[i].getMedicalRecordPath()).isFile()){
                                //直接从本地加载
                                mAdapter.setData(analogData(getMedicalRecordPathBeans[i].getMedicalRecordPath()));
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
     *功能描述 添加病历图片并上传
     * @author 百xiao生
     * @date 2021/4/29 22:23
     * @param  * @param
     * @return void
    */
    private void addMedicalRecord() {
        //跳转到图片选择界面
        ImageSelectActivity.start(getAttachActivity(), data -> {
            if (true) {
                medicalRecordUrl = data.get(0);
//                Log.d("头像", "setAvatarLayout: "+ mAvatarUrl);
                long length = new File(data.get(0)).length();
                if ( length > 16*1024*1024){
                    Log.d("病历", "setAvatarLayout: 照片大于"+length+"B");
                    toast("上传的图片不能大于16MB");
                    return;
                }
                //上传病历，先查询所选图片是否已上传过（用户id + 图片路径联合查询）
                EasyHttp.post(getAttachActivity())
                        .api(new IsMedicalRecordExistApi()
                                .setUId(USERID)
                                .setRecordPath(medicalRecordUrl))
                        .request(new HttpCallback<HttpData<String>>(getAttachActivity()){
                            @Override
                            public void onSucceed(HttpData<String> result) {
                                //updateMedicalRecord();
                                if (result != null && result.getData() != null){
                                    Log.d("病历上传", "onSucceed: 重复上传-"+result.getData()+"-");
                                    toast("照片已存在！");
                                }else {
                                    //上传病历图片
                                    Log.i("病历上传", "onSucceed: 开始上传病历");
                                    updateMedicalRecord();
                                }
                            }
                            @Override
                            public void onFail(Exception e) {
                                toast("图片上传失败！");
                                Log.d("病历上传", "onFail: 查询病历是否存在，失败"+e);
                            }
                        });
                mframeLayout.setVisibility(View.INVISIBLE);
                mAdapter.setData(analogData(medicalRecordUrl));
                mRefreshLayout.finishLoadMore();
            }
        });
    }

    //上传病历图片
    private void updateMedicalRecord() {
        //上传下载慢，可能是服务器带宽问题
        //测试用的natapp内网穿透，1m带宽 ≈ 125k/s
        EasyHttp.post(getAttachActivity())
                .api(new UpdateMedicalRecordApi()
                        .setUId(USERID)
                        .setRecordPath(medicalRecordUrl)
                        .setRecord(new File(medicalRecordUrl)))
                .request(new HttpCallback<HttpData<String>>(getAttachActivity()){
                    @Override
                    public void onSucceed(HttpData<String> result) {
                        toast("图片上传成功！");
                    }
                    @Override
                    public void onFail(Exception e) {
                        toast("图片上传失败！");
                        Log.d("病历上传", "onFail: "+e);
                    }
                });
    }


    /**
     * 添加数据
     */
    private List<Bitmap> analogData(String path) {

        data.add(BitmapFactory.decodeFile(path));

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
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        toast(mAdapter.getItem(position));
    }

    /**
     * {@link OnRefreshLoadMoreListener}
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

//    @Override
//    public HintLayout getHintLayout() {
//        return mHintLayout;
//    }
}