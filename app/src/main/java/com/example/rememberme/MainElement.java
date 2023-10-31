package com.example.rememberme;

import java.io.Serializable;

public class MainElement implements Serializable, Comparable<MainElement> {

    String image_url;
    String name;
    String description;
    String type;
    String time;
    boolean important_to_watch;
    boolean done;
    String future_release_date;
    long element_date;

    public MainElement() {}

    public MainElement(String image_url, String name, String description, String type, String time, boolean important_to_watch, boolean done, String future_release_date, long element_date) {
        this.image_url = image_url;
        this.name = name;
        this.description = description;
        this.type = type;
        this.time = time;
        this.important_to_watch = important_to_watch;
        this.done = done;
        this.future_release_date = future_release_date;
        this.element_date = element_date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isImportant_to_watch() {
        return important_to_watch;
    }

    public void setImportant_to_watch(boolean important_to_watch) {
        this.important_to_watch = important_to_watch;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getFuture_release_date() {
        return future_release_date;
    }

    public void setFuture_release_date(String future_release_date) {
        this.future_release_date = future_release_date;
    }

    public long getElement_date() {
        return element_date;
    }

    public void setElement_date(long element_date) {
        this.element_date = element_date;
    }

    @Override
    public int compareTo(MainElement MainElement) {
        return this.getName().compareTo(MainElement.getName());
    }
}
