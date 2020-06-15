package com.flightfight.flightfight.yankunwei;

import android.content.Context;
import android.graphics.Bitmap;

import com.flightfight.flightfight.GameSprite;

class GamePlayerSprite extends GameSprite {

    private int angel;

    public GamePlayerSprite(Context context, Bitmap rowBitmap) {
        super(context, rowBitmap);
    }

    public GamePlayerSprite(Context context, Bitmap rowBitmap, int totalFrames, int rowFrames) {
        super(context, rowBitmap, totalFrames, rowFrames);
    }


    @Override
    public void move() {
        if (this.active) {
            this.setX(this.x + getXSpeed());
            this.setY(this.y + getYSpeed());
        }
    }

    private float getXSpeed() {
        return (float) (Math.sin(angel) * speed);
    }

    private float getYSpeed() {
        return (float) (Math.cos(angel) * speed);
    }

    @Override
    public void setDir(int dir) {
        throw new UnsupportedOperationException("Please use setAngel()");
    }

    @Override
    public int getDir() {
        throw new UnsupportedOperationException("Please use getAngel()");
    }

    public int getAngel() {
        return angel;
    }

    public void setAngel(int angel) {
        this.angel = angel;
    }
}
