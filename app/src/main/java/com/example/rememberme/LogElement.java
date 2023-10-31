package com.example.rememberme;

import java.io.Serializable;

public class LogElement implements Serializable, Comparable<LogElement> {

    long date;
    String actor, actions;

    public LogElement() { }

    public LogElement(long date, String actor, String actions) {
        this.date = date;
        this.actor = actor;
        this.actions = actions;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    @Override
    public int compareTo(LogElement logElement) {
        return Long.compare(this.getDate(), logElement.getDate());
    }

    @Override
    public String toString() {
        return "ACTOR1: " + actor + " | " + "ACTIONS: " + actions.split("\\|");
    }
}
