package cn.zyy.bootredis.day03;

import cn.zyy.bootredis.task.day01.UserInfoServiceByString;
import cn.zyy.bootredis.task.day03.FriendBySetService;
import cn.zyy.bootredis.task.day03.LikeService;
import cn.zyy.bootredis.task.day03.LotteryServiceBySet;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Zz
 * @Date: 2024/02/25/21:35
 * @Description: 致敬
 */
@SpringBootTest
public class BootRedisApplicationTests02 {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Autowired
    LotteryServiceBySet lotteryServiceBySet;

    @Autowired
    UserInfoServiceByString userInfoServiceByString;

    @Autowired
    FriendBySetService friendService;

    @Autowired
    LikeService likeService;

    /**
     *测试抽奖
     */
    @Test
    public void  testLottery() throws JsonProcessingException {

        //取出第一天保存所有用户的key
        Set<String> allUserInfoKey = userInfoServiceByString.getAllUserInfoKey("user*");

        //把用户添加到抽奖系统中
        lotteryServiceBySet.saveAllUser("participants", allUserInfoKey);


        //执行抽奖
        lotteryServiceBySet.drawLottery("participants");

        //打印未抽到奖的用户
        Set<String> participants1 = lotteryServiceBySet.getAllUser("participants");
        System.out.println("未抽到奖的用户" + participants1);
    }


    /**
     * 测试添加好友方法
     */
    @Test
    public void testAddFriends(){

        String friendNo = "1001";
        HashSet<String> friendsNo = new HashSet<>();
        friendsNo.add("1002");
        friendService.addFriends(friendNo, friendsNo);

        String friendNo2="1002";
        HashSet<String> friendsNo2 = new HashSet<>();
        friendsNo2.add("1003");
        friendsNo2.add("1004");
        friendService.addFriends(friendNo2,friendsNo2);


    }

    /**
     * 测试可能认识的人
     */
    @Test
    void testMayBeFriend(){
        Set<String> fNo = friendService.recommendFriends("1001", "1002");
        System.out.println("可能认识的用户编号: " + fNo);
    }

    /**
     * 测试用户点赞功能
     */
    @Test
    void testLikeService(){

          //发布作品
        String workId = likeService.publishWorks("1001", "first work test");

        //用户点赞
        likeService.likeWork("1001" , "1002");
        likeService.likeWork("1001" , "1003");

        //获取点赞用户
        Set<String> likeWorkUser = likeService.getLikeWorkUser("1001");
        System.out.println("给"+workId+"作品点赞的有：" + likeWorkUser);


        //取消点赞
        likeService.cancelLike("1001","1002");

        //判断用户是否已经点点过赞
        Boolean isLike = likeService.isLike("1001", "1003");
        if(isLike){
            System.out.println("已经点过赞了");
        }else{
            System.out.println("还未点过赞");
        }


        //获取作品点赞次数
        Long numberOfLikes = likeService.getNumberOfLikes("1001");
        System.out.println("作品点赞次数为:" + numberOfLikes);

        //删除作品
        likeService.removeWorks("1001");
    }

//    @Test
//    public void test() throws JsonProcessingException {
//
////        //取出第一天保存所有用户的key
//     Set<String> allUserInfoKey = userInfoServiceByString.getAllUserInfoKey("user*");
////
//      lotteryServiceBySet.saveAllUser("participants", allUserInfoKey);
//
//
//        Set<String> participants = lotteryServiceBySet.getAllUser("participants");
//        ArrayList<String> list = new ArrayList<>();
//
//        for (String participant : participants) {
//            System.out.println(participant);
//        }
//    }
}
