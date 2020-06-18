package com.flightfight.flightfight.yankunwei;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class GameAchieveInfo {
    @Expose
    public final String uuid;
    @Expose
    public final Date date;

    public GameAchieveInfo(String uuid, Date date) {
        this.uuid = uuid;
        this.date = date;
    }

    public GameAchieveInfo(String uuid, long time) {
        this(uuid, new Date(time));
    }
}
