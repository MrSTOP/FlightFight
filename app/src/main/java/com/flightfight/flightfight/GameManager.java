package com.flightfight.flightfight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

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
    private List<GameSprite> bubbleList = null;
    private List<GameSprite> clonebubblelist = null;
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
        initHappyFish();
    }

    public void setPlayerAngelArc(double angle) {
        happyFish.setAngelArc(angle);
    }

    public void setPlayerActive(boolean active) {
        happyFish.setActive(active);
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
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_test);
        happyFish = new GamePlayerSprite(context, source, 5, 5);
        happyFish.setSpeed(10 * density);
        happyFish.setActive(false);
        happyFish.setRatio(0.1f * density);
        happyFish.setAngelArc(0);
        float px = (ScreenWidth - happyFish.getWidth()) / 2;
        float py = (ScreenHeight - happyFish.getHeight()) / 2;
        happyFish.setX(px);
        happyFish.setY(py);
    }

    public RectF getPlayerRectF() {
        return happyFish.getBoundRectF();
    }

    public void loadBubbles() {
        if (bubbleList == null) {
            bubbleList = new ArrayList<>();
            bubbleStartTime = System.currentTimeMillis();
        }
        long bubbleLoadTime = System.currentTimeMillis();
        if (bubbleLoadTime - bubbleStartTime > 150) {
            Bitmap bmp = null;//BitmapFactory.decodeResource(context.getResources(), R.mipmap.bubble);
            GameSprite bubble = new GameSprite(context, bmp);
            bubble.setSpeed(20 * density);
            bubble.setDir(GameSprite.UP);
            bubble.setActive(true);
            float[] ratio = {0.35f * density, 0.3f * density, 0.25f * density, 0.2f * density, 0.15f * density};
            int r = rand.nextInt(5);
            bubble.setRatio(ratio[r]);
            float x, y;

            if (happyFish.isFlip()) {
                x = happyFish.getX() + (happyFish.getWidth() - bubble.getWidth()) / 2;
                y = happyFish.getY() + (happyFish.getHeight() - bubble.getHeight()) / 2;
            } else {
                x = happyFish.getX() + (happyFish.getWidth() - bubble.getWidth()) / 2;
                y = happyFish.getY() + (happyFish.getHeight() - bubble.getHeight()) / 2;
            }
            bubble.setX(x);
            bubble.setY(y);
            Log.d("BUBBLE", "FISH X:" + happyFish.getX() + " Y:" + happyFish.getY() + "FISH W:" + happyFish.getWidth() + " H:" + happyFish.getHeight());
            Log.d("BUBBLE", "BUBB X:" + bubble.getX() + " Y:" + bubble.getY() + "BUBB W:" + bubble.getWidth() + " H:" + bubble.getHeight());
            bubbleList.add(bubble);
            bubbleStartTime = System.currentTimeMillis();
        }
    }

    public void updateBubblePos() {
        if (bubbleList != null) {
            List<GameSprite> cloneBubbles = new ArrayList<>(bubbleList);
            for (GameSprite bubble : cloneBubbles) {
                bubble.move();
            }
            cloneBubbles.clear();
        }
    }

    public void clearBubbles() {
        if (bubbleList != null) {
            List<GameSprite> cloneBubbles = new ArrayList<>(bubbleList);
            Iterator<GameSprite> it = cloneBubbles.iterator();
            while (it.hasNext()) {
                GameSprite bubble = it.next();
                if (bubble.getY() < 0 - bubble.getHeight()) {
                    bubble.releaseBitmap();
                    it.remove();
                }
            }
            bubbleList.clear();
            bubbleList.addAll(cloneBubbles);
            cloneBubbles.clear();
        }
    }

    public void draw(Canvas canvas) {
        destRect.left = 0;
        destRect.right = canvas.getWidth();
        destRect.top = 0;
        destRect.bottom = canvas.getHeight();
        paint.setDither(true);
        canvas.drawBitmap(backBmp, srcRect, destRect, paint);
        happyFish.draw(canvas);
        if (bubbleList != null) {
            if (clonebubblelist == null) {
                clonebubblelist = new ArrayList<>();
            }
            clonebubblelist.addAll(bubbleList);
            for (GameSprite bubble : clonebubblelist) {
                bubble.setAlpha(100);
                bubble.draw(canvas);
            }
            clonebubblelist.clear();
        }
    }

    public void updateHappyFish() {
//        int pWidth = (int) happyFish.getWidth();
//        int pHeight = (int) happyFish.getHeight();
//        int pDir = happyFish.getDir();
        happyFish.move();
    }
}
