package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 *功能描述 检测头像URL是否存在，存在则从本地加载，
 * 不存在则调用GetAvatarApi从数据库加载
 *
 * @author 百xiao生
 * @date 2021/4/20 20:05
 * @param  * @param null
 * @return
*/
public final class GetAvatarPathApi implements IRequestApi {
    @Override
    public String getApi() {
        return "user/load/avatarPath";
    }
    private Integer uId;

    public GetAvatarPathApi setUId(Integer  uId) {
        this.uId = uId;
        return this;
    }
}
