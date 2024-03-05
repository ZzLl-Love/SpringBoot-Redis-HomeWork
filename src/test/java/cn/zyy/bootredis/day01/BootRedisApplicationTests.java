package cn.zyy.bootredis.day01;

import cn.zyy.bootredis.entiy.User;
import cn.zyy.bootredis.task.day01.UserInfoServiceByString;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;

@SpringBootTest
class BootRedisApplicationTests {



    @Autowired
    UserInfoServiceByString userInfoService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 连接redis 服务器一直被拒绝
     * 原因： 1.防火墙未放行redis 端口
     *       2.redis 默认只有本机访问   修改redis 配置文件 bind 0.0.0.0
     */




    /**
     * 初始化数据
     */
      HashMap<String, User> initData(){

       HashMap<String, User> userInfos = new HashMap<>();

       User zs = new User(1001, "zhangsan", 22, "Beijing");
       User ls = new User(1002, "Lisi", 24, "Nanjing");
       User ww = new User(1003, "Wangwu", 25, "Shanghai");
       User zl = new User(1004, "Zhaoliu", 23, "chongqing");

       userInfos.put("user"+zs.getId(), zs);
       userInfos.put("user"+ls.getId(), ls);
       userInfos.put("user"+ww.getId(), ww);
       userInfos.put("user"+zl.getId(), zl);

       return userInfos;
   }

    /**
     * 测试第一天的任务    1.	存储用户信息
     */
    @Test
    void testDay01_saveUserInfo() throws JsonProcessingException {

        //得到测试的用户信息
        HashMap<String, User> allUserInfo = initData();

        //1.存储用户信息
        userInfoService.saveUserInfo(allUserInfo);

        //2.查看用户1004的全部信息

    }


    //2.修改1001年龄为23
    @Test
    void testDay01_updateUserInfo() throws JsonProcessingException {
        User updateUserInfo = userInfoService.updateUserInfo("user1001", "age", "23");
        System.out.println(updateUserInfo);


    }


    //3.	查看用户1004的全部信息
    @Test
    public void testDay01_2() throws JsonProcessingException {
        User user1004 = userInfoService.getUserInfo("user1004");
        System.out.println(user1004);

    }


    //5为1001添加手机号码属性  值为13055556666
    @Test
    public void testDay01_addFiled() throws JsonProcessingException {

        User user = userInfoService.addUserInfoField("user1001", "phone", "13055558888");
        System.out.println(user);
    }

    //6.向1001用户的手机添加一个验证码信息 2s5F进行验证,有效时间为2分钟
    @Test
    public void testDay02_addExpire() throws JsonProcessingException {
        User user = userInfoService.setUserInfoExpire("user1001", "code", "2s5f", 50);
        System.out.println(user);
    }
}
