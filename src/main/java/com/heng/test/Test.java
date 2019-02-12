package com.heng.test;

import com.heng.bean.Boy;
import com.heng.bean.YaGao;
import com.heng.util.ESClient;
import com.heng.util.ESUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws Exception {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200,"http")));
        ESClient client1 = ESClient.ESClientBuilder.init(new HttpHost("localhost",9200,"http"));
        Boy boy = new Boy();
        YaGao yaGao = new YaGao();
        String[] tags = {"aaa","bbb","ccc"};
        yaGao.setTags(tags);
        boy.getYaGaos().add(yaGao);
        boy.getYaGaos().add(yaGao);
        boy.getYaGaos().add(yaGao);
        boy.getYaGaos().add(yaGao);
        Map<String,Object> map = client1.getMapping("aaa","Boy");
        /*CreateIndexRequest request = new CreateIndexRequest("bbb");

        Map<String,Object> jsonMap = new HashMap<>();
        Map<String,Object> message = new HashMap<>();

//        message.put("type", "text");
//        Map<String,Object> properties = new HashMap<>();
//        properties.put("message", message);
//        Map<String,Object> mapping = new HashMap<>();
//        mapping.put("properties", properties);
        jsonMap.put("bbb", ESUtils.beanToEsType(YaGao.class));
        request.mapping("bbb", jsonMap);

        System.out.println(request.toString());*/

//        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

        client1.close();
    }
}
