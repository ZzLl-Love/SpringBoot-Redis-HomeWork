package cn.zyy.bootredis.task.day09;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 *
 * task: 微博热搜排名，有一次搜索，分数加10分，并按分数权重显示排名
 *
 * @Author: Zz
 * @Date: 2024/03/03/16:25
 * @Description: 致敬
 */
@Component
public class HotSearchService {

    //定义redis的key
    public static final String redisKey = "searches";

    @Value("${hotSearch.scoreToAdd}")
    public  Integer  scoreToAdd;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     *  对关键词进行热搜排名
     * @param keyWord 关键词
     */
    public void handleHotSearch(String keyWord ){

        if(keyWord == null) {
            throw new RuntimeException("关键词为空.....");
        }

        stringRedisTemplate.opsForZSet().incrementScore(redisKey, keyWord, scoreToAdd);
    }


    /**
     * 得到搜索排名
     */
    public void getSearchRanking(){
        Set<ZSetOperations.TypedTuple<String>> rankedSearches  = stringRedisTemplate.opsForZSet().reverseRangeWithScores(redisKey, 0, -1);

        //热搜排名
        int rank  = 1;

        // 遍历打印出热搜排名
        for (ZSetOperations.TypedTuple<String> tuple : rankedSearches) {

           String searchKeyWord = tuple.getValue();
           Double score = tuple.getScore();

            System.out.println("排名" + rank + "的热搜词是:"+ searchKeyWord +"得分为:" + score);
            rank ++;
        }


    }
}
