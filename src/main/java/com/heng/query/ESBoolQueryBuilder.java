package com.heng.query;

import com.heng.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ESBoolQueryBuilder {

    Map<String, List<Map<String,Object>>> bool = new HashMap<>();

    private ESMustQueryBuilder must;

    private ESShouldQueryBuilder should;

    public ESMustQueryBuilder must(){
        if (must == null){
            must = new ESMustQueryBuilder();
        }
        return must;
    }

    public ESShouldQueryBuilder should(){
        if (should == null){
            should = new ESShouldQueryBuilder();
        }
        return should;
    }

    public String getJson(){
        return Utils.objcetToJson(this.bool);
    }

    public class ESMustQueryBuilder{
        private List<Map<String,Object>> mustQuery;

        public ESMustQueryBuilder addField(String key,Object value){
            mustQuery = ESBoolQueryBuilder.this.bool.get(ESQueryType.MUST);
            if (mustQuery == null){
                mustQuery = new ArrayList<>();
            }
            ESBoolQueryBuilder.this.bool.put(ESQueryType.MUST, mustQuery);
            ESMatchQueryBuilder builder = new ESMatchQueryBuilder();
            builder.addField(key, value);
            mustQuery.add(builder.getMatch());
            return this;
        }

        public ESBoolQueryBuilder builder(){
            return ESBoolQueryBuilder.this;
        }
    }

    public class ESShouldQueryBuilder{
        private List<Map<String,Object>> shouldQuery;

        public ESShouldQueryBuilder addField(String key,Object value){
            shouldQuery = ESBoolQueryBuilder.this.bool.get(ESQueryType.SHOULD);
            if (shouldQuery == null){
                shouldQuery = new ArrayList<>();
            }
            ESBoolQueryBuilder.this.bool.put(ESQueryType.SHOULD, shouldQuery);
            ESMatchQueryBuilder builder = new ESMatchQueryBuilder();
            builder.addField(key, value);
            shouldQuery.add(builder.getMatch());
            return this;
        }

        public ESBoolQueryBuilder builder(){
            return ESBoolQueryBuilder.this;
        }
    }
}
