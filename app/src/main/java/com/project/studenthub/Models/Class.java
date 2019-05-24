package com.project.studenthub.Models;

import java.util.Map;

public class Class {

    public Class (){

    }

    private Map.Entry<String, String> classInfo;
    int count;
    public Map.Entry<String, String> getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(Map.Entry<String, String> classInfo) {
        this.classInfo = classInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
