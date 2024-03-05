package cn.zyy.bootredis.entiy;

import lombok.*;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @Author: Zz
 * @Date: 2024/02/21/23:38
 * @Description: 致敬
 * 用户类
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {

    private Integer id;

    private String name;

    private Integer age;

    private String address;

    private String phone;

    private String code;


    public User(Integer id, String name, Integer age, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }
}
