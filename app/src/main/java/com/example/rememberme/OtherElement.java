package com.example.rememberme;

import java.io.Serializable;
import java.util.ArrayList;

public class OtherElement implements Serializable, Comparable<OtherElement> {

    String tags, name, desc;

    public OtherElement() { }

    public OtherElement(String tags, String name, String desc) {
        this.tags = tags;
        this.name = name;
        this.desc = desc;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int compareTo(OtherElement otherElement) {
        return this.getName().compareTo(otherElement.getName());
    }

    @Override
    public String toString() {
        return "OtherElement{" +
                "tags='" + tags + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
