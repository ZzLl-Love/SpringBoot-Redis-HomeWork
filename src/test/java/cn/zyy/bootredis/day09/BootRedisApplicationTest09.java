package cn.zyy.bootredis.day09;

import cn.zyy.bootredis.task.day09.HotSearchService;
import cn.zyy.bootredis.task.day09.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author: Zz
 * @Date: 2024/03/03/19:31
 * @Description: 致敬
 */
@SpringBootTest
public class BootRedisApplicationTest09 {


    @Autowired
    private LoginService loginService;

    @Autowired
    private HotSearchService hotSearchService;

    /**
     * 测试登录
     */
    @Test
    void testLoginService(){

        boolean login = loginService.userLogin("admin", "test");
        System.out.println(login ? "登录成功": "登录失败");
    }

    /**
     * 测试热搜
     */
    @Test
    void testHotSearchTop(){

        //处理热搜
        hotSearchService.handleHotSearch("meizu");
        hotSearchService.handleHotSearch("meizu");
        hotSearchService.handleHotSearch("meizu");
        hotSearchService.handleHotSearch("xiaomi");
        hotSearchService.handleHotSearch("xiaomi");
        hotSearchService.handleHotSearch("huawei");

        //得到热搜排名
        hotSearchService.getSearchRanking();
    }
}
