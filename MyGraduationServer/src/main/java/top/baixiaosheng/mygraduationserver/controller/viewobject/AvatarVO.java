package top.baixiaosheng.mygraduationserver.controller.viewobject;

import java.io.File;

public class AvatarVO {
    private Integer uid;
    private String avatarPath;
    private String avatar;

    public AvatarVO() {
    }

    public AvatarVO(Integer uid, String avatarPath, String avatar) {
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
