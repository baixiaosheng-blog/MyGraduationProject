package top.baixiaosheng.mygraduationserver.controller;

import com.alibaba.druid.util.StringUtils;
import com.sun.mail.util.BASE64EncoderStream;
import org.apache.commons.lang3.CharEncoding;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.baixiaosheng.mygraduationserver.controller.viewobject.AvatarVO;
import top.baixiaosheng.mygraduationserver.controller.viewobject.UserVO;
import top.baixiaosheng.mygraduationserver.dataobject.AvatarDO;
import top.baixiaosheng.mygraduationserver.dataobject.MedicalRecordDO;
import top.baixiaosheng.mygraduationserver.dataobject.UserDO;
import top.baixiaosheng.mygraduationserver.dataobject.UserPasswordDO;
import top.baixiaosheng.mygraduationserver.error.BusinessException;
import top.baixiaosheng.mygraduationserver.error.EmBusinessError;
import top.baixiaosheng.mygraduationserver.response.CommonReturnType;
import top.baixiaosheng.mygraduationserver.response.OtpCode;
import top.baixiaosheng.mygraduationserver.response.PersonalData;
import top.baixiaosheng.mygraduationserver.service.UserService;
import top.baixiaosheng.mygraduationserver.service.model.MedicalRecordPathModel;
import top.baixiaosheng.mygraduationserver.service.model.UserModel;
import top.baixiaosheng.mygraduationserver.util.MailUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 *
 */
@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController {

    @Autowired(required = false)
    UserService userService;

    @Autowired(required = false)
    private HttpServletRequest httpServletRequest;
    private HttpSession session;
    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "uid") Integer uid) throws BusinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(uid);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //将核心领域模型用户对象转换为可供前端使用的简化用户模型
        UserVO userVO = convertFromModle(userModel);
        return CommonReturnType.create(userVO);
    }
    /**
     * 将UserModel转为UserVO
     *
     * @param userModel Model
     * @return UserVO
     */
    private UserVO convertFromModle(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }


    /***************************----验证码----*****************************************/

    /**
     * 获取otp验证码（注册用）
     * 三步走
     * 1、生成验证码
     * 2、存到 Session 中
     * 3、返回验证码
     *
     * @param email
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/getOtp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "email") String email) throws BusinessException {
        /* 0、用户获取验证码时，检测是否已存在注册用户 */
        boolean hasRegistered = userService.getUserByEmail(email);
        if (hasRegistered) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "邮箱已重复注册");
        }
        // 1、按照一定规则生成OTP验证码（6位）
        SecureRandom random = new SecureRandom();
        int randomInt = random.nextInt(99999);
        randomInt += 100000;
        String otpCode = String.valueOf(randomInt);

        // 2、将OTP验证码与用户邮箱进行绑定
        session = httpServletRequest.getSession();
        session.setAttribute(email, otpCode);

        System.out.println(email+"-->"+otpCode);

        // 3、将OTP验证码通过短信通道发给用户
        //Log.info("telephone: " + email + "&otpCode: " + otpCode);
        String emailMsg = "欢迎注册脑卒中绿色通道App！您的验证码为："+otpCode+"。验证码将在5分钟后失效。";
        try {
            MailUtils.sendMail(email,"注册验证",emailMsg);
        } catch (MessagingException e) {
            e.printStackTrace();
            return CommonReturnType.create(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码发送失败");
        }

        // 4、将信息抽象为类
        OtpCode otpCodeObj = new OtpCode(email, otpCode);
        // 5、返回正确信息，方便前端获取
        return CommonReturnType.create(otpCodeObj, "successGetOtpCode");
    }


    /**
     * 获取邮箱otp验证码（验证用）
     *
     * @param email
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/getMailOtp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getMailOtp(@RequestParam(name = "email") String email) throws BusinessException {
        // 1、按照一定规则生成OTP验证码（6位）
        SecureRandom random = new SecureRandom();
        int randomInt = random.nextInt(99999);
        randomInt += 100000;
        String otpCode = String.valueOf(randomInt);

        // 2、将OTP验证码与用户邮箱进行绑定
        session = httpServletRequest.getSession();
        session.setAttribute(email, otpCode);

        System.out.println(email+"-->"+otpCode);

        // 3、将OTP验证码通过短信通道发给用户
        //Log.info("telephone: " + email + "&otpCode: " + otpCode);
//        String emailMsg = "欢迎使用脑卒中绿色通道App！您的验证码为："+otpCode+"。验证码将在5分钟后失效。";
//        try {
//            MailUtils.sendMail(email,"身份验证",emailMsg);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            return CommonReturnType.create(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码发送失败");
//        }

        // 4、将信息抽象为类
        OtpCode otpCodeObj = new OtpCode(email, otpCode);
        // 5、返回正确信息，方便前端获取
        return CommonReturnType.create(otpCodeObj, "successGetOtpCode");
    }

    /**
     * 获取手机otp验证码（验证用）
     *
     * @param email
     * @return
     * @throws BusinessException
     */
//    @RequestMapping(value = "/getPhoneOtp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
//    @ResponseBody
//    public CommonReturnType getPhoneOtp(@RequestParam(name = "phone") String email) throws BusinessException {
//        /* 0、用户获取验证码时，检测是否已存在注册用户 */
//        boolean hasRegistered = userService.getUserByEmail(email);
//        if (hasRegistered) {
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "邮箱已重复注册");
//        }
//        // 1、按照一定规则生成OTP验证码（6位）
//        SecureRandom random = new SecureRandom();
//        int randomInt = random.nextInt(99999);
//        randomInt += 100000;
//        String otpCode = String.valueOf(randomInt);
//
//        // 2、将OTP验证码与用户邮箱进行绑定
//        session = httpServletRequest.getSession();
//        session.setAttribute(email, otpCode);
//
//        System.out.println(email+"-->"+otpCode);
//
//        // 3、将OTP验证码通过短信通道发给用户
//        //Log.info("telephone: " + email + "&otpCode: " + otpCode);
//        String emailMsg = "欢迎注册xxxxx！您的验证码为："+otpCode+"。验证码将在5分钟后失效。";
//        try {
//            MailUtils.sendMail(email,"注册验证",emailMsg);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            return CommonReturnType.create(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码发送失败");
//        }
//
//        // 4、将信息抽象为类
//        OtpCode otpCodeObj = new OtpCode(email, otpCode);
//        // 5、返回正确信息，方便前端获取
//        return CommonReturnType.create(otpCodeObj, "successGetOtpCode");
//    }




    /**
     * 用户注册接口
     * 接收参数统一使用字符串，接收后再进行类型转换
     *
     *
     * @param email 邮箱
     * @param otpCode   验证码
    // * @param name      姓名
    // * @param ageStr    年龄
    // * @param genderStr 性别
     * @param password  密码
     * @return 通用返回对象
     */
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(
//            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "otpCode") String otpCode,
//            @RequestParam(name = "age") String ageStr,        //客户端请求时，age、gender不为空才能正确插入数据库，
//            @RequestParam(name = "gender") String genderStr,  //为空时插入失败原因：未知
            @RequestParam(name = "password") String password
    ) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {
        boolean hasRegistered = userService.getUserByEmail(email);
        if (hasRegistered) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "邮箱已重复注册");
        }

        // 从Session中获取对应手机号的验证码
        // otpCode是用户填写的，inSessionOtpCode是系统生成的
        if (session == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "无效的验证码，请重新获取");
        }

        String inSessionOtpCode = (String) session.getAttribute(email);


        System.out.println("telephone: " + email + " inSessionOtpCode: " + inSessionOtpCode + " otpCode: " + otpCode);
        if (!StringUtils.equals(otpCode, inSessionOtpCode)) {
            System.out.println("验证码错误");
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "验证码错误");
        }

        // 类型转换，适配数据库
//        int age = Integer.parseInt(ageStr);
//        int gender = Byte.parseByte(genderStr);
        // 验证码通过后，进行注册流程
        UserModel userModel = new UserModel();
//        userModel.setName(name);
//        userModel.setGender(gender);
//        userModel.setAge(age);
        userModel.setEmail(email);
//        userModel.setTelephone(telephone);
        userModel.setRegisterMode("byemail");
        userModel.setEncryptPassword(this.EncodeByMd5(password));

        userService.register(userModel);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return CommonReturnType.create(userVO);
    }


    /**
     * 用户登录接口
     *
     * @param email 邮箱
     * @param password  原生密码
     * @param type 登录类型
     * @return 通用返回对象
     */
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(
            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "type", required = true) String type
    ) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(email)
                || org.apache.commons.lang3.StringUtils.isEmpty(password)
                || org.apache.commons.lang3.StringUtils.isEmpty(type)
        ) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        boolean hasRegistered = userService.getUserByEmail(email);
        if (!hasRegistered) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        UserModel userModel = null;
        // 登录
        if (StringUtils.equals(type, "login")) {
            userModel = userService.validateLogin(email, this.EncodeByMd5(password));
        }
        // 自动登录
        else if (StringUtils.equals(type, "autoLogin")) {
            userModel = userService.validateLogin(email, password);
        } else {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        // 将登陆凭证加入到用户登录成功的Session中
        // 切换web页面的时候，可以不用重复登录
        session = httpServletRequest.getSession();
        session.setAttribute("IS_LOGIN", true);
        session.setAttribute("LOGIN_USER", userModel);

        // 登录成功，只返回success即可
        return CommonReturnType.create(userVO);
    }


    /*************************----个人资料----****************************************
    //代码优化，抽取相对的代码
     /*
     *功能描述 更新个人信息（密码）
     * @author 百xiao生
     * @date  15:11
     * @param  * @param null
     * @return
     */
    @RequestMapping(value = "update/resetPassword", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType resetPassword(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "password", required = true) String password
    ) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        UserPasswordDO passwordDO = new UserPasswordDO();
        passwordDO.setUserId(uId);
        passwordDO.setEncryptPassword(this.EncodeByMd5(password));//用加密算法加密
        int i = userService.updateUserPassword(passwordDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新密码成功！");
    }

     /*
      *功能描述 更新个人信息（姓名）
      * @author 百xiao生
      * @date  15:11
      * @param  * @param null
      * @return
     */
    @RequestMapping(value = "update/updateName", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateName(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "name", required = true) String name
    ) throws BusinessException {
        UserDO userDO = new UserDO();
        userDO.setUid(uId);
        userDO.setName(name);
        int i = userService.updateUserInfo(userDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新姓名成功！");
    }


    /*
     *功能描述 更新个人信息（性别）
     * @author 百xiao生
     * @date  17:26
     * @param  * @param uId
     * @param name
     * @return top.baixiaosheng.mygraduationserver.response.CommonReturnType
    */
    @RequestMapping(value = "update/updateGender", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateGender(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "gender", required = true) Integer gender
    ) throws BusinessException {
        UserDO userDO = new UserDO();
        userDO.setUid(uId);
        userDO.setGender(gender);
        int i = userService.updateUserInfo(userDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新性别成功！");
    }


    /*
     *功能描述 更新个人信息（年龄）
     * @author 百xiao生
     * @date  17:26
     * @param  * @param uId
     * @param name
     * @return top.baixiaosheng.mygraduationserver.response.CommonReturnType
     */
    @RequestMapping(value = "update/updateAge", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateAge(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "age", required = true) Integer age
    ) throws BusinessException {
        UserDO userDO = new UserDO();
        userDO.setUid(uId);
        userDO.setAge(age);
        int i = userService.updateUserInfo(userDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新年龄成功！");
    }


    /*
     *功能描述 更新个人信息（邮箱）
     * @author 百xiao生
     * @date  17:26
     * @param  * @param uId
     * @param name
     * @return top.baixiaosheng.mygraduationserver.response.CommonReturnType
     */
    @RequestMapping(value = "update/updateEmail", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateEmail(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "email", required = true) String email
    ) throws BusinessException {
        UserDO userDO = new UserDO();
        userDO.setUid(uId);
        userDO.setEmail(email);
        int i = userService.updateUserInfo(userDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新邮箱成功！");
    }


    /*
     *功能描述 更新个人信息（手机号）
     * @author 百xiao生
     * @date  17:26
     * @param  * @param uId
     * @param name
     * @return top.baixiaosheng.mygraduationserver.response.CommonReturnType
     */
    @RequestMapping(value = "update/updatePhone", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updatePhone(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "phone", required = true) String phone
    ) throws BusinessException {
        UserDO userDO = new UserDO();
        userDO.setUid(uId);
        userDO.setTelephone(phone);
        int i = userService.updateUserInfo(userDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新手机号成功！");
    }




    /*
     *功能描述 更新个人信息（用户身份）
     * @author 百xiao生
     * @date  17:26
     * @param  * @param uId
     * @param name
     * @return top.baixiaosheng.mygraduationserver.response.CommonReturnType
     */
    @RequestMapping(value = "update/updateIdentity", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateIdentity(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "identity", required = true) Integer identity
    ) throws BusinessException {
        UserDO userDO = new UserDO();
        userDO.setUid(uId);
        userDO.setIdentity(identity);
        int i = userService.updateUserInfo(userDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新身份成功！");
    }



    /*
     *功能描述 更新个人信息（紧急联系人）
     * @author 百xiao生
     * @date  17:26
     * @param  * @param uId
     * @param name
     * @return top.baixiaosheng.mygraduationserver.response.CommonReturnType
     */
    @RequestMapping(value = "update/updateContact", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateContact(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "contact", required = true) String contact
    ) throws BusinessException {
        UserDO userDO = new UserDO();
        userDO.setUid(uId);
        userDO.setContact(contact);
        int i = userService.updateUserInfo(userDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新联系人成功！");
    }


    /*
     *功能描述 更新个人信息（地区）
     * @author 百xiao生
     * @date  17:26
     * @param  * @param uId
     * @param name
     * @return top.baixiaosheng.mygraduationserver.response.CommonReturnType
     */
    @RequestMapping(value = "update/updateAddress", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateAddress(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "address", required = true) String address
    ) throws BusinessException {
        UserDO userDO = new UserDO();
        userDO.setUid(uId);
        userDO.setAddress(address);
        int i = userService.updateUserInfo(userDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新地区成功！");
    }



    /*
     *功能描述 更新个人信息（详细地址）
     * @author 百xiao生
     * @date  17:26
     * @param  * @param uId
     * @param name
     * @return top.baixiaosheng.mygraduationserver.response.CommonReturnType
     */
    @RequestMapping(value = "update/updateFulladdress", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType updateFulladdress(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "fulladdress", required = true) String fulladdress
    ) throws BusinessException {
        UserDO userDO = new UserDO();
        userDO.setUid(uId);
        userDO.setFulladdress(fulladdress);
        int i = userService.updateUserInfo(userDO);
        if (i<=0)
            return null;
        return CommonReturnType.create("更新详细地址成功！");
    }




    /**
     *功能描述 获取个人信息
     * @author 百xiao生
     * @date  14:08
     * @param  * @param null
     * @return
    */

    @RequestMapping(value = "info/getPersonalData", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getPersonalData(
            @RequestParam(value = "uId", required = true) Integer uId
    ){
        PersonalData personalData = userService.getPersonalData(uId);
        if (personalData == null)
            return null;
        return CommonReturnType.create(personalData);
    }


    /***************************----头像----*****************************************

    /**
     *功能描述 更新头像
     * @author 百xiao生
     * @date  17:38
     * @param  * @param null
     * @return
    */

    @RequestMapping(value = "update/avatar", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMMU})
    @ResponseBody
    public CommonReturnType updateAvatar(
        @RequestParam(value = "uId", required = true) Integer uId,
        @RequestParam(value = "avatar", required = true) MultipartFile file,
        @RequestParam(value = "avatarPath", required = true) String avatarPath
    ) throws BusinessException, UnsupportedEncodingException {
        String avatar = null;
        if (!file.isEmpty()){
            Base64.Encoder encoder = Base64.getEncoder();

            try {
                avatar = encoder.encodeToString(file.getBytes());
            } catch (IOException e) {
                System.out.println("！！！头像转换二进制失败！！！");
                e.printStackTrace();
                return CommonReturnType.create(EmBusinessError.PARAMETER_VALIDATION_ERROR,"头像上传失败！");
            }

        }

        //把头像存入数据库
        AvatarDO avatarDO = new AvatarDO();
        avatarDO.setUid(uId);
        avatarDO.setPath(avatarPath);

        /**统一编码，以免图片信息在多次byte[]和String转过程中产生错误*/
        avatarDO.setAvatar(avatar.getBytes(CharEncoding.ISO_8859_1));
        if (!userService.isAvatarExist(uId)){
            //没上传过图片则执行插入sql
            userService.avataUpdate(avatarDO,false);
        }else userService.avataUpdate(avatarDO,true);

        return CommonReturnType.create("头像上传成功！");
    }


    /**
     *功能描述 查询（加载）用户头像URL
     * @author 百xiao生
     * @date  12:41
     * @param  * @param null
     * @return
     */
    @RequestMapping(value = "load/avatarPath", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType loadAvatarPath(
            @RequestParam(value = "uId", required = true) Integer uId){
        AvatarDO avatarDO = userService.avatarLoading(uId);

        String avatarPath = avatarDO.getPath();
        if (avatarPath == null){
            return null;
        }
        return CommonReturnType.create(avatarPath);

    }

    /**
     *功能描述 查询（加载）用户头像
     * @author 百xiao生
     * @date  12:41
     * @param  * @param null
     * @return
    */
    @RequestMapping(value = "load/avatar", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType loadAvatar(
            @RequestParam(value = "uId", required = true) Integer uId){
        AvatarDO avatarDO = userService.avatarLoading(uId);
        byte[] avatar;
        if (avatarDO == null){
            return CommonReturnType.create(null);
        }
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            avatar = decoder.decode(avatarDO.getAvatar());
            for (int i = 0; i < avatar.length; i++) {
                if (avatar[i] < 0) {
                    avatar[i] += 256;
                }
            }

            //测试用，看能否重新生成正确的图片
//            OutputStream outputStream = new FileOutputStream("src/main/resources/temp.jpg");
//            outputStream.write(avatar);
//            outputStream.flush();
//            outputStream.close();

            //统一编码，以免图片信息在多次byte[]和String转过程中产生错误
            String avatar1 = new String(avatar,CharEncoding.ISO_8859_1);
            AvatarVO avatarVO = new AvatarVO();
            avatarVO.setUid(uId);
            avatarVO.setAvatarPath(avatarDO.getPath());
            avatarVO.setAvatar(avatar1);
            return CommonReturnType.create(avatarVO);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonReturnType.create(EmBusinessError.UNKNOWN_ERROR,"服务器解析头像失败");
        }

    }


    /***************************----病历----*****************************************

     /**
     *功能描述 查询病历是否已上传过
     * @author 百xiao生
     * @date  12:41
     * @param  * @param null
     * @return
     */
    @RequestMapping(value = "update/isMedicalRecordExist", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType isMedicalRecordExist(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "recordPath", required = true) String recordPath){
        boolean b = userService.isMedicalRecordExist(uId, recordPath);

        if (b){
            return CommonReturnType.create("照片已存在！");
        }
        return null;

    }


    /**
     *功能描述 上传更新病历
     * @author 百xiao生
     * @date  21:38
     * @param  * @param null
     * @return
    */
    @RequestMapping(value = "update/medicalRecord", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMMU})
    @ResponseBody
    public CommonReturnType updateMedicalRecord(
            @RequestParam(value = "uId", required = true) Integer uId,
            @RequestParam(value = "record", required = true) MultipartFile file,
            @RequestParam(value = "recordPath", required = true) String recordPath
    ) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String record = null;
        if (!file.isEmpty()){
            Base64.Encoder encoder = Base64.getEncoder();

            try {
                record = encoder.encodeToString(file.getBytes());
            } catch (IOException e) {
                System.out.println("！！！病历转换二进制失败！！！");
                e.printStackTrace();
                return CommonReturnType.create(EmBusinessError.PARAMETER_VALIDATION_ERROR,"病历上传失败！");
            }

        }

        //把头像存入数据库
        MedicalRecordDO medicalRecordDO = new MedicalRecordDO();
        medicalRecordDO.setUid(uId);
        medicalRecordDO.setMedicalRecordPath(recordPath);

        /**统一编码，以免图片信息在多次byte[]和String转过程中产生错误*/
        medicalRecordDO.setMedicalRecord(record.getBytes(CharEncoding.ISO_8859_1));
        if (!userService.isMedicalRecordExist(uId, recordPath)){
            //没上传过图片则执行插入sql
            userService.medicalRecordUpdate(medicalRecordDO,false);
        }else userService.medicalRecordUpdate(medicalRecordDO,true);

        return CommonReturnType.create("病历上传成功！");
    }


    /**
     *功能描述 查询病历文件路径
     * @author 百xiao生
     * @date  0:50
     * @param  * @param null
     * @return
    */
    @RequestMapping(value = "getMedicalRecordPath", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getMedicalRecordPath(
            @RequestParam(value = "uId", required = true) Integer uId) {
        MedicalRecordDO[] medicalRecordDOS = userService.getMedicalRecordPath(uId);


            if (medicalRecordDOS != null){
                //抛掉多余的病历文件数据，减轻用户（和后端所在服务器）的流量压力
                MedicalRecordPathModel[] medicalRecordPathModelS = new MedicalRecordPathModel[medicalRecordDOS.length];

                for (int i = 0; i < medicalRecordDOS.length; i++) {
                    //要在循环内新建，否则数组元素都为最后一次所赋的值
                    MedicalRecordPathModel medicalRecordPathModel = new MedicalRecordPathModel();
                    //BeanUtils.copyProperties(medicalRecordDOS[i], medicalRecordPathModelS[i]);
                    BeanUtils.copyProperties(medicalRecordDOS[i], medicalRecordPathModel);
    //                System.out.println(medicalRecordDOS[i]);
    //                System.out.println(medicalRecordPathModel);
                    medicalRecordPathModelS[i] = medicalRecordPathModel;
    //                System.out.println(medicalRecordPathModelS[i]);
    //            System.out.println(medicalRecordPathModel[i]);
                }
            return CommonReturnType.create(medicalRecordPathModelS);
        }
        return null;

    }

    /**
     *功能描述 查询病历文件数据
     * @author 百xiao生
     * @date  13:13
     * @param  * @param null
     * @return
    */
    @RequestMapping(value = "getMedicalRecord", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getMedicalRecord(
            @RequestParam(value = "id", required = true) Integer id) {
        MedicalRecordDO medicalRecordDO = userService.getMedicalRecord(id);

        byte[] record;
        if (medicalRecordDO == null){
            return CommonReturnType.create(null);
        }
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            record = decoder.decode(medicalRecordDO.getMedicalRecord());
            for (int i = 0; i < record.length; i++) {
                if (record[i] < 0) {
                    record[i] += 256;
                }
            }

            //测试用，看能否重新生成正确的图片
//            OutputStream outputStream = new FileOutputStream("src/main/resources/recordTemp.jpg");
//            outputStream.write(record);
//            outputStream.flush();
//            outputStream.close();

            //统一编码，以免图片信息在多次byte[]和String转过程中产生错误
            String record1 = new String(record,CharEncoding.ISO_8859_1);

            return CommonReturnType.create(record1);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonReturnType.create(EmBusinessError.UNKNOWN_ERROR,"服务器解析病历失败");
        }

    }


    /**
     * MD5加密+BASE64编码
     *
     * @return 加密后字符串
     */
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder base64en = Base64.getEncoder();
        String newstr = base64en.encodeToString(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

}
