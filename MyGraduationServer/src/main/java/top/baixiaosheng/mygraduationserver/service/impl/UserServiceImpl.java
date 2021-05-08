package top.baixiaosheng.mygraduationserver.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import top.baixiaosheng.mygraduationserver.dao.AvatarDOMapper;
import top.baixiaosheng.mygraduationserver.dao.MedicalRecordDOMapper;
import top.baixiaosheng.mygraduationserver.dao.UserDOMapper;
import top.baixiaosheng.mygraduationserver.dao.UserPasswordDOMapper;
import top.baixiaosheng.mygraduationserver.dataobject.AvatarDO;
import top.baixiaosheng.mygraduationserver.dataobject.MedicalRecordDO;
import top.baixiaosheng.mygraduationserver.dataobject.UserDO;
import top.baixiaosheng.mygraduationserver.dataobject.UserPasswordDO;
import top.baixiaosheng.mygraduationserver.error.BusinessException;
import top.baixiaosheng.mygraduationserver.error.EmBusinessError;
import top.baixiaosheng.mygraduationserver.response.PersonalData;
import top.baixiaosheng.mygraduationserver.service.UserService;
import top.baixiaosheng.mygraduationserver.service.model.MedicalRecordQueryVo;
import top.baixiaosheng.mygraduationserver.service.model.UserModel;
import top.baixiaosheng.mygraduationserver.validator.ValidationResult;
import top.baixiaosheng.mygraduationserver.validator.ValidatorImpl;

@Service
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UserDOMapper userDOMapper;

    @Autowired(required = false)
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired(required = false)
    private AvatarDOMapper avatarDOMapper;

    @Autowired(required = false)
    private MedicalRecordDOMapper medicalRecordDOMapper;

    @Autowired
    private ValidatorImpl validator;



    @Override
    public UserModel getUserById(Integer uid) {
        //调用userdomapper获取到对应的用户dataobject
        UserDO userDO = userDOMapper.selectByUId(uid);

        if (userDO == null){
            return null;
        }
        //通过用户id获得用户对应的加密密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getUid());
        return convertFromDataObject(userDO,userPasswordDO);
    }

    /**
     * 通过邮箱查询用户是否存在
     * 发送验证码
     * @param email
     * @return
     */
    @Override
    public boolean getUserByEmail(String email) {
        UserDO userDO = userDOMapper.selectByEmail(email);
        if (userDO == null){
            return  false;
        } else {
            return  true;
        }
    }

    /**
     * 用户注册服务的实现
     * 加上@Transactional注解是为了避免出现用户信息插入不全，程序意外结束
     *
     * @param userModel 用户信息
     */
    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        // 先进行整体判空处理，这样代码才健壮一些
        if (userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        // 优化后的model校验
        ValidationResult result = validator.validate(userModel);
        if (result.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        // model --->  dataobject:UserDO
        // 之所以使用insertSelective方法，这样可以避免使用null字段，而使用设计数据库时的默认值
        UserDO userDO = convertFromModel(userModel);
        try {
            userDOMapper.insertSelective(userDO);
            //userDOMapper.insert(userDO);

            System.out.println("注册成功");
        }catch (DuplicateKeyException ex){
            System.out.println(ex);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"邮箱已重复注册");
        }
        /*
        一旦insertSelective成功，user表的id就会自增（这需要去UserDOMapper.xml文件中设置id为主键自增）
        这时候就可以通过userDo进行get了
        将get到的id赋值给userModel，并传递给convertPasswoedFromModel方法
         */
        userModel.setUid(userDO.getUid());

        // model --->  dataobject:UserPasswordDO
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
    }





    /**
     * 实现model --->  dataobject:UserDO
     *
     * @param userModel Model
     * @return UserDO
     */
    private UserDO convertFromModel(UserModel userModel){
        // 每一层都进行判空，这样代码才处处健壮
        if(userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        // source是userModel，target是userDO，
        // 这样在copy过程中，userModel中多余的属性会被自动丢弃
        BeanUtils.copyProperties(userModel, userDO);

        return userDO;
    }

    /**
     * 实现model --->  dataobject:UserDO
     *
     //* @param userModel Model
     * @return UserDO
     */
    private UserPasswordDO convertPasswordFromModel(UserModel userModel){
        if (userModel == null){
            return  null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        /*
        对于userPasswordDO，我们不能像userDO那样进行copy
        因为我们在整合DO为model时，id是从userDO那里copy过来的，
        强行copy会导致user_password表中id不一致
        还有一点是，应该现有userDO中的属性，才会有userPasswordDO中的属性
        所以这里的userPasswordDO的id属性不需要设置，自动递增即可
         */
        userPasswordDO.setEncryptPassword(userModel.getEncryptPassword());
        /*
         user_password表总共三个字段，id不用我们管，可不要漏掉user_id字段
         否则无法根据外键进行查询密码了
         之所以userModel可以getId，在register方法中有提到
         */
        userPasswordDO.setUserId(userModel.getUid());
        return userPasswordDO;
    }

    //拼装处于不同表的信息，得到拥有完整信息的UserModel对象
    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if (userDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        if (userPasswordDO != null){
            userModel.setEncryptPassword(userPasswordDO.getEncryptPassword());
        }

        return userModel;
    }

    /**
     * 用户登录服务的实现
     *
     * @param email 手机号
     * @param encryptPassword 加密密码
     * @return 用户Model
     */
    @Override
    public UserModel validateLogin(String email, String encryptPassword) throws BusinessException {
        // 通过用户的邮箱获取用户信息
        /* 这里的selectByEmail是我们在UserDOMapper.xml中手动实现的 */
        UserDO userDO = userDOMapper.selectByEmail(email);
        if (userDO == null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getUid());
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);

        // 比对用户信息内加密的面是否和传输进来的密码相匹配
        if (!StringUtils.equals(encryptPassword,userModel.getEncryptPassword())){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户名或密码错误");
        }
        return userModel;
    }


    /**
     * 查询用户是否已上传过头像
     * @param uid
     * @return
     */
    @Override
    public boolean isAvatarExist(Integer uid) {
        AvatarDO avatarDO = avatarDOMapper.selectByUId(uid);
        if (avatarDO == null){
            return  false;
        } else {
            return  true;
        }
    }


    /**
     * 用户更新头像的实现
     * 加上@Transactional注解是为了避免出现用户信息插入不全，程序意外结束
     *
     * @param avatarDO 用户头像
     */
    @Override
    @Transactional
    public void avataUpdate(AvatarDO avatarDO, Boolean exist) throws BusinessException {
        // 先进行整体判空处理，这样代码才健壮一些
        if (avatarDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        // 优化后的model校验
        ValidationResult result = validator.validate(avatarDO);
        if (result.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        if (!exist){
            try {
                avatarDOMapper.insertSelective(avatarDO);
                System.out.println("更新头像成功");
            }catch (DuplicateKeyException ex){
                System.out.println(ex);
                // 手动回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"更新头像失败");
            }
        }else {
            try {
                avatarDOMapper.updateByUIdSelective(avatarDO);
                System.out.println("更新头像成功");
            }catch (DuplicateKeyException ex){
                System.out.println(ex);
                // 手动回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"更新头像失败");
            }
        }
    }

    /**
     * 加载（查询）用户头像的实现
     *
     * @param uid 用户id
     */
    @Override
    public AvatarDO avatarLoading(Integer uid){
        AvatarDO avatarDO = avatarDOMapper.selectByUId(uid);
        if (avatarDO == null){
            return  null;//没上传过头像
        }else return avatarDO;

    }


    /******************************----病历----******************************************/

    /**
     *功能描述 查询病历是否已上传过
     * @author 百xiao生
     * @date  21:17
     * @param  * @param uid
     * @param recordPath
     * @return void
    */
    @Override
    public boolean isMedicalRecordExist(Integer uid, String recordPath){
        MedicalRecordDO medicalRecordDO = new MedicalRecordDO();
        MedicalRecordDO medicalRecordDO2 = new MedicalRecordDO();
        medicalRecordDO.setUid(uid);
        medicalRecordDO.setMedicalRecordPath(recordPath);

        MedicalRecordQueryVo medicalRecordQueryVo = new MedicalRecordQueryVo();
        medicalRecordQueryVo.setMedicalRecordDO(medicalRecordDO);

        medicalRecordDO2 = medicalRecordDOMapper.selectByUidAndPath(medicalRecordQueryVo);
        if (medicalRecordDO2 == null){
            return false;
        }
        return true;
    }

    /**
     *功能描述 上传更新病历
     * @author 百xiao生
     * @date  21:50
     * @param  * @param medicalRecordDO
     * @param exist
     * @return void
    */
    @Override
    public void medicalRecordUpdate(MedicalRecordDO medicalRecordDO, Boolean exist) throws BusinessException {
        // 先进行整体判空处理，这样代码才健壮一些
        if (medicalRecordDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        // 优化后的model校验
        ValidationResult result = validator.validate(medicalRecordDO);
        if (result.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        if (!exist){
            try {
                medicalRecordDOMapper.insertSelective(medicalRecordDO);
                System.out.println("更新病历成功");
            }catch (DuplicateKeyException ex){
                System.out.println(ex);
                // 手动回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"更新病历失败");
            }
        }else {
            MedicalRecordQueryVo medicalRecordQueryVo = new MedicalRecordQueryVo();
            medicalRecordQueryVo.setMedicalRecordDO(medicalRecordDO);
            try {
                medicalRecordDOMapper.updateByUIdSelective(medicalRecordQueryVo);
                System.out.println("更新病历成功");
            }catch (DuplicateKeyException ex){
                System.out.println(ex);
                // 手动回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"更新病历失败");
            }
        }
    }

    /**
     *功能描述 查询病历的文件路径
     * @author 百xiao生
     * @date  0:34
     * @param  * @param null
     * @return
    */
    @Override
    public MedicalRecordDO[] getMedicalRecordPath(Integer uid){
        MedicalRecordDO[] medicalRecordDO = medicalRecordDOMapper.selectByUid(uid);
        if (medicalRecordDO == null){
            return null;
        }
        return medicalRecordDO;
    }

    /**
     *功能描述 查询病历
     * @author 百xiao生
     * @date  14:10
     * @param  * @param null
     * @return
    */
    @Override
    public MedicalRecordDO getMedicalRecord(Integer id) {
        MedicalRecordDO medicalRecordDO = medicalRecordDOMapper.selectByPrimaryKey(id);
        if (medicalRecordDO == null){
            return null;
        }
        return medicalRecordDO;
    }


//    @Override
//    public MedicalRecordPathModel[] getMedicalRecordPath(Integer uid){
//        MedicalRecordDO[] medicalRecordDO = medicalRecordDOMapper.selectByUid(uid);
//        if (medicalRecordDO == null){
//            return null;
//        }
//        MedicalRecordPathModel[] medicalRecordPathModelS = new MedicalRecordPathModel[medicalRecordDO.length];
//        MedicalRecordPathModel medicalRecordPathModel = new MedicalRecordPathModel();
//
//
//        for (int i = 0; i < medicalRecordDO.length; i++) {
//            BeanUtils.copyProperties(medicalRecordDO[i], medicalRecordPathModel);
//            System.out.println(medicalRecordDO[i]);
//            System.out.println(medicalRecordPathModel);
//            medicalRecordPathModelS[i] = medicalRecordPathModel;
//            System.out.println(medicalRecordPathModelS[i]);
////            System.out.println(medicalRecordPathModel[i]);
//        }
//
//        return medicalRecordPathModelS;
//    }


    /*
     *功能描述 更新个人信息
     * @author 百xiao生
     * @date  15:03
     * @param  * @param null
     * @return
    */
    @Override
    public int updateUserInfo(UserDO userDO) throws BusinessException{
        if (userDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"更新个人信息失败！");
        }
        int i = userDOMapper.updateByPrimaryKeySelective(userDO);
        return i;
    }

    /**
     *功能描述 修改密码
     * @author 百xiao生
     * @date  0:08
     * @param  * @param passwordDO
     * @return int
    */
    @Override
    public int updateUserPassword(UserPasswordDO passwordDO) throws BusinessException {
        if (passwordDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"修改密码失败！");
        }
        int i = userPasswordDOMapper.updateByUserIdSelective(passwordDO);
        return i;
    }

    /*
     *功能描述 获取个人信息
     * @author 百xiao生
     * @date  15:03
     * @param  * @param null
     * @return
    */
    @Override
    public PersonalData getPersonalData(Integer uId) {
        UserDO userDO = userDOMapper.selectByUId(uId);
        if (userDO == null)
            return null;
        PersonalData personalData = new PersonalData();
        BeanUtils.copyProperties(userDO,personalData);
        return personalData;
    }


}
