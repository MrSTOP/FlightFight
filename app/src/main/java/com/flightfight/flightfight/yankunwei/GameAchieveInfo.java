package com.flightfight.flightfight.yankunwei;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class GameAchieveInfo {
    @Expose
    public final String uuid;
    @Expose
    public final Date date;
    @Expose
    public final int level;

    public GameAchieveInfo(String uuid, Date date, int level) {
        this.uuid = uuid;
        this.date = date;
        this.level = level;
    }

    public GameAchieveInfo(String uuid, long time,int level) {
        this(uuid, new Date(time), level);
    }
}
