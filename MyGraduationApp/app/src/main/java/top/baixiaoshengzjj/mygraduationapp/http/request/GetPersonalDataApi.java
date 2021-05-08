package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestClient;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 *功能描述 获取头像数据api
 * @author 百xiao生
 * @date 2021/4/20 22:48
 * @param  * @param null
 * @return
*/
public final class GetPersonalDataApi implements IRequestApi, IRequestClient {
    @Override
    public String getApi() {
        return "user/info/getPersonalData";
    }


    /** 用户id */
    private Integer uId;

    public GetPersonalDataApi setUId(Integer  uId) {
        this.uId = uId;
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
