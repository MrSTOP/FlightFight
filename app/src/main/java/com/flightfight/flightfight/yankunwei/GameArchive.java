package com.flightfight.flightfight.yankunwei;

import com.flightfight.flightfight.GameSprite;

import java.util.Date;
import java.util.List;

public class GameArchive {
    private Date gameDate;
    private List<GameSprite> enemyList;
    private GameSprite player;
    private List<GameSprite> enemyBulletList;
    private List<GameSprite> playerBulletList;

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
        this.enemyList = enemyList;
    }

    public GameSprite getPlayer() {
        return player;
    }

    public void setPlayer(GameSprite player) {
        this.player = player;
    }

    public List<GameSprite> getEnemyBulletList() {
        return enemyBulletList;
    }

    public void setEnemyBulletList(List<GameSprite> enemyBulletList) {
        this.enemyBulletList = enemyBulletList;
    }

    public List<GameSprite> getPlayerBulletList() {
        return playerBulletList;
    }

    public void setPlayerBulletList(List<GameSprite> playerBulletList) {
        this.playerBulletList = playerBulletList;
    }
}
