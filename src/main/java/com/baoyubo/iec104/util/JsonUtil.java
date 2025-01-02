package com.baoyubo.iec104.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON 工具类
 *
 * @author yubo.bao
 * @date 2023/7/19 19:24
 */
public final class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    /**
     * 将 对象 转换为 JSON字符串
     *
     * @param obj obj
     * @return JSON字符串
     */
    public static String toJsonString(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("to json string exception, obj: {}", obj, e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 将 JSON字符串 转换为 对象
     *
     * @param json  JSON字符串
     * @param clazz clazz
     * @return 对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error("parse object exception, json: {}, clazz: {}", json, clazz.getName(), e);
            throw new RuntimeException(e);
        }
    }


    private JsonUtil() {
    }
}
