package cn.zyy.bootredis.task.day07.entiy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Zz
 * @Date: 2024/03/04/17:12
 * @Description: 致敬
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Car {

    //车辆唯一标识
    private String id;

    //车辆颜色
    private String color;

    //车辆品牌
    private String brand;

    //车辆年份
    private Integer year;

    //车辆位置- 经度
    private double longitude;

    //车辆位置 -纬度
    private double latitude;
}
