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
public final class GetAvatarApi implements IRequestApi, IRequestClient {
    @Override
    public String getApi() {
        return "user/load/avatar";
    }


    /** 用户id */
    private Integer uId;

    public GetAvatarApi setUId(Integer  uId) {
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
