package com.heng.bean;

import java.util.ArrayList;
import java.util.List;

public class Boy {

    private String name = "wuheng";

    private List<YaGao> yaGaos = new ArrayList<>();

    public List<YaGao> getYaGaos() {
        return yaGaos;
    }

    public void setYaGaos(List<YaGao> yaGaos) {
        this.yaGaos = yaGaos;
    }
}
