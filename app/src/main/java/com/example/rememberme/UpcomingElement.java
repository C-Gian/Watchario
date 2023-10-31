package com.example.rememberme;

public class UpcomingElement {

    String image_url, name, future_date;
    long days_left;

    public UpcomingElement() { }

    public UpcomingElement(String image_url, String name, String future_date) {
        this.image_url = image_url;
        this.name = name;
        this.future_date = future_date;
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

    public String getFuture_date() {
        return future_date;
    }

    public void setFuture_date(String future_date) {
        this.future_date = future_date;
    }

    public long getDays_left() {
        return days_left;
    }

    public void setDays_left(long days_left) {
        this.days_left = days_left;
    }

    @Override
    public String toString() {
        return "UpcomingElement{" +
                "image_url='" + image_url + '\'' +
                ", name='" + name + '\'' +
                ", future_date='" + future_date + '\'' +
                ", days_left='" + days_left + '\'' +
                '}';
    }
}
