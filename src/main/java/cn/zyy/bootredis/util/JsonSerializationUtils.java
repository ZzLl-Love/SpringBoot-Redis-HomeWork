package cn.zyy.bootredis.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Author: Zz
 * @Date: 2024/02/22/0:05
 * @Description:  序列化工具
 */
public class JsonSerializationUtils  {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 序列化为json 对象
     * @param object
     * @return
     */
    public  static  String serializeObject(Object object) throws JsonProcessingException {

        // 设置 ObjectMapper 的配置，使其不序列化属性值为 null 的属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper.writeValueAsString(object);

    }


    /**
     *
     * 从JSON字符串反序列化对象
     * @param json
     * @param classType
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> T deserializeObject(String json, Class<T> classType) throws JsonProcessingException {
        return objectMapper.readValue(json, classType);
    }
}
