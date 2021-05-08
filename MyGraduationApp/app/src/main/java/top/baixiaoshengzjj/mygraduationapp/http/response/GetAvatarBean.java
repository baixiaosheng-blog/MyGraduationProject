package top.baixiaoshengzjj.mygraduationapp.http.response;

public class GetAvatarBean {
    private Integer uid;
    private String avatarPath;

    /** 头像数据 */
    private String avatar;

    public GetAvatarBean() {
    }

    public GetAvatarBean(Integer uid, String avatarPath, String avatar) {
        this.uid = uid;
        this.avatarPath = avatarPath;
        this.avatar = avatar;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
