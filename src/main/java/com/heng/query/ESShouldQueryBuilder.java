package com.heng.query;

import com.heng.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class ESShouldQueryBuilder {

    private Map<String,Object> should = new HashMap<>();

    public void addField(String key,Object value){
        Map<String,Object> kv = new HashMap<>();
        kv.put(key, value);
        should.put(ESQueryType.MATCH, kv);
    }

    public Map<String,Object> getShould(){
        return should;
    }

    public String getJson(){
        return Utils.objcetToJson(should);
    }
}
