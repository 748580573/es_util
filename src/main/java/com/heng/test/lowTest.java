package com.heng.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heng.bean.Boy;
import com.heng.bean.Student;
import com.heng.bean.YaGao;
import com.heng.util.ClazzConvert;
import com.heng.util.ESUtils;
import com.heng.util.Graph;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class lowTest {

    public static void main(String[] args) throws Exception {
        Graph<Integer> graph = new Graph<>();

        Boy boy = new Boy();
        YaGao yaGao = new YaGao();
        String[] tags = {"aaa","bbb","ccc"};
        yaGao.setTags(tags);
        boy.getYaGaos().add(yaGao);
        boy.getYaGaos().add(yaGao);
        boy.getYaGaos().add(yaGao);
        boy.getYaGaos().add(yaGao);
        Map<String,Object> map = ESUtils.beanToEsData(boy);
        System.out.println("hello");

        /*Student student = new Student();
        student.setNumber(1);
        Boy boy = new Boy();
        boy.setHeight(165);
        student.setBoy(boy);
        Map<Object,Object> map = ESUtils.beanToEsData(student);
        System.out.println("hello");

        ESClient client = ESClient.ESClientBuilder.init(new HttpHost("localhost",9200,"http"));
        client.createIndex("bbb", "test", Student.class);
        client.insertData("bbb","test",student,"2");
        client.close();*/
    }
}
