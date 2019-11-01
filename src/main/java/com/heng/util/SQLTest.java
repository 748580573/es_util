package com.heng.util;

import com.heng.query.ESQueryBuilder;


public class SQLTest {

    public static void main(String[] args) {
        ESQueryBuilder builder = new ESQueryBuilder();
        builder.bool().should().addField("blogTilte", "123");

        System.out.println(builder.toString());
    }
}
