package com.heng.util;

import com.heng.exception.CycleReferenceException;
import org.apache.http.HttpHost;
import org.apache.lucene.index.IndexNotFoundException;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ESClient {


    private RestHighLevelClient client = null;

    private ESClient() {
    }

    /**
     * 关闭客户端
     *
     * @throws Exception
     */
    public void close() throws Exception {
        if (client != null) {
            client.close();
        } else {
            throw new Exception("客户端关闭出错，客户端未建立任何连接");
        }
    }

    /**
     * 以默认配置创建创建索引
     *
     * @param indexName，索引名
     * @return 返回结果，包含响应码和响应内容
     * @throws IOException
     */
    public CreateIndexResponse createIndex(String indexName) throws Exception {
        return createIndex(indexName, null, null, null);
    }


    /**
     * 以指定的Setting创建索引
     *
     * @param indexName
     * @param settings
     * @return
     * @throws IOException
     */
    public CreateIndexResponse createIndex(String indexName, Settings settings) throws Exception {

        return createIndex(indexName, null, null, settings);
    }

    /**
     * @param indexName 索引名
     * @param bean      创建索引时需要映射的类型
     * @param settings  索引设置
     * @return 返回结果，响应内容
     * @throws Exception
     */
    public CreateIndexResponse createIndex(String indexName, Class<?> bean, Settings settings) throws Exception {
        return createIndex(indexName, null, bean, settings);
    }

    /**
     * @param indexName 索引名
     * @param bean      创建索引时需要映射的类型
     * @param  type 创建的文档类型
     * @return 返回结果，响应内容
     * @throws Exception
     */
    public CreateIndexResponse createIndex(String indexName,String type, Class<?> bean) throws Exception {
        return createIndex(indexName, type, bean, null);

    }

    /**
     * @param indexName 索引名
     * @param bean      创建索引时需要映射的类型
     * @return 返回结果，包含响应内容
     * @throws Exception
     */
    public CreateIndexResponse createIndex(String indexName, Class<?> bean) throws Exception {
        return createIndex(indexName, null, bean, null);
    }

    /**
     * @param indexName 索引名
     * @param bean      创建的类型
     * @param typeName  类型名
     * @param settings  索引设置
     * @return 返回结果，包含响应内容
     */
    public CreateIndexResponse createIndex(String indexName, String typeName, Class<?> bean, Settings settings) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> jsonMap = new HashMap<>();
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        if (settings != null) {
            request.settings(settings);
        }
        if (typeName == null) {
            typeName = bean.getSimpleName();
        }

        jsonMap.put(typeName, ESUtils.beanToEsType(bean));
        request.mapping(typeName, jsonMap);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

        return response;
    }

    /**
     * 删除索引
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public AcknowledgedResponse deleteIndex(String indexName) throws IOException {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            return response;
        } catch (ElasticsearchException exception) {
            exception.printStackTrace();
        }
        return null;

    }

    /**
     * 索引是否存在
     *
     * @param IndexName
     * @return
     * @throws IOException
     */
    public boolean existIndex(String IndexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(IndexName);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        return exists;
    }

    /**
     * 打开关闭所以i你，使索引种的数据可查     *
     *
     * @param indexName 索引名
     * @return
     * @throws IOException
     */
    public OpenIndexResponse openIndex(String indexName) throws IOException {
        if (!existIndex(indexName)) {
            throw new IndexNotFoundException("请求失败,索引" + indexName + "不存在");
        }
        OpenIndexRequest request = new OpenIndexRequest(indexName);
        OpenIndexResponse response = client.indices().open(request, RequestOptions.DEFAULT);
        return response;
    }

    /**
     * 关闭索引，使索引内数据不可查
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public AcknowledgedResponse closeIndex(String indexName) throws IOException {
        if (!existIndex(indexName)) {
            throw new IndexNotFoundException("请求失败,索引" + indexName + "不存在");
        }
        CloseIndexRequest request = new CloseIndexRequest(indexName);
        AcknowledgedResponse response = client.indices().close(request, RequestOptions.DEFAULT);
        return response;
    }

    /**
     * 获得索引下某个类型的映射信息
     *
     * @param indexName
     * @param type
     * @return
     * @throws IOException
     */
    public Map<String, Object> getMapping(String indexName, String type) throws IOException {
        GetMappingsRequest request = new GetMappingsRequest();
        request.indices(indexName);
        request.types(type);
        GetMappingsResponse response = client.indices().getMapping(request, RequestOptions.DEFAULT);
        ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> allMappings = response.mappings();
        MappingMetaData typeMapping = allMappings.get(indexName).get(type);
        Map<String, Object> mapping = typeMapping.sourceAsMap();
        return mapping;
    }

    /**
     *
     * @param indexName  索引名
     * @param type       文档类型
     * @param data       需要插入的数据
     * @param id         id
     */
    public IndexResponse insertData(String indexName,String type,Object data,String id){
        if (type == null){
            type = data.getClass().getSimpleName();
        }
        IndexRequest request;
        if (id == null){
            request = new IndexRequest(indexName,type);
        }else {
            request = new IndexRequest(indexName,type,id);
        }
        IndexResponse response = null;
        Map<String, Object> jsonData = null;
        try {
            jsonData = ESUtils.beanToEsData(data);
            if (data != null){
                request.source(jsonData);
                response = client.index(request,RequestOptions.DEFAULT);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CycleReferenceException e) {
            e.printStackTrace();
        }
        return response;
    }

    public IndexResponse insertData(String indexName,Object data,String id){
        return insertData(indexName,null,data,id);
    }

    public IndexResponse insertData(String indexName,String type,Object data) {
        return insertData(indexName,type,data,null);
    }

    /**
     * 获得索引下某个类型的某个字段
     *
     * @param indexName
     * @param type
     * @param field
     * @return
     * @throws IOException
     */
    public Map<String, Object> getField(String indexName, String type, String field) throws IOException {
        GetFieldMappingsRequest request = new GetFieldMappingsRequest();
        request.indices(indexName);
        request.types(type);
        request.fields(field);

        GetFieldMappingsResponse response = client.indices().getFieldMapping(request, RequestOptions.DEFAULT);
        Map<String, Map<String, Map<String, GetFieldMappingsResponse.FieldMappingMetaData>>> mappings = response.mappings();
        final Map<String, GetFieldMappingsResponse.FieldMappingMetaData> typeMappings = mappings.get(indexName).get(type);
        final GetFieldMappingsResponse.FieldMappingMetaData metaData = typeMappings.get(field);
        final String fullName = metaData.fullName();
        final Map<String, Object> source = metaData.sourceAsMap();
        return source;
    }

    /**
     * 获取某个文档
     */
    public SearchResponse matchSearch(String indexName,String type,String fieldName,Object fieldValue) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery(fieldName,fieldValue));
        request.source(builder);
        SearchResponse response =  client.search(request,RequestOptions.DEFAULT);
        return response;
    }

    /**
     *
     * @param indexName     索引名
     * @param type          索引类型
     * @param queryParm     查询参数（有字段类型与字段值组成）
     * @return
     */
    public SearchResponse boolMulitSearchForShould(String indexName,String type,Map<String,Object> queryParm) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String,Object> query : queryParm.entrySet()){
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(query.getKey(), query.getValue());
            boolQueryBuilder.should(matchQueryBuilder);
        }
        builder.query(boolQueryBuilder);
        request.source(builder);
        SearchResponse response =  client.search(request,RequestOptions.DEFAULT);
        return response;
    }

    public SearchResponse preciseSearch(String indexName,String type,String fieldName,Object fieldValue) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery(fieldName, fieldValue));
        request.source(builder);
        SearchResponse response =  client.search(request,RequestOptions.DEFAULT);
        return response;
    }














        public static class ESClientBuilder {
        /**
         * 以给定的节点初始化客户端
         *
         * @param httpHosts
         * @return
         */
        public static ESClient init(HttpHost... httpHosts) {
            ESClient client = new ESClient();
            client.client = new RestHighLevelClient(RestClient.builder(httpHosts));
            return client;
        }
    }
}
