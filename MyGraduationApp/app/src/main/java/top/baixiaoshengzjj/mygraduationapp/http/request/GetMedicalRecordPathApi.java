package top.baixiaoshengzjj.mygraduationapp.http.request;

import com.hjq.http.config.IRequestApi;


/**
 *功能描述 获取病历图片的路径
 * @author 百xiao生
 * @date 2021/4/27 20:54
 * @param  * @param null
 * @return
*/
public class GetMedicalRecordPathApi implements IRequestApi{
    @Override
    public String getApi() {
        return "user/getMedicalRecordPath";
    }

    /** 患者用户的id */
    private Integer uId;

    public GetMedicalRecordPathApi setUId(Integer uId){
        this.uId = uId;
        return this;
    }

}
