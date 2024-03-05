package cn.zyy.bootredis.task.day07;

import cn.zyy.bootredis.task.day07.entiy.Car;
import cn.zyy.bootredis.task.day07.entiy.User;
import cn.zyy.bootredis.util.JsonSerializationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Zz
 * @Date: 2024/03/04/17:12
 * @Description: 致敬
 */
@Component
public class UserService {

    //定义redis 标识
    public static final String redisKey = "userInfo";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 保存所有 用户的信息
     * @param userInfos
     */
    public void saveUserInfo(Map<String, User> userInfos) throws JsonProcessingException {

        if(userInfos == null || userInfos.isEmpty()){
            throw new RuntimeException("用户信息为空，无法保存....");
        }

        //遍历添加车辆信息
        for (Map.Entry<String, User> carInfo : userInfos.entrySet()) {

            String carId = carInfo.getKey();
            User user = carInfo.getValue();

            redisTemplate.opsForHash().put(redisKey,"user:"+carId, JsonSerializationUtils.serializeObject(user));
        }
    }


    /**
     * 获取所有用户的信息
     * @return
     */
    public Map<String,User> getAllUserInfos() throws JsonProcessingException {

        HashMap<String, User> userHashMap = new HashMap<>();

        //得到所有的用户信息
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(redisKey);

        for (Map.Entry<Object, Object> objectObjectEntry : entries.entrySet()) {

            String key = (String)objectObjectEntry.getKey();
            String userString =(String) objectObjectEntry.getValue();

            User user = JsonSerializationUtils.deserializeObject(userString, User.class);

            // //获取用户信息 放入新的map中
            userHashMap.put(key, user);
        }

        return userHashMap;
    }


    /**
     * 获取单个用户的信息
     * @param userId
     * @return
     */
    public User getOneUserInfo(String userId) throws JsonProcessingException {

        if(userId == null) {
            throw new RuntimeException("车辆id为空");
        }

        //userId 不包含前缀"car:", 则凭借前缀
        if(!userId.startsWith("user:")){
            userId = "user:" + userId;
        }

        //得到单个car的json字符串
        String carString = (String)redisTemplate.opsForHash().get(redisKey,  userId);

        //反序列化为Car对象
        return JsonSerializationUtils.deserializeObject(carString,User.class);

    }

}
