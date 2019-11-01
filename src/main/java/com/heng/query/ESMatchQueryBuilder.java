package com.heng.query;

import com.heng.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class ESMatchQueryBuilder {

    private Map<String,Object> match = new HashMap<>();

    public void addField(String key,Object value){
        Map<String,Object> kv = new HashMap<>();
        kv.put(key, value);
        match.put(ESQueryType.MATCH, kv);
    }

    public Map<String,Object> getMatch(){
        return match;
    }

    public String getJson(){
        return Utils.objcetToJson(match);
    }
}
