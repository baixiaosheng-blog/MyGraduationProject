package top.baixiaosheng.mygraduationserver.util;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailUtils {

    public static void sendMail(String email,String emailtitle, String emailMsg)
            throws AddressException, MessagingException {
        // 1.创建一个程序与邮件服务器会话对象 Session
        Properties props = new Properties();
        //设置发送的协议   *可能需要改的地方*
        props.setProperty("mail.transport.protocol", "SMTP");

        //设置发送邮件的服务器     *localhost需要根据实际情况更改，smtp.qq.com如163的为 smtp.163.com*
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "smtp.163.com");
        props.setProperty("mail.smtp.auth", "true");// 指定验证为true
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtp.port", "465");


        // 创建验证器
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                //设置发送人的帐号和密码       *需要改的地方*        网易jg2421授权码 NVCDDADVWEBJHJJF
                //140136授权码：auriuthvhfllffcg
                return new PasswordAuthentication("jg2421899151", "NVCDDADVWEBJHJJF");
            }
        };

        Session session = Session.getInstance(props, auth);

        // 2.创建一个Message，它相当于是邮件内容
        Message message = new MimeMessage(session);

        //设置发送者       *需要改的地方，如ceobaidu@163.com*
        message.setFrom(new InternetAddress("jg2421899151@163.com"));

        //设置发送方式与接收者(mail目的地)
        message.setRecipient(RecipientType.TO, new InternetAddress(email));

        //设置邮件主题    *根据实际情况添加主题*
        message.setSubject(emailtitle);
        // message.setText("这是一封激活邮件，请<a href='#'>点击</a>");

        //设置邮件内容
        message.setContent(emailMsg, "text/html;charset=utf-8");

        // 3.创建 Transport用于将邮件发送
        Transport.send(message);

    }

    /*public Map<String, Object> sendCodeToMail(UserDOMapper drUserCustom){
        Map<String, Object> responseData = new HashMap<String, Object>();
        if(UserDoMapper.getDrUserId(drUserCustom) != null){		//判断用户是否存在
            //根据name获取保存的邮箱
            String mailAddress = drUserMapper.getMailByName(drUserCustom);
            if(mailAddress.equals(drUserCustom.getDoc_mail())){		//用户输入邮箱与绑定邮箱一致→发送验证码
                try{
                    //生成验证码
                    String verifyCode = RandomUtil.getCode();
                    //邮件主题
                    String emailTitle = "邮箱验证";
                    //邮件内容
                    String emailContent = "您正在进行邮箱验证，您的验证码为：" + verifyCode + "，请于5分钟内完成验证！";
                    //发送邮件
                    SendMailUtil.sendEmail(mailAddress, emailTitle, emailContent);
                    //缓存5分钟
                    redisTemplate.opsForValue().set(mailAddress,verifyCode,5,TimeUnit.MINUTES);
                    responseData = ResponseData.success("邮箱验证码发送成功");
                }catch(Exception e){
                    responseData = ResponseData.error(e.getMessage());
                }
            }else{
                //邮箱地址错误
                responseData=ResponseData.result(ResponseState.REP_PASSWORD_MAIL_ERROR.state, ResponseState.REP_PASSWORD_MAIL_ERROR.message, "");
            }
        }else{
            //用户不存在
            responseData=ResponseData.result(ResponseState.REP_USER_EXIST_ERROR.state, ResponseState.REP_USER_EXIST_ERROR.message, "");
        }
        return responseData;
    }*/

}
