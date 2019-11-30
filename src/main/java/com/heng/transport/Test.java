package com.heng.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heng.bean.Blog;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
public class Test {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        System.out.println(Blog.class.getSimpleName());
    }

    public static void test() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "heng")
                .put("client.transport.sniff", true)
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        SearchRequestBuilder srb1 = client.prepareSearch()
                .setQuery(QueryBuilders.queryStringQuery("测试博客")).setSize(1);
        MultiSearchResponse sr = client.prepareMultiSearch()
                .add(srb1)
                .get();
        long nbHits = 0;
        for (MultiSearchResponse.Item item : sr.getResponses()){
            SearchResponse response = item.getResponse();
            for (SearchHit hit : response.getHits()){
                System.out.println(hit.getSourceAsString());
            }
        }


    }
}
