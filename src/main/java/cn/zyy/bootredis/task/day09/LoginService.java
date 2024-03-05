package cn.zyy.bootredis.task.day09;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * task: 用户登录系统，如果3分钟内，失败三次，则要求30分钟后才可以再次登录
 *
 * @Author: Zz
 * @Date: 2024/03/03/16:25
 * @Description: 致敬
 */
@Component
public class LoginService {

    public static final String LOGIN_PREFIX = "login:";

    public static final String LOCK_PREFIX = "lock:";

    //最大登录尝试次数
    @Value("${login.maxAttempts}")
    private int maxAttempts;

    //锁定时长(分钟)
    @Value("${login.lockDuration}")
    private int lockDuration;

    //登录窗口时间(分钟)
    @Value("${login.loginWindow}")
    private int loginWindow;


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public boolean userLogin(String username, String password){

        // 定义存入redis 数据库中的key
        String loginKey = LOGIN_PREFIX + username;

        String lockKey  = LOCK_PREFIX + username;

        // 验证数据非法性
        if(username == null || password == null){
            throw new RuntimeException("用户名| 密码不合法");
        }

        //检查账号是否被锁定  getExpire return -1  键不存在或者已经过期了 || -2  键已经过期了
        if( stringRedisTemplate.hasKey(lockKey) && stringRedisTemplate.getExpire(lockKey, TimeUnit.SECONDS)>0){

            System.out.println("用户"+username+"已经被锁定了," +lockDuration + "分钟后在尝试登录");
            //存在超时时间，账号锁定，登录失败
            return false;
        }

        //检查是否在登录窗口期中
        Long ttl = stringRedisTemplate.getExpire(loginKey, TimeUnit.SECONDS);
        if(ttl > 0 && ttl <= loginWindow * 60){
            //在窗口登录期 ，检查登录次数
            Long failedAttempts = stringRedisTemplate.opsForValue().increment(loginKey, 0);
            if(failedAttempts != null && failedAttempts >=maxAttempts){
                //尝试登录次数超过上限，账号锁定
                stringRedisTemplate.opsForValue().set(lockKey,"locked", lockDuration, TimeUnit.MINUTES);
                System.out.println("用户:"+username+"|在" +loginWindow +"分钟内登录次数超过"+maxAttempts+ "次|账号已被锁定"+lockDuration+"分钟");
                return false; //登录失败，锁定账号
            }
        }




        //模拟密码登录验证逻辑
        if (!"admin".equals(username) || !"admin".equals(password)){
            //密码错误，记录登录次数
            long failedAttempts = stringRedisTemplate.opsForValue().increment(loginKey, 1);

            //设置登录次数的超时时间为登录窗口时间
            System.out.println("用户名或密码错误....");

            //用户第一次登录,设置过期时间为初始三分钟 -1 未设置  -2 已过期
            if(ttl == -1 ||  ttl == -2){
                 stringRedisTemplate.expire(loginKey, loginWindow*60, TimeUnit.SECONDS);
            }else{
                //更新过期时间，确保总时间长控制在三分钟内
                stringRedisTemplate.expire(loginKey,ttl ,TimeUnit.SECONDS);
            }


            return false; //密码错误，登录失败
        }

        //密码验证成功，登录成功
        stringRedisTemplate.delete(loginKey);
        stringRedisTemplate.delete(lockKey);
        return true;

    }
}
