package com.heng.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heng.query.ESBoolQueryBuilder;
import com.heng.query.ESMatchQueryBuilder;
import com.heng.util.ESClientAbstract;
import com.heng.util.ESTransfortClient;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Test {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        ESBoolQueryBuilder builder = new ESBoolQueryBuilder();
        System.out.println(builder.must()
                .addField("name","heng")
                .addField("age", 11)
                .builder().getJson());
    }

    public void test() throws UnknownHostException {
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
