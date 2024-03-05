package cn.zyy.bootredis.task.day03;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * task: 将1001和1002加好友，将1002和1003、1004加好友
 *
 * 使用set 数据类型来完成
 *
 * @Author: Zz
 * @Date: 2024/03/03/12:04
 * @Description: 致敬
 */
@Component
public class FriendBySetService {

    //定义向redis 服务器中存储好友信息的key
    private static final String redisKey = "friendships";

    private static final String userKey ="user";

    private static final String finalKey = redisKey + ":"+userKey;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     *
     * 添加好友
     *  desc: 将friedNo编号对应用户 和多个friendsNo编号对应用户 互相添加为好友
     * @param friendNo 用户编号
     * @param friendsNo 带添加好友的用户编号
     */
    public void addFriends(String friendNo, HashSet<String> friendsNo){


        //验证数据合法性
        if(friendNo == null || friendsNo.isEmpty() || friendsNo == null){
            throw new RuntimeException("用户的数据不合法.....");
        }

        for (String addFriendNo : friendsNo) {

            stringRedisTemplate.opsForSet().add(redisKey+":"+userKey+ friendNo, userKey+addFriendNo);
            stringRedisTemplate.opsForSet().add(redisKey+":"+userKey+addFriendNo, userKey+friendNo);
        }

        System.out.println("用户添加完成.....");
    }


    /**
     * 推荐用户可能认识的好友
     * @param userNo1  用户标号， 唯一标识
     */
    public Set<String> recommendFriends(String userNo1, String userNo2){


        //验证数据的合法性
        if(userNo1 == null || userNo2 ==null){
            throw new RuntimeException("用户的数据不合法.....");
        }

        String maybeFriend= "maybeFriend";


        //计算差集并存储结果
       stringRedisTemplate.opsForSet().differenceAndStore( finalKey+userNo2, finalKey+ userNo1, maybeFriend);


       //移除用户本身
        stringRedisTemplate.opsForSet().remove(maybeFriend, userKey+ userNo1);

       // 返回可能认识的人
       return stringRedisTemplate.opsForSet().members(maybeFriend);

        //todo 要清空掉可能认识好友的集合，不能一直累加
    }

}
