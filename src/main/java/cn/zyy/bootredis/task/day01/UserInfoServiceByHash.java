//package cn.zyy.bootredis.task.day01;
//
//import cn.zyy.bootredis.entiy.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.util.Map;
//
///**
// * 用户信息service 层， 实现CRUD
// *
// * @Author: Zz
// * @Date: 2024/02/25/15:54
// * @Description:
// */
//public class UserInfoServiceByHash {
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    /**
//     * save 保存用户信息
//     * @param userId 用户id
//     * @param userInfo 用户信息
//     */
//    public  void saveUserInfo(String userId, Map<String, String> userInfo){
//
//        //验证数据合法性
//        if(userId ==null || userInfo == null){
//            throw new RuntimeException("用户传入数据非法");
//        }
//
//        redisTemplate.opsForHash().putAll(userId,userInfo);
//    }
//
//
//    /**
//     *  get 查询用户信息
//     * @param userId
//     * @return
//     */
//    public User  getUserInfo(String userId){
//
//        if(userId == null){
//            throw new RuntimeException("用户传入的查询信息为空");
//        }
//
//        Map<Object, Object> userInfo = redisTemplate.opsForHash().entries(userId);
//    }
//}
