package cn.zyy.bootredis.task.day03;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * task: 模拟1001用户在快手发布一个作品，1002、1003用户为其点赞，并统计点赞人数
 * @Author: Zz
 * @Date: 2024/03/03/14:36
 * @Description: 致敬
 */
@Component
public class LikeService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //作品key
    private final static String workKey = "work:";

    //用户key
    public static final  String userKey = "user:";

    /**
     * 发布作品
     * @param workId  作品id
     * @param context 作品内容
     */
    public String publishWorks(String workId, String context){

        if(workId == null || context == null){
            throw new RuntimeException("用户传入数据非法....");
        }

        //发布作品
         stringRedisTemplate.opsForSet().add(workKey + workId + ":info", context);

        System.out.println("发布"+workId+"作品成功...");

        return  workId;

    }

    /**
     * 删除作品
     * @param workId
     */
    public void removeWorks(String workId){

        if(workId == null) {
            throw new RuntimeException("用户传入数据非法");
        }

        //作品存在才删除
        if(stringRedisTemplate.hasKey(workKey+workId+":info")){
            //删除作品信息
            stringRedisTemplate.delete(workKey + workId+":info" );
            //删除作品点赞信息
            stringRedisTemplate.delete(workKey+workId + ":userLikes");
            System.out.println("作品"+workId + "删除成功......");
        }

    }

    /**
     * 用户点赞
     * @param workId 作品id
     * @param userId 点赞用户id
     */
    public void likeWork(String workId, String userId){

        //验证非法性
        if(workId == null || userId == null){
            throw new RuntimeException("用户传入数据不合法.....");
        }

        //判断作品是否存在
        if(!stringRedisTemplate.hasKey(workKey +workId +":info")){
            throw new RuntimeException("用户作品不存在，无法点赞.....");
        }

        stringRedisTemplate.opsForSet().add(workKey+workId + ":userLikes", userKey+userId);

    }


    /**
     * 用户取消点赞
     * @param workId
     * @param userId
     */
    public void cancelLike(String workId, String userId){

        if(workId == null || userId ==null){
            throw new RuntimeException("用户传入数据非法");
        }

        //判断作品是否存在
        if(!stringRedisTemplate.hasKey(workKey +workId +":info")){
            throw new RuntimeException("用户作品不存在...");
        }

        //取消点赞
        Long remove = stringRedisTemplate.opsForSet().remove(workKey + workId + ":userLikes", userKey + userId);

        if(remove ==0){
            throw new RuntimeException("用户"+userId+"请先点赞，才能取消赞....");
        }

        System.out.println("用户"+userId+"取消点赞成功......");
    }


    /**
     * 判断用户是否已经点赞
     * @param workId
     * @param userId
     * @return
     */
    public Boolean isLike(String workId, String userId){

        if(workId == null || userId ==null){
            throw new RuntimeException("用户传入数据非法");
        }

        return stringRedisTemplate.opsForSet().isMember(workKey + workId + ":userLikes", userKey + userId);


    }


    /**
     *  显示点赞用户
     * @param workId
     * @return
     */
    public Set<String> getLikeWorkUser(String workId){
        if(workId == null ){
            throw new RuntimeException("未传入作品"+ workId);
        }

        return stringRedisTemplate.opsForSet().members(workKey + workId + ":userLikes");
    }

    /**
     * 获取点赞次数
     * @param workId
     * @return
     */
    public  Long getNumberOfLikes(String workId){

        if(workId == null ){
            throw new RuntimeException("用户传入数据非法");
        }

        //判断作品是否存在
        if(!stringRedisTemplate.hasKey(workKey +workId +":info")){
            throw new RuntimeException("用户作品已经删除了......");
        }


       return stringRedisTemplate.opsForSet().size(workKey + workId + ":userLikes");
    }
}
