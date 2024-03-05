package cn.zyy.bootredis.task.day07.entiy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Zz
 * @Date: 2024/03/04/17:17
 * @Description: 致敬
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class User {

    private String id;

    //用户姓名
    private String name;

    //居住 小区名称
    private String communityName;

    //用户位置- 经度
    private double longitude;

    //用户位置 -纬度
    private double latitude;
}


