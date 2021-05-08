package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;

import java.io.File;

/**
 *功能描述 上传头像
 * @author 百xiao生
 * @date 2021/4/27 20:53
 * @param  * @param null
 * @return
*/
public final class UpdateAvatarApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/update/avatar";
    }

    /** 用户Id */
    private Integer uId;
    /** 图片路径 */
    private String avatarPath;
    /** 图片文件 */
    @HttpRename("avatar")
    private File avatar;

    public UpdateAvatarApi setAvatar(File avatar) {
        this.avatar = avatar;
        return this;
    }

    public UpdateAvatarApi setAvatarPath(String imagePath) {
        this.avatarPath = imagePath;
        return this;
    }

    public UpdateAvatarApi setUId(Integer uId) {
        this.uId = uId;
        return this;
    }
}