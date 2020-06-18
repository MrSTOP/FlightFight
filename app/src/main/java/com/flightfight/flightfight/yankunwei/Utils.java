package com.flightfight.flightfight.yankunwei;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import com.flightfight.flightfight.GameSprite;
import com.flightfight.flightfight.R;
import com.flightfight.flightfight.ZhuJintao.GameNpc;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

public class Utils {


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

    public static void initGameSprite(Context context, List<GameSprite> dest, List<GameSprite> src, int type) {
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

    public static void castNpc(List<GameNpc> dest, List<GameSprite> src) {
        for (GameSprite gameSprite : src) {
            dest.add((GameNpc) gameSprite);
        }
    }

    public static List<Date> parsePlayerRecord() {
        Gson gson = new Gson();
        return gson.fromJson(ValueContainer.SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE_ARG_DATA, new TypeToken<List<Date>>(){}.getType());
    }
}
