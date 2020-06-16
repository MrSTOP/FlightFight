package com.flightfight.flightfight.yankunwei;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.flightfight.flightfight.GameSprite;

public class GamePlayerSprite extends GameSprite {

    public static final int HALF_DESTINATION_AREA_LENGTH = 15;

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
    }

    @Deprecated
    public GamePlayerSprite(Context context, Bitmap rowBitmap, int totalFrames, int rowFrames) {
        super(context, rowBitmap, totalFrames, rowFrames);
        throw new UnsupportedOperationException("Do not use this constructor");
    }

    @Override
    public void move() {
        if (this.active && !arriveDestination()) {
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
        super.setActive(active);
//        this.needSetBanking = active;

        currentFrame = 0;
        totalFrames = totalBankFrame;
        if (!active) {
            spriteBitmaps = normalBitmap;
            totalFrames = 1;
        }
    }

    public double getAngelArc() {
        return angelArc;
    }

    public void setAngelArc(double angelArc) {
        this.angelArc = angelArc;
        if (angelArc > Math.PI / 2 && angelArc < Math.PI * 3 / 2) {
            spriteBitmaps = leftBank;
        } else {
            spriteBitmaps = rightBank;
        }
//        if (!banking) {
//            currentFrame = 0;
//            totalFrames = totalBankFrame;
//        }
//        if (needSetBanking) {
//            banking = true;
//            needSetBanking = false;
//        }
    }

    public RectF getBoundRectF() {
        return this.boundRect;
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
}
