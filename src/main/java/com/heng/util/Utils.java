package com.heng.util;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

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
}
