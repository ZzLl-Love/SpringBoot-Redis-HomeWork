package cn.zyy.bootredis.task.day07;

import cn.zyy.bootredis.task.day07.entiy.Car;
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
public class CarService {

    //定义redis 标识
    public static final String redisKey = "carInfo";

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 保存车辆信息
     * @param carInfos
     */
    public void saveCarInfo(Map<String, Car> carInfos) throws JsonProcessingException {

        if(carInfos == null || carInfos.isEmpty()){
            throw new RuntimeException("车辆信息为空，无法保存....");
        }


        //遍历添加车辆信息
        for (Map.Entry<String, Car> carInfo : carInfos.entrySet()) {

            String carId = carInfo.getKey();
            Car car = carInfo.getValue();

            redisTemplate.opsForHash().put(redisKey,"car:"+carId, JsonSerializationUtils.serializeObject(car));
        }

    }

    /**
     * 获取所有车辆的信息
     */
    public Map<String,Car> getAllCarInfos() throws JsonProcessingException {

        HashMap<String, Car> carHashMap  = new HashMap<>();


        //验证数据非法性
        if(!redisTemplate.hasKey(redisKey)){
            throw new RuntimeException("还未保存车辆信息，无法查询.....");
        }


        //得到所有的车辆信息
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(redisKey);

        //没有查询到车辆信息，返回为null
        if(entries.isEmpty()){
            return carHashMap = null;
        }

        //查询到车辆信息 遍历添加到Map中，然后返回
        for (Map.Entry<Object, Object> objectObjectEntry : entries.entrySet()) {
            String key =(String) objectObjectEntry.getKey();
            String carString = (String)objectObjectEntry.getValue();

            Car car = JsonSerializationUtils.deserializeObject(carString, Car.class);

            //获取车辆信息 放入新的map中
            carHashMap.put(key, car);
        }

        return carHashMap;
    }

    /**
     * 获取单个车辆的信息
     * @param carId
     * @return
     */
    public Car getOneCarInfo(String carId) throws JsonProcessingException {

        String key_Prefix = "car:";

         if(carId == null) {
             throw new RuntimeException("车辆id为空");
         }

         //判断是否带有前缀
         if(!carId.startsWith(key_Prefix)){
             carId= key_Prefix + carId;
         }

         //得到单个car的json字符串
         String carString = (String)redisTemplate.opsForHash().get(redisKey,  carId);


        //反序列化为Car对象
       return JsonSerializationUtils.deserializeObject(carString,Car.class);

       //https://github.com/ZzLl-Love/Redis-HomeWork.git
    }
}
