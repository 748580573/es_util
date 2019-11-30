package com.heng.util;

import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface ESClient {
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
    public abstract RestStatus update(String indexName,String type,String id, Map<String,Object> fields);


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

    public abstract List<String> getDataByShouldQuery(String index,String type,Map<String, Object> search);

    public abstract boolean existIndex(String index);

    public abstract boolean existType(String index,String type);

    public long getLastUsedTime();

    public void setLastUsedTime(long lastUsedTime);

    public long getCreateTime() ;

    public void setCreateTime(long createTime);
}
