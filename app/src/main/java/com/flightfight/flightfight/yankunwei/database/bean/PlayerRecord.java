package com.flightfight.flightfight.yankunwei.database.bean;

public class PlayerRecord {

    private int id;
    private long time;
    private int score;
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
