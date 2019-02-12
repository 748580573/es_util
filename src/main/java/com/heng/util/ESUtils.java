package com.heng.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heng.enumerate.BaseEnum;
import com.heng.enumerate.SetEnum;
import com.heng.exception.CycleReferenceException;
import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ESUtils {

    /**
     * 将Bean转换为es对应类型
     * @warining 改方法不支持类型的循环解析
     * @param clazz  bean所对应的类型
     * @param  nested 类形种是否包含嵌套类型,只用作标识
     * @return
     */
    private static Map<String,Object> beanToEsType(Class<?> clazz, boolean nested) throws Exception {
        Map<String,Object> property = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
            if (clazz == field.getType()){
                throw new Exception("不支持同类型字段的循环解析，解析" + field.getName() + "字段失败");
            }
            Map<String,Object> propertyType = new HashMap<>();
            String type = ClazzConvert.convertClazz(field.getType());
            //处理基础类型以外的类型
            if (type == null){
                type = ClazzConvert.convertClazz(field.getType().getComponentType());
                type = (type == null ? "nested":type);
            }
            propertyType.put("type", type);
            //此处代替nested，省略了nested = "nested".equals(type)
            if ("nested".equals(type)){
                propertyType.put("properties", beanToEsType(field.getType(),true));
            }
            property.put(field.getName(), propertyType);
        }
        return property;
    }

    public static Map<String,Object> beanToEsType(Class<?> clazz) throws Exception {
        Map<String,Object> properties = new HashMap<>();
        Map<String,Object> property = beanToEsType(clazz,false);
        properties.put("properties", property);
        return properties.size() > 0 ? properties : null;
    }

    /**
     * 将object转换为es类型对应的数据
     * @param object
     * @return
     */
    public static Map<String,Object> beanToEsData(Object object) throws IllegalAccessException, NoSuchMethodException, CycleReferenceException, IOException {
        checkExistCycleReference(object);                        //检查object是否存在循环引用
        Map<String,Object> result = new HashMap<>();
        Class<?> clazz = object.getClass();

        Stack<Object> stack = new Stack<>();
        stack.push(object);

        Map<Object,Integer> hash = new HashMap<>();          //用于记录对象与其对应的hashcode
        Map<Object,Integer> relation = new HashMap<>();      //用于记录字段的父子关系
        Map<Object,Map<String,Object>> objectToGetMap = new HashMap<>();       //用于记录字段对象转换后对应的Map


        objectToGetMap.put(object,result);
        hash.put(object,object.hashCode());
        /**
         *
         */
        while (!stack.isEmpty()){
            Object currObject = stack.pop();                     //父对象
            if (currObject != null){
                Map<String,Object> currMap = objectToGetMap.get(currObject);
                Class<?> tempClazz = currObject.getClass();
                Field[] fields = tempClazz.getDeclaredFields();
                for (Field field : fields){
                    field.setAccessible(true);
                    String fieldName = field.getName();
//                    Method method = tempClazz.getDeclaredMethod(obtainGeterMethod(field));
//                    Object subObject = invokeMethod(currObject, method);              //子对象
                    Object subObject = field.get(currObject);           //子对象
                    //不为基本类型
                    if (BaseEnum.getType(field.getType()) == null){
                        if (subObject == null){
                            currMap.put(fieldName,"null" );
                            continue;
                        }
                        //集合类型
                        if (SetEnum.getType(field.getType()) != null){
                            /*stack.push(subObject);
                            Map<String,Object> objectMap = new HashMap<>();
                            objectToGetMap.put(subObject, objectMap);
                            currMap.put(fieldName, subObject);*/
                            List list = convertToList(subObject);
                            currMap.put(fieldName, list);
                        }else {
                            //对非集合的引用类型进行处理
                            stack.push(subObject);
                            Map<String,Object> objectMap = new HashMap<>();
                            objectToGetMap.put(subObject, objectMap);
                            currMap.put(fieldName, subObject);
                        }
                    }else {
                        //基本类型
                        currMap.put(fieldName,subObject);
                    }
                }
            }
        }

        return result;
    }

    public static List convertToList(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(object);
        return mapper.readValue(json, List.class);
    }


    /**
     * 判断传入的类型是否为基础类型
     * @param clazz
     * @return
     */
    public static boolean isBaseType(Class<?> clazz){
        String type =  ClazzConvert.convertClazz(clazz);
        return type != null;
    }

    /**
     * 检查是否存在循环引用
     * @param object
     * @return
     */
    public static boolean checkExistCycleReference(Object object) throws CycleReferenceException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Graph<Class<?>> graph = new Graph<>();
        Queue<Class<?>> queue = new LinkedList<>();


        queue.add(clazz);

        Stack<Class<?>> stack = new Stack<>();
        Map<Class<?>,Object> rela = new HashMap<>();
        rela.put(clazz, object);
        stack.push(clazz);
        //获取集合中的类型
        while (!stack.isEmpty()){
            Class<?> tempClazz = stack.pop();
            Object ob = rela.get(tempClazz);
            Field[] fields = tempClazz.getDeclaredFields();
            for (Field field : fields){
                field.setAccessible(true);
                if (SetEnum.getType(field.getType()) != null){
                    SetEnum setEnum = SetEnum.getType(field.getType());
                    if (setEnum == SetEnum.LIST){
                        List list = (List) field.get(object);
                        if (list != null && list.size() > 0){
                            Class<?> fiedClazz = list.get(0).getClass();
                            if (!queue.contains(fiedClazz)){
                                queue.add(fiedClazz);
                            }
                            rela.put(fiedClazz, list.get(0));
                            graph.addSide(tempClazz, fiedClazz);
                        }
                    }
                }
            }
        }

        stack.clear();


        while (!queue.isEmpty()){
            Class<?> tempClazz = queue.remove();
            Field[] fields = tempClazz.getDeclaredFields();


            for (Field field : fields){
                if (SetEnum.getType(field.getType()) != null){
                    SetEnum setEnum = SetEnum.getType(field.getType());
                    switch (setEnum){
                        case ARRAY:
                            queue.add(field.getType().getComponentType());
                            graph.addSide(tempClazz,field.getType());
                            break;
                    }
                }else if (!isBaseType(field.getType()) && SetEnum.getType(field.getType()) == null){
                    queue.add(field.getType());
                    graph.addSide(tempClazz,field.getType());
                }
            }

        }

        queue.clear();
        return false;
    }

    /**
     * 数据的类型转换
     * @return
     */
    public static Object ConvertData(){
        return null;
    }

    /**
     *
     * @param object  传入的对象
     * @param method  object种的一个方法
     * @param params  向方法种传入参数
     * @return  返回调用方法的结果
     */
    public static Object invokeMethod(Object object,Method method,Object... params){
        try {
            Object result = method.invoke(object, params);
            return result;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取某个字段的getter方法
     * @param field
     * @return
     */
    public static String obtainGeterMethod(Field field){
        String fieldName = field.getName();
        char[] chars = fieldName.toCharArray();
        if (chars[0] >= 'a' && chars[0] <= 'z'){
            chars[0] = (char)(chars[0] - 32);
        }
        String getMethod = "get" + new String(chars);
        return getMethod;
    }

    public static void test(Object object){
        Class<?> clazz = object.getClass();
        Graph<Class<?>> graph = new Graph<>();
        Queue<Class<?>> queue = new LinkedList<>();

        queue.add(clazz);

        Stack<Class<?>> stack = new Stack<>();
        Map<Class<?>,Object> rela = new HashMap<>();
        rela.put(clazz, object);
        stack.push(clazz);


        try {
            //获取集合中的类型
            while (!stack.isEmpty()){
                Class<?> tempClazz = stack.pop();
                Object ob = rela.get(tempClazz);
                Field[] fields = tempClazz.getDeclaredFields();
                for (Field field : fields){
                    if (SetEnum.getType(field.getType()) != null){
                        SetEnum setEnum = SetEnum.getType(field.getType());
                        if (setEnum == SetEnum.LIST){
                            List list = (List) field.get(object);
                            if (list != null && list.size() > 0){
                                Class<?> fiedClazz = list.get(0).getClass();
                                rela.put(fiedClazz, list.get(0));
                                graph.addSide(tempClazz, fiedClazz);
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stack.empty();
    }

}
