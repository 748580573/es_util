package com.heng.util;

import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class ESClientAbstract {

    /**
     * 关闭客户端
     *
     * @throws Exception
     */
    public abstract void close() throws Exception;

    /**
     * 以默认配置创建创建索引
     *
     * @param indexName，索引名
     * @return 返回结果，包含响应码和响应内容
     * @throws IOException
     */
    public abstract RestStatus createIndex(String indexName, String type,Object object);

    /**
     * 插入数据
     * @param indexName
     * @param type
     * @param data
     * @return
     */
    public abstract RestStatus  insertData(String indexName,String type,Object data);

    /**
     * 更新文档
     * @param indexName
     * @param type
     * @param id
     * @param fields
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public abstract RestStatus update(String indexName,String type,String id, Map<String,Object> fields) throws ExecutionException, InterruptedException;


    public abstract RestStatus delete(String indexName,String type,String id);
    /**
     * 判断是否存在该字段
     * @param index
     * @param type
     * @param fieldName
     * @return
     */
    public abstract boolean existField(String index,String type,String fieldName);

    /**
     * 获取数据
     * @param index
     * @param type
     */
    public abstract Map<String,Object> getDataById(String index, String type, String id);


    /**
     * select * from table
     * 这里table表示es中的类型
     * 暂不支持复杂查询sql
     * @这里暂未实现
     * @param index 索引
     * @param sql
     * @return
     */
    private Map<String,Object> getDataBySql(String index, String sql){return null;};
    /**
     * 是否存在该
     * @param index
     * @return
     */
    public abstract boolean existIndex(String index);

    public abstract boolean existType(String index,String type);

}
