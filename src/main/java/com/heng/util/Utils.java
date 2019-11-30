package com.heng.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class Utils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String md5(String plainText){
        //定义一个字节数组
        byte[] secreBytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            secreBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String md5code = new BigInteger(1,secreBytes).toString(16);
        for (int i = 0;i < 32 - md5code.length();i++){
            md5code = "0" + md5code;
        }
        return md5code;
    }

    public static boolean isEmpty(Object object){
        return object == null;
    }

    public static String objcetToJson(Object object){
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T jsonToObject(String json,Class<T> clazz){
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map objectToMap(Object object) {
        String json = objcetToJson(object);
        Map map = null;
        try {
            map = mapper.readValue(json, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
