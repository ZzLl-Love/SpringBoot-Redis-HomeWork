package cn.zyy.bootredis.task.day07;

import cn.zyy.bootredis.task.day07.entiy.Car;
import cn.zyy.bootredis.task.day07.entiy.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * 叫车服务
 *
 * @Author: Zz
 * @Date: 2024/03/04/15:17
 * @Description:
 */

@Component
public class TaxiService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Autowired
    UserService userService;


    @Autowired
    CarService carService;

    //定义redisKey 离车最近的用户
    String redisKey = "nearestUser";

    //定义存储车辆经纬度 位置的redis key
    String carRedisKey = "car:Longitude:Latitude";



    //以用户为中心的半径
    @Value("${user.center.radius}")
    double user_center_radius;

    //以车辆为中心的半径
    @Value("${car.center.radius}")
    double car_center_radius;

    /**
     * 给车辆选择最近的用户
     * @param carId
     * @return
     */
    public User selUserNearByCar(String carId) throws JsonProcessingException {

        if(carId == null) {
            throw new RuntimeException("车辆标号为空");
        }

        //todo 附近没有用户 allUsrInfos 为空的情况

        //1.查询所有用户的经纬信息
        Map<String, User> allUserInfos = userService.getAllUserInfos();
        for (Map.Entry<String, User> stringUserEntry : allUserInfos.entrySet()) {

            String key = stringUserEntry.getKey();
            User user = stringUserEntry.getValue();

            //保存用户的经纬度信息
            stringRedisTemplate.opsForGeo().add(redisKey, new Point(user.getLongitude(),user.getLatitude()), key);
        }


        //2. 查询车辆的经纬度
        Car car = carService.getOneCarInfo(carId);
        //得到车的经度
        double carLongitude = car.getLongitude();
        //得到车的纬度
        double carLatitude = car.getLatitude();

        //保存车辆的经纬度信息
        stringRedisTemplate.opsForGeo().add(redisKey, new Point(carLongitude,carLatitude), "car:" + car.getId());


        //3.计算出离车辆最近的用户
        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();

        //以车辆为中心点
        Point point = new Point(carLongitude, carLatitude);

        //执行车辆中心点的半径
        Distance distance = new Distance(car_center_radius, org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit.METERS);


        //计算最近的用户
        GeoResults<RedisGeoCommands.GeoLocation<String>> nearUsers  = geoOps.radius(redisKey, new Circle(point, distance));

        if(!nearUsers.getContent().isEmpty()){
            GeoResult<RedisGeoCommands.GeoLocation<String>> nearestUser  = nearUsers.getContent().get(0);
            String userId = nearestUser.getContent().getName();

            return userService.getOneUserInfo(userId);
        }

        return null;
    }


    /**
     * 给用户选择最近的车辆
     * @param userId 用户标识
     * @return Car   最近车辆
     */
     public Car selCarNearByUser(String userId) throws JsonProcessingException {

         if(userId == null) {
             throw new RuntimeException("用户id为空....,无法筛选最近车辆");
         }

         //1. 获取所有车辆经纬度的位置信息
         Map<String, Car> allCarInfos = carService.getAllCarInfos();

         //判断附近是否有车辆
         if(allCarInfos.isEmpty()){
             throw new RuntimeException("附近没有车辆......");
         }

         //保存车辆经纬度信息
         for (Map.Entry<String, Car> carEntry : allCarInfos.entrySet()) {

             //获取key == car: carId
             String key = carEntry.getKey();
             Car car = carEntry.getValue();

             //保存车辆的经纬度信息
             stringRedisTemplate.opsForGeo().add(carRedisKey, new Point(car.getLongitude(), car.getLatitude()),key);
         }


         //2.获取传入用户的经纬度信息 todo userId 是否要拼接"car"
         User userInfo = userService.getOneUserInfo(userId);

         //获取用户经度信息
         double userLongitude = userInfo.getLongitude();

         //获取用户纬度信息
         double userLatitude = userInfo.getLatitude();

         //3. 计算离userId 最近的车辆信息
         GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();

         //以用户为中心点
         Point userPoint = new Point(userLongitude, userLatitude);

         //以用户为中心点的半径 500m
         Distance userDistance = new Distance(user_center_radius, RedisGeoCommands.DistanceUnit.METERS);

         //计算得到最近的车辆
         GeoResults<RedisGeoCommands.GeoLocation<String>> nearCar = geoOps.radius(carRedisKey, new Circle(userPoint, userDistance));

         //计算得到的车辆不为空
         if(!nearCar.getContent().isEmpty()){
             GeoResult<RedisGeoCommands.GeoLocation<String>> nearestCar = nearCar.getContent().get(0);
             String carId = nearestCar.getContent().getName();

           return  carService.getOneCarInfo(carId);
         }

         return null;
     }
}
