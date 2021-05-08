package top.baixiaoshengzjj.mygraduationapp.http.server;

import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;

import top.baixiaoshengzjj.mygraduationapp.constant.NetConstant;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 正式环境
 */
public class ReleaseServer implements IRequestServer {

    @Override
    public String getHost() {
        return NetConstant.getBaseService();
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public BodyType getType(){
        return BodyType.FORM;
    }
}