package com.flightfight.flightfight;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.flightfight.flightfight.yankunwei.GameArchive;
import com.flightfight.flightfight.yankunwei.GameBulletFactory;
import com.flightfight.flightfight.yankunwei.GamePlayerSprite;
import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yankunwei.Utils;
import com.flightfight.flightfight.yankunwei.ValueContainer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GameManager {
    private int ScreenWidth;
    private int ScreenHeight;
    private Context context;
    private Random rand;
    private GameSprite[] bubbles;
    private GamePlayerSprite player;
    private Bitmap backBmp;
    private long bubbleStartTime;
    private float density;

    Rect srcRect;
    Rect destRect;
    Paint paint;

    public GameManager(Context context, int ScreenWidth, int ScreenHeight) {
        this.context = context;
        this.ScreenWidth = ScreenWidth;
        this.ScreenHeight = ScreenHeight;
        density = context.getResources().getDisplayMetrics().density;
        backBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.background);
        this.srcRect = new Rect(0, 0, backBmp.getWidth(), backBmp.getHeight());
        this.destRect = new Rect();
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rand = new Random(System.currentTimeMillis());
        GameBulletFactory.getInstance().initFactory(context, density);
        initHappyFish();
    }

    public void setPlayerAngelArc(double angle) {
        player.setAngelArc(angle);
    }

    public void setPlayerActive(boolean active) {
            player.setActive(active);
    }

    public void setPlayerDestination(float x, float y) {
        player.serDestination(x, y);
    }

    public void setPlayerFlip(boolean flip) {
        player.setFlip(flip);
    }

    public void updateAnimation() {
        player.loopFrame();
    }

    private void initHappyFish() {
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1);
        Bitmap left = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_left);
        Bitmap right = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_right);
        player = new GamePlayerSprite(context, source, left, right, 12);
        player.setSpeed(10 * density);
        player.setActive(false);
        player.setRatio(0.5f * density);
        player.setAngelArc(0);
        player.setScreenSize(ScreenWidth, ScreenHeight);
        float px = (ScreenWidth - player.getWidth()) / 2;
        float py = (ScreenHeight - player.getHeight()) / 2;
        player.setX(px);
        player.setY(py);
    }

    public RectF getPlayerRectF() {
        return player.getBoundRectF();
    }

    public void draw(Canvas canvas) {
        destRect.left = 0;
        destRect.right = canvas.getWidth();
        destRect.top = 0;
        destRect.bottom = canvas.getHeight();
        paint.setDither(true);
        canvas.drawBitmap(backBmp, srcRect, destRect, paint);
        player.draw(canvas);
    }

    public void updateHappyFish() {
        player.move();
    }

    public void load() {
        Intent load = new Intent(context, GameSaveService.class);
        load.setAction(GameSaveService.SERVICE_ACTION_LOAD_GAME_ACHIEVE);
        load.putExtra(GameSaveService.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG, 0L);
        context.startService(load);
    }

    public void setAchieveData(GameControl controller) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        GameArchive gameAchieve = gson.fromJson(ValueContainer.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG_DATA, GameArchive.class);
        List<GameSprite> enemyList = gameAchieve.getEnemyList();
        List<GameSprite> initializedEnemyList = new ArrayList<>();
        List<GameSprite> initializedPlayerBulletList = new ArrayList<>();
        GamePlayerSprite player = gameAchieve.getPlayer();
        GamePlayerSprite newPlayer = new GamePlayerSprite(context,
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1),
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_left),
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_right), 12);
        Utils.initGameSprite(context, initializedPlayerBulletList, gameAchieve.getPlayer().getPlayerBulletListSafeForIteration(), Utils.GAME_ACHIEVE_PLAYER_BULLET);
        newPlayer.setPlayerBulletList(initializedPlayerBulletList);
        newPlayer.initBySaved(player);
        gameAchieve.setPlayer(player);
        controller.setPlayerRect(newPlayer.getBoundRectF());
        this.player = newPlayer;
    }

    public void save() {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            GameArchive gameArchive = new GameArchive();
            gameArchive.setGameDate(new Date(0));
            gameArchive.setPlayer(player);
            String str = gson.toJson(gameArchive);
            Intent save = new Intent(context, GameSaveService.class);
            save.setAction(GameSaveService.SERVICE_ACTION_SAVE_GAME_ACHIEVE);
            ValueContainer.SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_DATA = str;
            save.putExtra(GameSaveService.SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_TIME, gameArchive.getGameDate().getTime());
            context.startService(save);
            System.out.println("BROAD");
    }
}
