package com.heng.util;

import com.heng.enumerate.BaseEnum;
import com.heng.enumerate.SetEnum;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 类型转换
 */
public class ClazzConvert {

    /**
     * 将bean中字段类型转换为es中对应的类型
     * @param clazz
     * @return
     */
    public static String convertClazz(Class<?> clazz){
        String result = BaseEnum.getType(clazz);
        return result;

    }

    /**
     * 获取集合中元素的类型
     */
    public static Class<?> getElementClazzOfSet(Class<?> clazz){
        if (clazz.isArray()){
            //数组
            return clazz.getComponentType();
        }else {
            if (Collection.class.isAssignableFrom(clazz)){
                ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
                for (Type type1 : type.getActualTypeArguments()){
                    System.out.println(type1.getTypeName());
                }
                return clazz;
            }

        }
        return null;
    }
}
