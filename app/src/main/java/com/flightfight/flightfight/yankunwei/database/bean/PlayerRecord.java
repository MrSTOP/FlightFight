package com.flightfight.flightfight.yankunwei.database.bean;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class PlayerRecord implements Serializable {

    @Expose
    private int id;
    @Expose
    private long time;
    @Expose
    private int score;
    @Expose
    private String playerName;

    public PlayerRecord() {

    }

    public PlayerRecord(long time, int score, String playerName) {
        this.time = time;
        this.score = score;
        this.playerName = playerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Date getDate() {
        return new Date(this.time);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
