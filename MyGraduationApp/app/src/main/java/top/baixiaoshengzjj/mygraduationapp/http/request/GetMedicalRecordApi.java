package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestClient;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 *功能描述 获取病历
 * @author 百xiao生
 * @date 2021/4/27 20:54
 * @param  * @param null
 * @return
*/
public class GetMedicalRecordApi implements IRequestApi, IRequestClient {
    @Override
    public String getApi() {
        return "user/getMedicalRecord";
    }

    /** 病历图片id */
    private Integer id;

    public GetMedicalRecordApi setId(Integer id){
        this.id = id;
        return this;
    }

    @Override
    public OkHttpClient getClient() {
        OkHttpClient.Builder builder = EasyConfig.getInstance().getClient().newBuilder();
        builder.readTimeout(5000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
        return builder.build();
    }

}
