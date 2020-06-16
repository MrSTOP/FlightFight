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

    public GamePlayerSprite(Context context, Bitmap rowBitmap) {
        super(context, rowBitmap);
    }

    public GamePlayerSprite(Context context, Bitmap rowBitmap, int totalFrames, int rowFrames) {
        super(context, rowBitmap, totalFrames, rowFrames);
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

    public double getAngelArc() {
        return angelArc;
    }

    public void setAngelArc(double angelArc) {
        this.angelArc = angelArc;
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
