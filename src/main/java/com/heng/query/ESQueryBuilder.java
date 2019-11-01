package com.heng.query;

import com.heng.util.Utils;
import java.util.HashMap;
import java.util.Map;

public class ESQueryBuilder {

    private Map<String,Object> query = new HashMap<>();

    private ESBoolQueryBuilder boolQuery = null;

    public String toString(){
        return Utils.objcetToJson(query);
    }

    public ESBoolQueryBuilder bool(){
        if (boolQuery == null){
            boolQuery = new ESBoolQueryBuilder();
        }
        query.put(ESQueryType.BOOL, boolQuery.bool);
        return boolQuery;
    }

}
