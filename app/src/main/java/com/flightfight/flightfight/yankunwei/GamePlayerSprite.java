package com.flightfight.flightfight.yankunwei;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import com.flightfight.flightfight.GameSprite;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePlayerSprite extends GameSprite {

    public static final int HALF_DESTINATION_AREA_LENGTH = 20;
    public static final int SHOOT_COOL_TICK = 5;

    private double angelArc;
    private float destinationX;
    private float destinationY;
    private RectF destinationArea = new RectF();
    //    private boolean banking = false;
//    private boolean needSetBanking = true;
    private Bitmap[] leftBank;
    private Bitmap[] rightBank;
    private Bitmap[] normalBitmap;
    private int totalBankFrame;
    @Expose
    private List<GameSprite> playerBulletList;


    /**
     * 发射子弹冷却时间
     */
    @Expose
    private int coolTime;
    @Expose
    private boolean canShoot;
    @Expose
    private int screenWidth;
    @Expose
    private int screenHeight;


    public GamePlayerSprite(Context context, Bitmap bitmap, Bitmap leftBankBitmap, Bitmap rightBankBitmap, int bankFrame) {
        super(context, bitmap);
        this.leftBank = new Bitmap[bankFrame];
        this.rightBank = new Bitmap[bankFrame];
        this.normalBitmap = new Bitmap[]{bitmap};
        int frameWidth = leftBankBitmap.getWidth() / bankFrame;
        int frameHeight = leftBankBitmap.getHeight();
        for (int i = 0; i < bankFrame; i++) {
            this.leftBank[i] = Bitmap.createBitmap(leftBankBitmap, frameWidth * i, 0, frameWidth, frameHeight);
        }
        frameWidth = rightBankBitmap.getWidth() / bankFrame;
        frameHeight = rightBankBitmap.getHeight();
        for (int i = 0; i < bankFrame; i++) {
            this.rightBank[i] = Bitmap.createBitmap(rightBankBitmap, frameWidth * i, 0, frameWidth, frameHeight);
        }
        totalBankFrame = bankFrame;
        this.spriteBitmaps = new Bitmap[1];
        this.currentFrame = 0;
        this.totalFrames = 1;
        this.spriteBitmaps = normalBitmap;
        playerBulletList = new ArrayList<>();
        this.canShoot = true;
        this.hp = 100;
        this.life = 3;
    }

    @Deprecated
    public GamePlayerSprite(Context context, Bitmap rowBitmap, int totalFrames, int rowFrames) {
        super(context, rowBitmap, totalFrames, rowFrames);
        throw new UnsupportedOperationException("Do not use this constructor");
    }

    public void setScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void move() {
        if (this.active) {
            if (!arriveDestination()) {

                this.setX(this.x + getXSpeed());
                this.setY(this.y + getYSpeed());
                float centerX = this.boundRect.centerX();
                float centerY = this.boundRect.centerY();
                this.destinationArea.top = centerY - HALF_DESTINATION_AREA_LENGTH;
                this.destinationArea.bottom = centerY + HALF_DESTINATION_AREA_LENGTH;
                this.destinationArea.left = centerX - HALF_DESTINATION_AREA_LENGTH;
                this.destinationArea.right = centerX + HALF_DESTINATION_AREA_LENGTH;
//            Log.d("PLAYER_MOVE", "SX: " + getXSpeed() + "  SY: " + getYSpeed() + "  Angle:" + (angelArc * 180 / Math.PI));
//            angelArc = Utils.calculate2PointAngleArc(destinationX, destinationY, centerX, centerY);
//            Log.d("PLAYER_MOVE", "PX: " + this.destinationArea.centerX() + "  PY: " + this.destinationArea.centerY() + "  DX: " + destinationX + "  DY: " + destinationY);
//            Log.d("PLAYER_MOVE", "PLX: " + this.x + "  PLY: " + this.y);
            } else {
                spriteBitmaps = normalBitmap;
                totalFrames = 1;
                currentFrame = 0;
                active = false;
            }
        }
        if (x <= 0/* - getWidth()*/) {
            setX(0/* - getWidth()*/);
        }
        if (x >= screenWidth - getWidth()) {
            setX(screenWidth - getWidth());
        }
        if (y <= 0/* - getHeight()*/) {
            setY(0/* - getHeight()*/);
        }
        if (y >= screenHeight - getHeight()) {
            setY(screenHeight - getHeight());
        }
        coolTime--;
        if (coolTime <= 0) {
            canShoot = true;
        }
        if (canShoot) {
            shoot();
            canShoot = false;
            coolTime = SHOOT_COOL_TICK;
        }
        if (playerBulletList.size() > 30) {
            removeInvalidBullet();
        }
        for (GameSprite bullet : getPlayerBulletListSafeForIteration()) {
            bullet.move();
        }
//        Log.d("PLAYER", "PX: " + x + " SW: " + screenWidth + " SW1: " + (screenWidth - getWidth() / 4) + " PY: " + y + " SH: " + screenHeight + " SH1: " + (screenHeight - getHeight() / 4));
    }

    private void shoot() {
        playerBulletList.add(GameBulletFactory.getInstance().getPlayerBullet(GameBulletFactory.BULLET_PLAYER, this.boundRect));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (GameSprite bullet : getPlayerBulletListSafeForIteration()) {
            bullet.draw(canvas);
        }
    }

    private boolean arriveDestination() {
        return destinationArea.contains(this.destinationX, this.destinationY);
    }

    private float getXSpeed() {
        return (float) (Math.cos(angelArc) * speed);
    }

    private float getYSpeed() {
        return (float) (Math.sin(angelArc) * speed);
    }

    @Override
    public void setDir(int dir) {
        throw new UnsupportedOperationException("Please use setAngel()");
    }

    @Override
    public int getDir() {
        throw new UnsupportedOperationException("Please use getAngel()");
    }

    @Override
    public void loopFrame() {
        if (totalFrames > 1) {
            currentFrame = currentFrame + 1;
            if (currentFrame > totalFrames - 1) {
                currentFrame = totalBankFrame - 1;
//                banking = false;
            }
        }
    }

    @Override
    public void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        super.setActive(active);
//        this.needSetBanking = active;

        currentFrame = 0;
        totalFrames = totalBankFrame;
        System.out.println("ACT:" + totalBankFrame + "::" + (spriteBitmaps == leftBank || spriteBitmaps == rightBank));
        if (!active) {
            spriteBitmaps = normalBitmap;
            totalFrames = 1;
        }
    }

    @Override
    public void initBySaved(GameSprite gameSprite) {
        try {
            super.initBySaved(gameSprite);
        } catch (UnsupportedOperationException e) {
        }
        if (gameSprite instanceof GamePlayerSprite) {
            GamePlayerSprite playerSprite = (GamePlayerSprite) gameSprite;
            this.setAngelArc(playerSprite.getAngelArc());
            this.spriteBitmaps = this.normalBitmap;
            this.totalFrames = 1;
            this.currentFrame = 0;
            this.active = false;
            this.screenWidth = playerSprite.screenWidth;
            this.screenHeight = playerSprite.screenHeight;
        } else {
            throw new IllegalArgumentException("gameSprite is not GamePlayerSprite\'s instance");
        }
    }

    public double getAngelArc() {
        return angelArc;
    }

    public void setAngelArc(double angelArc) {
        this.angelArc = angelArc;
        if (angelArc > Math.PI / 2 && angelArc <= Math.PI * 3 / 2) {
            spriteBitmaps = leftBank;
        } else {
            spriteBitmaps = rightBank;
        }
//        System.out.println("ARC:" + currentFrame + "::" + lastBank + "\t" + ());
//        if (!banking) {
//            currentFrame = 0;
//            totalFrames = totalBankFrame;
//        }
//        if (needSetBanking) {
//            banking = true;
//            needSetBanking = false;
//        }
    }

    public void serDestination(float destinationX, float destinationY) {
        this.destinationX = destinationX;
        this.destinationY = destinationY;
    }

    public void setDestinationX(float destinationX) {
        this.destinationX = destinationX;
    }

    public void setDestinationY(float destinationY) {
        this.destinationY = destinationY;
    }

    public void removeInvalidBullet() {
        List<GameSprite> cloneBullet = getPlayerBulletListSafeForIteration();
        Iterator<GameSprite> iterator = cloneBullet.iterator();
        RectF windowRectF = new RectF(0, 0, this.screenWidth, this.screenHeight);
//        System.out.println(windowRectF.toShortString());
        while (iterator.hasNext()) {
            GameSprite bullet = iterator.next();
            if (!Utils.rectCollide(windowRectF, bullet.getBoundRectF())) {
                iterator.remove();
            }
        }
        playerBulletList.clear();
        playerBulletList.addAll(cloneBullet);
    }

    public void setPlayerBulletList(List<GameSprite> bulletList) {
        this.playerBulletList = bulletList;
    }

    public List<GameSprite> getPlayerBulletListSafeForIteration() {
        return new ArrayList<>(this.playerBulletList);
    }
}
