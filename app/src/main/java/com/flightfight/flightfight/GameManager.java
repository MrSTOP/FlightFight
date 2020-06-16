package com.flightfight.flightfight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.flightfight.flightfight.yankunwei.GameBulletFactory;
import com.flightfight.flightfight.yankunwei.GamePlayerSprite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameManager {
    private int ScreenWidth;
    private int ScreenHeight;
    private Context context;
    private Random rand;
    private GameSprite[] bubbles;
    private GamePlayerSprite happyFish;
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
        happyFish.setAngelArc(angle);
    }

    public void setPlayerActive(boolean active) {
        if (happyFish.isActive() != active) {
            happyFish.setActive(active);
        }
    }

    public void setPlayerDestination(float x, float y) {
        happyFish.serDestination(x, y);
    }

    public void setPlayerFlip(boolean flip) {
        happyFish.setFlip(flip);
    }

    public void updateAnimation() {
        happyFish.loopFrame();
    }

    private void initHappyFish() {
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1);
        Bitmap left = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_left);
        Bitmap right = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_right);
        happyFish = new GamePlayerSprite(context, source, left, right, 12);
        happyFish.setSpeed(20 * density);
        happyFish.setActive(false);
        happyFish.setRatio(0.5f * density);
        happyFish.setAngelArc(0);
        float px = (ScreenWidth - happyFish.getWidth()) / 2;
        float py = (ScreenHeight - happyFish.getHeight()) / 2;
        happyFish.setX(px);
        happyFish.setY(py);
    }

    public RectF getPlayerRectF() {
        return happyFish.getBoundRectF();
    }

    public void draw(Canvas canvas) {
        destRect.left = 0;
        destRect.right = canvas.getWidth();
        destRect.top = 0;
        destRect.bottom = canvas.getHeight();
        paint.setDither(true);
        canvas.drawBitmap(backBmp, srcRect, destRect, paint);
        happyFish.draw(canvas);
    }

    public void updateHappyFish() {
        happyFish.move();
    }
}
