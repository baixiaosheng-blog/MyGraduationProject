package top.baixiaosheng.mygraduationserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.baixiaosheng.mygraduationserver.dao.UserDOMapper;
import top.baixiaosheng.mygraduationserver.dataobject.UserDO;
import top.baixiaosheng.mygraduationserver.util.MailUtils;

import javax.mail.MessagingException;

/**
 * Hello world!
 *
 */
//@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = {"top.baixiaosheng.mygraduationserver"})
@RestController
@MapperScan("top.baixiaosheng.mygraduationserver.dao")
public class App 
{
    @Autowired(required = false)
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    public String home(){
        UserDO userDO =userDOMapper.selectByUId(1);
        if (userDO == null){
            return "用户不存在";
        }else{
            return "你好！"+userDO.getName()+"!";
        }
    }

    /**
     * 调试数据库查询与插入操作
     * @return
     */
    @RequestMapping("/in")
    public int ins(){
        UserDO userDO = userDOMapper.selectByEmail("jg2421988151@163.com");
        if (userDO == null){
            UserDO userDO1 = new UserDO();
            userDO1.setAge(32);
            userDO1.setEmail("jg2421988151@163.com");
            userDO1.setGender(0);
            userDO1.setName("212331");
            userDO1.setRegistMode("byhand");
            return userDOMapper.insertSelective(userDO1);
        }
        return 0;
    }


    /**
     * 调试发邮件功能
     * @return
     */
    @RequestMapping("/jihuo")
    public String jihuo(){
        try {
            MailUtils.sendMail("2421899151@qq.com","来自俊哥的祝福","你俊哥发给你的测试邮件");
        } catch (MessagingException e) {
            e.printStackTrace();
            return "发送失败";
        }
        return "发送成功";
    }
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(App.class);
    }
}
