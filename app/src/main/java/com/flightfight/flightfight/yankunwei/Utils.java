package com.flightfight.flightfight.yankunwei;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import com.flightfight.flightfight.GameSprite;
import com.flightfight.flightfight.R;
import com.flightfight.flightfight.ZhuJintao.GameNpc;
import com.flightfight.flightfight.yankunwei.database.bean.PlayerRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    public static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static final int GAME_ACHIEVE_ENEMY = 1;
    public static final int GAME_ACHIEVE_ENEMY_BULLET = 2;
    public static final int GAME_ACHIEVE_PLAYER_BULLET = 3;

    public static double calculate2PointAngleArc(float x1, float y1, float x2, float y2) {
        double horizontalX = x2 + 1;
        double vector = (x1 - x2) * (horizontalX - x2) + (y1 - y2) * (y2 - y2);
        double lengthPointerVector = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double lengthHorizontalVector = horizontalX - x2;
        double angle = Math.acos(vector / (lengthPointerVector * lengthHorizontalVector));
        if (y1 < y2) {
            angle = 2 * Math.PI - angle;
        }
//        System.out.println("ANGLE: " + angle * 180 / Math.PI);
        return angle;
    }

    public static boolean rectCollide(RectF rect1, RectF rect2) {
        return rect1.intersects(rect2.left, rect2.top, rect2.right, rect2.bottom);
    }

    public static boolean collideWithPlayer(RectF[] playerBounds, RectF target) {
        for (RectF bound : playerBounds) {
            if (rectCollide(bound, target)) {
                return true;
            }
        }
        return false;
    }

    public static void setRectF(RectF rectF, float left, float top, float width, float height) {
        rectF.left = left;
        rectF.top = top;
        rectF.right = rectF.left + width;
        rectF.bottom = rectF.top + height;
    }

    private static void initGameSprite(Context context, List<GameSprite> dest, List<GameSprite> src, int type) {
        GameSprite newGameSprite = null;
        for (GameSprite gameSprite : src) {
            switch (type) {
                case GAME_ACHIEVE_ENEMY:
                    newGameSprite = new GameNpc(context, BitmapFactory.decodeResource(context.getResources(), R.mipmap.enemy1_1));
                    newGameSprite.setActive(true);
                    break;
                case GAME_ACHIEVE_ENEMY_BULLET:
                    newGameSprite = new GameSprite(context, BitmapFactory.decodeResource(context.getResources(), R.mipmap.bullet3), 2, 2);
                    break;
                case GAME_ACHIEVE_PLAYER_BULLET:
                    newGameSprite = new GameSprite(context, BitmapFactory.decodeResource(context.getResources(), R.mipmap.bullet1), 2, 2);
                    newGameSprite.setActive(true);
                    break;
                default:
                    throw new IllegalArgumentException("Game sprite type [" + type + "] not exist");
            }
            newGameSprite.initBySaved(gameSprite);
            dest.add(newGameSprite);
        }
    }

    private static void castNpc(List<GameNpc> dest, List<GameSprite> src) {
        for (GameSprite gameSprite : src) {
            dest.add((GameNpc) gameSprite);
        }
    }

    public static List<Date> parseAllGameAchieveDate() {
        return GSON.fromJson(ValueContainer.SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE_ARG_DATA, new TypeToken<List<Date>>(){}.getType());
    }

    public static List<PlayerRecord> parsePlayerRecord() {
        return GSON.fromJson(ValueContainer.SERVICE_RESPONSE_LOAD_ALL_GAME_RECORD_ARG_DATA, new TypeToken<List<PlayerRecord>>(){}.getType());
    }

    public static GameArchive parseGameAchieve(Context context) {
        GameArchive gameAchieve = GSON.fromJson(ValueContainer.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG_DATA, GameArchive.class);
        List<GameSprite> enemyList = new ArrayList<>(gameAchieve.getEnemyList());
        List<GameSprite> initializedEnemyList = new ArrayList<>();
        List<GameSprite> initializedEnemyBulletList = new ArrayList<>();
        List<GameSprite> initializedPlayerBulletList = new ArrayList<>();
        List<GameNpc> initializedNpcList = new ArrayList<>();
        GamePlayerSprite player = gameAchieve.getPlayer();
        GamePlayerSprite newPlayer = new GamePlayerSprite(context,
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1),
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_left),
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_right), 12);
        initGameSprite(context, initializedPlayerBulletList, gameAchieve.getPlayer().getPlayerBulletListSafeForIteration(), Utils.GAME_ACHIEVE_PLAYER_BULLET);
        initGameSprite(context, initializedEnemyList, enemyList, Utils.GAME_ACHIEVE_ENEMY);
        initGameSprite(context, initializedEnemyBulletList, gameAchieve.getEnemyBulletList(), Utils.GAME_ACHIEVE_ENEMY_BULLET);
        castNpc(initializedNpcList, initializedEnemyList);
        newPlayer.setPlayerBulletList(initializedPlayerBulletList);
        newPlayer.initBySaved(player);
        gameAchieve.setPlayer(newPlayer);
        gameAchieve.setEnemyList(initializedNpcList);
        gameAchieve.setEnemyBulletList(initializedEnemyBulletList);
        return gameAchieve;
    }
}
