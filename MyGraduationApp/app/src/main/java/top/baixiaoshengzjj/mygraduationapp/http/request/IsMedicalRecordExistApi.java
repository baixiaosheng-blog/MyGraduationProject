package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;

/**
 *功能描述 上传病历
 * @author 百xiao生
 * @date 2021/4/27 20:53
 * @param  * @param null
 * @return
*/
public final class IsMedicalRecordExistApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/update/isMedicalRecordExist";
    }

    /** 用户Id */
    private Integer uId;
    /** 图片路径 */
    private String recordPath;


    public IsMedicalRecordExistApi setRecordPath(String imagePath) {
        this.recordPath = imagePath;
        return this;
    }

    public IsMedicalRecordExistApi setUId(Integer uId) {
        this.uId = uId;
        return this;
    }
}