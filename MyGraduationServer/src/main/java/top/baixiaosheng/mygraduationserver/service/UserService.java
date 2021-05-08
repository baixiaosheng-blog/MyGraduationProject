package top.baixiaosheng.mygraduationserver.service;

import top.baixiaosheng.mygraduationserver.dataobject.AvatarDO;
import top.baixiaosheng.mygraduationserver.dataobject.MedicalRecordDO;
import top.baixiaosheng.mygraduationserver.dataobject.UserDO;
import top.baixiaosheng.mygraduationserver.dataobject.UserPasswordDO;
import top.baixiaosheng.mygraduationserver.error.BusinessException;
import top.baixiaosheng.mygraduationserver.response.PersonalData;
import top.baixiaosheng.mygraduationserver.service.model.UserModel;

public interface UserService {
    //通过用户ID获取用户对象的方法
    UserModel getUserById(Integer uid);

    boolean getUserByEmail(String email);

    void register(UserModel userModel) throws BusinessException;

    UserModel validateLogin(String email, String encryptPassword) throws BusinessException;

    /**********---头像----*****************/
    public boolean isAvatarExist(Integer uid);

    public void avataUpdate(AvatarDO avatarDO, Boolean exist) throws BusinessException;

    public AvatarDO avatarLoading(Integer uid);

    /*******-----病历-------
     * @return********/
    public boolean isMedicalRecordExist(Integer uid, String recordPath);

    public void medicalRecordUpdate(MedicalRecordDO medicalRecordDO, Boolean exist) throws BusinessException;

    //public MedicalRecordPathModel[] getMedicalRecordPath(Integer uid);
    public MedicalRecordDO[] getMedicalRecordPath(Integer uid);

    public MedicalRecordDO getMedicalRecord(Integer id);

    public PersonalData getPersonalData(Integer uId);

    int updateUserInfo(UserDO userDO) throws BusinessException;

    int updateUserPassword(UserPasswordDO passwordDO) throws BusinessException;
}
