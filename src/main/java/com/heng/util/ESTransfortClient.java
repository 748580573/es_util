package com.heng.util;

import com.alibaba.druid.stat.TableStat;
import com.heng.query.ESQueryBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ESTransfortClient extends ESClientAbstract {

    private TransportClient transportClient = null;

    private int status = 0;                   //状态

    private ESTransfortClient() {
    }

    /**
     * 关闭客户端
     *
     * @throws Exception
     */
    public void close() throws Exception {
        if (transportClient != null) {
            transportClient.close();
        } else {
            throw new Exception("客户端关闭出错，客户端未建立任何连接");
        }
    }

    /**
     * 添加索引
     * @param indexName
     * @param type 索引类型
     * @param data
     * @return
     */
    public RestStatus createIndex(String indexName,String type,Object data) {
        String json = Utils.objcetToJson(data);
        IndexResponse response = transportClient.prepareIndex(indexName,type)
                .setSource(json, XContentType.JSON )
                .get();
        return response.status();
    }

    public RestStatus  insertData(String indexName,String type,Object data) {
        IndexResponse response = transportClient.prepareIndex(indexName,type)
                .setSource(Utils.objcetToJson(data),XContentType.JSON)
                .get();
        return response.status();
    }

    public RestStatus update(String indexName, String type, String id, Map<String,Object> fields) {
        UpdateRequest request = new UpdateRequest(indexName,type,id);
        UpdateResponse response = null;
        try {
            String json = Utils.objcetToJson(fields);
            request.doc(json,XContentType.JSON);
            response = transportClient.update(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return response.status();
    }

    public boolean existField(String index,String type,String fieldName){

        return false;
    }

    public Map<String,Object> getDataById(String index, String type, String id){
        GetResponse response = transportClient.prepareGet(index,type,id)
                .get();
        return response.getSource();
    }

    /**
     * 未实现的功能
     * @param index
     * @param sql
     * @return
     */
    private Map<String, Object> getDataBySql(String index, String sql) {
        Map<String,Object> query = new HashMap<>();
        Map<String,Object> match = new HashMap<>();
        Map<String,Object> term = new HashMap<>();
        boolean hasMatch = false;
        boolean hasTerm = false;
        SQLUtil sqlUtil = SQLUtil.SQLUtilBuilder.build(sql);
        Set<TableStat.Name> table = sqlUtil.getTable();
        String type = null;
        for (TableStat.Name name : table){
            //获取第一个
            type = name.getName();
            break;
        }
        if (type != null){
            List<TableStat.Condition> list = sqlUtil.getConditions();
            for (TableStat.Condition condition : list){
                String str = condition.getOperator();
                switch (str){
                    case SQLUtil.OperatorType.LIKE:
                        hasMatch = true;
                        match.put(condition.getColumn().getName(),condition.getValues().stream().map(e -> e.toString()).reduce((a,b) -> a + " " + b).get());
                        break;
                    case SQLUtil.OperatorType.IN:
                        break;
                    default:

                }
            }
        }
        return null;
    }

    public void test(){
//        Map<String,Object> query = new HashMap<>();
//        Map<String,Object> bool = new HashMap<>();
//        List<Map<String,Object>> must = new ArrayList<>();
//        Map<String,Object> map = new HashMap<>();
//        Map<String,Object> match = new HashMap<>();
//
//        query.put("BOOL",bool);
//        bool.put("must",must);
//        must.add(map);
//        map.put("MATCH", match);
//        match.put("blogTilte","000");
//        String json = Utils.objcetToJson(query);
//        System.out.println(json);
        ESQueryBuilder builder = new ESQueryBuilder();

        System.out.println(builder.toString());
        WrapperQueryBuilder wqb = QueryBuilders.wrapperQuery(builder.toString());
        SearchResponse response = transportClient.prepareSearch("blog").setTypes("Blog").setQuery(wqb).get();
        SearchHit[] hits = response.getHits().getHits();
        for(SearchHit hit : hits){
            String content = hit.getSourceAsString();
            System.out.println(content);
        }
    }

    @Override
    public boolean existIndex(String index) {
        IndicesExistsResponse response = transportClient.admin().indices().exists(new IndicesExistsRequest().indices(index)).actionGet();
        return response.isExists();
    }

    @Override
    public boolean existType(String index, String type) {
        TypesExistsResponse response = transportClient.admin().indices().typesExists(new TypesExistsRequest(new String[]{index},type)).actionGet();
        return response.isExists();
    }

    @Override
    public RestStatus delete(String indexName, String type, String id) {
        DeleteResponse response = transportClient.prepareDelete(indexName, type, id).get();
        return response.status();
    }

    public static class ESClientBuilder {


        /**
         * 以给定的节点初始化客户端
         *
         * @return
         */
        public static ESTransfortClient init(Settings settings, String hostname, int port) throws UnknownHostException {
            ESTransfortClient client = new ESTransfortClient();
            try {
                String clusterName = settings.get("cluster.name");
            }catch (NullPointerException e){
                throw new UnknownHostException("请设置集群名");
            }

            client.transportClient = new PreBuiltTransportClient(settings);
            String[] hostnames = hostname.split(",");
            for (String str : hostnames){
                client.transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName(str),port));
            }
            return client;
        }
    }
}
