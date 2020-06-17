package com.flightfight.flightfight.yankunwei;

import com.flightfight.flightfight.GameSprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameArchive implements Serializable {
    private Date gameDate;
    private ArrayList<GameSprite> enemyList;
    private GamePlayerSprite player;
    private ArrayList<GameSprite> enemyBulletList;

    public GameArchive() {
        this.enemyList = new ArrayList<>();
        this.enemyBulletList = new ArrayList<>();
    }

    public Date getGameDate() {
        return gameDate;
    }

    public void setGameDate(Date gameDate) {
        this.gameDate = gameDate;
    }

    public List<GameSprite> getEnemyList() {
        return enemyList;
    }

    public void setEnemyList(List<GameSprite> enemyList) {
        this.enemyList.clear();
        this.enemyList.addAll(enemyList);
    }

    public List<GameSprite> getEnemyBulletList() {
        return enemyBulletList;
    }

    public void setEnemyBulletList(List<GameSprite> enemyBulletList) {
        this.enemyBulletList.clear();
        this.enemyBulletList.addAll(enemyBulletList);
    }

    public GamePlayerSprite getPlayer() {
        return player;
    }

    public void setPlayer(GamePlayerSprite player) {
        this.player = player;
    }
}
