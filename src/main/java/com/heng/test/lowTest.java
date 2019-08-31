package com.heng.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heng.bean.Blog;
import com.heng.bean.Boy;
import com.heng.bean.Student;
import com.heng.bean.YaGao;
import com.heng.util.ClazzConvert;
import com.heng.util.ESClient;
import com.heng.util.ESUtils;
import com.heng.util.Graph;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class lowTest {

    public static void main(String[] args) throws Exception {
        Graph<Integer> graph = new Graph<>();

        Blog blog = new Blog();
        blog.setBlogCode("123");
        blog.setBlogContent("<div>123</div>");
        blog.setBlogConverUrl("/img/aaa.jpg");
        blog.setBlogDesc("测试");
        blog.setBlogTilte("测试博客");
        blog.setTags("java spring");
        Map<String,Object> map = ESUtils.beanToEsData(blog);
        System.out.println("hello");

        /*Student student = new Student();
        student.setNumber(1);
        Boy boy = new Boy();
        boy.setHeight(165);
        student.setBoy(boy);
        Map<Object,Object> map = ESUtils.beanToEsData(student);
        System.out.println("hello");*/

        ESClient client = ESClient.ESClientBuilder.init(new HttpHost("localhost",9200,"http"));
        SearchResponse response = client.preciseSearch("blog", "Blog", "blogCode", "8ca28c7c22b54bbf443da0f4f2071ef6");
        System.out.println(response.getHits().totalHits);
//        client.createIndex(blog.getClass().getSimpleName().toLowerCase(),blog.getClass().getSimpleName() , Blog.class);
//        client.insertData("bbb","test",blog,"1");
        client.close();
    }
}
