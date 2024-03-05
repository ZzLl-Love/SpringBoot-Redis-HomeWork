package cn.zyy.bootredis.day07;

import cn.zyy.bootredis.task.day07.CarService;
import cn.zyy.bootredis.task.day07.TaxiService;
import cn.zyy.bootredis.task.day07.UserService;
import cn.zyy.bootredis.task.day07.entiy.Car;
import cn.zyy.bootredis.task.day07.entiy.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Zz
 * @Date: 2024/03/04/17:31
 * @Description: 致敬
 */
@SpringBootTest
public class BootRedisApplicationTest07 {


    @Resource
    private CarService carService;


    @Autowired
    private UserService userService;

    @Autowired
    private TaxiService taxiService;

    //存储车辆信息
    Map<String,Car> carMap = new HashMap();

    //存储用户信息
    Map<String,User> userMap = new HashMap();

    /**
     * 初始化车辆信息和用户信息数据
     */
    @BeforeEach
    void initData(){

        //car data init
        Car car1 = new Car("1003", "白色", "哈佛", 2018, 116.503154, 39.948035);
        Car car2 = new Car("1006", "白色", "宝来", 2012, 116.510305, 39.946237);
        Car car3 = new Car("1023", "红色", "奥迪", 2019, 116.509047, 39.94856);
        Car car4 = new Car("1031", "黑色", "长安", 2017, 116.503154, 39.948035);

        //添加车辆信息到map中
        carMap.put(car1.getId(),car1);
        carMap.put(car2.getId(),car2);
        carMap.put(car3.getId(),car3);
        carMap.put(car4.getId(),car4);

        //user data init
        User user1 = new User("001", "zhangsan", "泛海世家", 116.501501, 39.947205);
        User user2 = new User("002", "lisi", "泛海容郡", 116.50955, 39.948615);
        User user3 = new User("003", "wangwu", "泛海国际", 116.506676, 39.945407);

        userMap.put(user1.getId(), user1);
        userMap.put(user2.getId(), user2);
        userMap.put(user3.getId(), user3);
    }

    /**
     * 测试保存车辆和查询车辆信息
     */
    @Test
    void testCarService() throws JsonProcessingException {

        //保存车辆信息
        carService.saveCarInfo(carMap);
    }

    /**
     * 查询车辆信息
     */
    @Test
    void testGetCarInfo() throws JsonProcessingException {

        Map<String, Car> allCarInfos = carService.getAllCarInfos();
        for (Map.Entry<String, Car> carMap : allCarInfos.entrySet()) {
            String key = carMap.getKey();
            Car value = carMap.getValue();
            System.out.println("车标识:"+key + ",对应的具体信息为: " + value);
            System.out.println();
        }

    }


    /**
     * 测试保存用户信息
     */
    @Test
    void testSaveUserInfo() throws JsonProcessingException {

        userService.saveUserInfo(userMap);
    }

    /**
     * 测试查询用户所有信息
     * @throws JsonProcessingException
     */
    @Test
    void testGetUserInfo() throws JsonProcessingException {
        Map<String, User> allUserInfos = userService.getAllUserInfos();
        for (Map.Entry<String, User> useMap : allUserInfos.entrySet()) {
            String key = useMap.getKey();
            User value = useMap.getValue();
            System.out.println("用户标识:"+key + ",对应的具体信息为: " + value);
            System.out.println();
        }
    }

    /**
     * 测试查询单个车辆的信息
     * @throws JsonProcessingException
     */
    @Test
    void test() throws JsonProcessingException {
        Car oneCarInfo = carService.getOneCarInfo("1023");
        System.out.println(oneCarInfo);
    }

    /**
     * 测试离车辆最近的用户
     * @throws JsonProcessingException
     */
    @Test
    void getUserNearCar() throws JsonProcessingException {

        User user = taxiService.selUserNearByCar("1003");
        System.out.println("最近的用户是"+ user);
    }

    /**
     * 测试离用户最近的车
     */
    @Test
    void getCarNearUser() throws JsonProcessingException {

        //wangwu 对应的Key
        String userId = "003";

        Car car = taxiService.selCarNearByUser(userId);

        if(car != null){
            System.out.println("离用户最近的车是" + car);
        } else {
            System.out.println("附近没有合适的车辆....");
        }
    }
}
