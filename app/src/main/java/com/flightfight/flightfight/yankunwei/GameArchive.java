package com.flightfight.flightfight.yankunwei;

import com.flightfight.flightfight.GameSprite;

import java.util.Date;
import java.util.List;

public class GameArchive {
    private Date gameDate;
    private List<GameSprite> enemyList;
    private GamePlayerSprite player;
    private List<GameSprite> enemyBulletList;

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

    public List<GameSprite> getEnemyBulletList() {
        return enemyBulletList;
    }

    public void setEnemyBulletList(List<GameSprite> enemyBulletList) {
        this.enemyBulletList = enemyBulletList;
    }

    public GamePlayerSprite getPlayer() {
        return player;
    }

    public void setPlayer(GamePlayerSprite player) {
        this.player = player;
    }
}
