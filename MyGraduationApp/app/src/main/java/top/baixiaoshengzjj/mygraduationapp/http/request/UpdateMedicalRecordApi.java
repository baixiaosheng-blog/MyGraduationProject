package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 *功能描述 上传病历
 * @author 百xiao生
 * @date 2021/4/27 20:53
 * @param  * @param null
 * @return
*/
public final class UpdateMedicalRecordApi implements IRequestApi, IRequestClient {

    @Override
    public String getApi() {
        return "user/update/medicalRecord";
    }

    /** 用户Id */
    private Integer uId;
    /** 图片路径 */
    private String recordPath;
    /** 图片文件 */
    private File record;

    public UpdateMedicalRecordApi setRecord(File record) {
        this.record = record;
        return this;
    }

    public UpdateMedicalRecordApi setRecordPath(String imagePath) {
        this.recordPath = imagePath;
        return this;
    }

    public UpdateMedicalRecordApi setUId(Integer uId) {
        this.uId = uId;
        return this;
    }
    //上传下载慢，可能是服务器带宽问题
    //测试用的natapp内网穿透，1m带宽 ≈ 125k/s
    @Override
    public OkHttpClient getClient() {
        OkHttpClient.Builder builder = EasyConfig.getInstance().getClient().newBuilder();
        builder.readTimeout(5000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
        return builder.build();
    }
}