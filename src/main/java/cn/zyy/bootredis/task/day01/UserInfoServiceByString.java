package cn.zyy.bootredis.task.day01;

import cn.zyy.bootredis.entiy.User;
import cn.zyy.bootredis.util.JsonSerializationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * 保存用户信息service层
 * 将用户信息序列化为json字符串存放到redis 数据库中
 * 取出用户信息时，将json字符串反序列化为对象
 *
 * @Author: Zz
 * @Date: 2024/02/21/23:40
 * @Description: 致敬
 */
@Component
public class UserInfoServiceByString {


    @Autowired
    StringRedisTemplate  stringRedisTemplate;

    /**
     * 获取指定key的键
     * @param pattern
     * @return
     */
    public Set<String> getAllUserInfoKey(String pattern){
        Set<String> keys = stringRedisTemplate.keys(pattern);
        return keys;
    }

    /**
     * 保存单个用户
     * @param user
     * @throws JsonProcessingException
     */
    //序列化对象 到redis 服务器中
    public  void saveUserInfo(User user) throws JsonProcessingException {

        //将user对象 序列化后 只存储对象属性值到redis 服务器，而不把对象字节码存放进去
        stringRedisTemplate.opsForValue().set("user"+user.getId(), JsonSerializationUtils.serializeObject(user));
    }


    /**
     * 保存多个用户
     * @param userInfos 保存的多个对象
     *
     */
    public  void saveUserInfo(Map<String,User> userInfos) throws JsonProcessingException {

        // 验证数据的非法性
        if(Objects.isNull(userInfos)){
            throw new RuntimeException("==error用户未传入数据，无法保存==");
        }

        //遍历取出需要保存的单个用户信息
        for (Map.Entry<String, User> entry : userInfos.entrySet()) {

            String key = entry.getKey();
            User user = entry.getValue();
            if(user == null){
                throw new RuntimeException(key+"对应的保存对象信息为null");
            }

            String userJson = JsonSerializationUtils.serializeObject(user);
            stringRedisTemplate.opsForValue().set(key, userJson);
        }
    }

    /**
     * 查询用户
     * @param userId
     * @return
     * @throws JsonProcessingException
     */
    //从redis 服务器中 得到对象属性值，反序列化为对象
    public User getUserInfo(String userId) throws JsonProcessingException {
        String json = stringRedisTemplate.opsForValue().get(userId);
        return JsonSerializationUtils.deserializeObject(json, User.class);
    }



    /**
     * 修改指定用户 指定字段 为指定值
     * @param userId 修改用户
     * @param updateField 需修改字段
     * @param updateValue  修改值
     * @return
     * @throws JsonProcessingException
     */
    public User updateUserInfo(String userId, String updateField, String updateValue) throws JsonProcessingException {



        //验证用户是否存在
        if(getUserInfo(userId) == null){
            throw  new RuntimeException("需要修改的用户" + userId + "不存在，无法修改");
        }

        //取出修改的User对象
        User updateUser = getUserInfo(userId);


        //修改User对象对应的字段的值
        try {
            Field filed = User.class.getDeclaredField(updateField);
            filed.setAccessible(true);
            //获取字段的类型
            Class<?> type = filed.getType();

            if(type == String.class){
                filed.set(updateUser,updateValue);
            } else if(type == Integer.class){
                //把string类型的修改值， 转换成对应修改字段的类型
                filed.set(updateUser,Integer.parseInt(updateValue));
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //向redis 数据库中保存用户修改完成后的用户信息
        saveUserInfo(updateUser);

        //返回修改后的用户信息对象
        return updateUser;

    }


    /**
     * 给指定用户添加对应字段的属性值
     * @param userId
     * @param addField 添加字段
     * @param addValue 添加字段对应的值
     * @return
     */
    public User addUserInfoField(String userId, String addField, String addValue) throws JsonProcessingException{

        User addUserInfo = getUserInfo(userId);

        String updateString = null;

        //验证数据合法性
        if(userId ==null || addField == null || addValue ==null){
            throw new RuntimeException("用户传入数据非法");
        }


        User userInfo = getUserInfo(userId);

        //获取到Json字符串
        String jsonUserInfo = JsonSerializationUtils.serializeObject(userInfo);

        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject;
        try {
             jsonObject = (JSONObject)jsonParser.parse(jsonUserInfo);

                jsonObject.put(addField, addValue);
                //把修改后的结果转成字符串
                updateString = jsonObject.toJSONString();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //保存
        stringRedisTemplate.opsForValue().set(userId, updateString);

        //返回添加字段后的用户信息
        return getUserInfo(userId);
    }


     /**
     * 添加一个字段并指定过期时间
     * @param userId
     * @param addField
     * @param addValue
     * @return
     */
    public User setUserInfoExpire(String userId, String addField, String addValue, int expireTime) throws JsonProcessingException {


        User userInfo = getUserInfo(userId);

        if(userId == null || addField ==null || addField == null){
            throw new RuntimeException("用户传入数据非法");
        }


        if(userInfo == null){
            throw new RuntimeException("用户信息为空");
        }

        // 添加属性值
        try {
            Field declaredField = User.class.getDeclaredField(addField);
            declaredField.setAccessible(true);
            declaredField.set(userInfo,addValue);
             saveUserInfo(userInfo);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //设置过期时间 -1 表示永不过期
        if(expireTime !=-1){
            //永不过期
            stringRedisTemplate.expire(userId, expireTime, TimeUnit.SECONDS );
        }


        // 启动一个线程用于实时打印过期时间
        if (expireTime != -1) {
            startExpirationCountdownThread(userId, expireTime);
        }


        return userInfo;
    }


    //启动一个线程用于实时打印过期时间
    private void startExpirationCountdownThread(String userId, int expireTime) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                while (true) {
                    long currentTime = System.currentTimeMillis();
                    long remainingTime = (startTime + expireTime * 1000) - currentTime;
                    if (remainingTime <= 0) {
                        break;
                    }
                    System.out.println("User " + userId + " will expire in " + remainingTime / 1000 + " seconds.");
                    try {
                        Thread.sleep(1000); // 每隔1秒打印一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("User " + userId + " has expired.");
            }
        });
        thread.start();
    }

}
