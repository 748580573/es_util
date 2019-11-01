package com.heng.bean;

import java.io.Serializable;

public class FieldObject<T> implements Serializable {

    private static final long serialVersionUID = -1233211234567L;

    private String key;

    private T value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
