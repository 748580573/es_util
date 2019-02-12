package com.heng.enumerate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 将与数组有关的集合进行枚举
 */
public enum SetEnum {
    ARRAY(0, Array.class,"array"),                  //这里用Array类替代为基础类型意外数组的标识
    LIST(1, List.class,"list"),

    BASE(19,null,"base"),                           //Base代表基础类型
    STRING(2, java.lang.String[].class,"string"),
    _BOOLEAN(3,java.lang.Boolean[].class,"boolean"),
    _BYTE(4,java.lang.Byte[].class,"byte"),
    _SHORT(5,java.lang.Byte[].class,"short"),
    _CHAR(6,java.lang.Character[].class,"char"),
    _INT(7,java.lang.Integer[].class,"int"),
    _FLOAT(8,java.lang.Float[].class,"float"),
    _LONG(9,java.lang.Long[].class,"long"),
    _DOUBLE(10,java.lang.Double[].class,"double"),
    BOOLEAN(11, boolean[].class,"boolean"),
    BYTE(12,byte[].class,"byte"),
    SHORT(13,short[].class,"short"),
    CHAR(14,char[].class,"char"),
    INT(15,int[].class,"int"),
    FLOAT(16,float[].class,"float"),
    LONG(17,long[].class,"long"),
    DOUBLE(18,double[].class,"double");



    private Class<?> clazz;

    private int id;

    private String type;

    /**
     *
     * @param id
     * @param clazz
     * @param type
     */
    SetEnum(int id,Class<?> clazz,String type){
        this.id= id;
        this.clazz = clazz;
        this.type = type;
    }

    public static SetEnum getType(Class<?> clazz){
        //排除基础类型与String的数组形式
        for (SetEnum setEnum : SetEnum.values()){
            if (clazz.isArray()){
                if (setEnum.clazz == clazz){
                    return BASE;
                }
            }
        }

        //集合
        if (LIST.clazz.isAssignableFrom(clazz)){
            return LIST;
        }

        for (SetEnum setEnum : SetEnum.values()){
            //引用类型的数组
            if (setEnum.clazz == Array.class && clazz.isArray()){
                return setEnum;
            }
        }
        return null;
    }
}
