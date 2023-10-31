package com.example.rememberme;

import java.io.Serializable;

public class NotificationElement implements Serializable, Comparable<NotificationElement> {

    long date;
    String name, desc, image_url;

    public NotificationElement() { }

    public NotificationElement(long date, String name, String desc, String image_url) {
        this.date = date;
        this.name = name;
        this.desc = desc;
        this.image_url = image_url;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "NotificationElement{" +
                "date=" + date +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }

    @Override
    public int compareTo(NotificationElement notificationElement) {
        return this.getName().compareTo(notificationElement.getName());
    }
}
