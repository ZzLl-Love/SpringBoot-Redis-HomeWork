package cn.zyy.bootredis.task.day03;

import cn.zyy.bootredis.util.JsonSerializationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/**
 *
 * 抽奖服务层 使用redis 的set 结构来实现
 *
 * task:
 *  将第二天任务的所有用户加入抽奖系统中
 *  从中抽取出一个一等奖，一个二等奖，参与过的用户，不允许再次参与从中抽取出一个一等奖，一个二等奖，参与过的用户，不允许再次参与
 *
 * @Author: Zz
 * @Date: 2024/02/25/21:28
 * @Description: 致敬
 */
@Component
public class LotteryServiceBySet {

    //定义抽奖用户的唯一key
    int num = 0;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加抽奖用户
     * @param allUserKey
     */
    public void  saveAllUser(String key,Set<String> allUserKey) throws JsonProcessingException {
        for (String userKey : allUserKey) {
            stringRedisTemplate.opsForSet().add(key, userKey);
        }
    }

    /**
     * 查询所有的抽奖用户
     * @param key
     */
    public Set<String>  getAllUser(String key){
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        return members;
    }

    /**
     * 对抽经用户进行抽奖
     * @param key 抽奖用户标识
     */
    public void drawLottery(String key){

        ArrayList<String> userList = new ArrayList<>();

        //获取素有所有抽奖用户
        Set<String> allUser = getAllUser(key);

        if(allUser == null || allUser.isEmpty()){
            throw new RuntimeException("没有抽奖用户");
        }

        for (String userKey : allUser) {
            userList.add(userKey);
        }

        System.out.println("所有的抽奖用户:"+ userList);

        Random random = new Random();

        //随机抽取一位获奖者
        int firstPrizeIndex = random.nextInt(userList.size());
        String firstPrizeWinner  = userList.get(firstPrizeIndex);
        System.out.println("一等奖获奖者:" + firstPrizeWinner );

        // 从剩余的用户中随机选择二等奖获奖者
        userList.remove(firstPrizeIndex);
        if (userList.isEmpty()) {
            throw new RuntimeException("没有剩余用户可供抽奖");
        }

        int secondPrizeIndex  = random.nextInt(userList.size());
        String secondPrizeWinner = userList.get(secondPrizeIndex);
        System.out.println("二等奖获奖者:" + secondPrizeWinner);

        stringRedisTemplate.opsForSet().remove(key,firstPrizeWinner,secondPrizeWinner);

    }

    /**
     * 移除用户
     * @param userToRemove
     */
    public void removeUser(String key, String... userToRemove){
        stringRedisTemplate.opsForSet().remove(key, userToRemove);
    }

    private String[] removeElement(String[] array, int indexToRemove) {

        String[] newArray = new String[array.length - 1];
        /**
         * public static native void arraycopy(
         *  Object src,     源数组，即要复制的原始数组
         *  int  srcPos,    源数组的起始位置
         *  Object dest,    目标数组，
         *  int destPos,    目标数组的起始位置
         * int length);     要复制的元素数量      );
         * */

        System.arraycopy(array, 0, newArray, 0, indexToRemove);
        System.arraycopy(array, indexToRemove + 1, newArray, indexToRemove, array.length - indexToRemove - 1);
        return newArray;
    }

}
