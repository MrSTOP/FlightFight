package com.flightfight.flightfight.ZhuJintao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.flightfight.flightfight.GameSprite;

import java.util.Random;

public class GameNpc extends GameSprite {

    public static final int LEFTDOWN = 4;
    public static final int RIGHTDOWN = 5;

    //开火时间
    long fireStartTime;
    long fireCurTime;

    //构造不带帧动画的Sprite
    public GameNpc(Context context, Bitmap rowBitmap) {
        super(context, rowBitmap);
    }

    //构造具有帧动画的Sprite
    //rowBitmap：有帧图的原始动画位图
    //totalFrames：帧图的总数
    //rowFrames：原始动画位图每行的帧数
    public GameNpc(Context context, Bitmap rowBitmap, int totalFrames, int rowFrames) {
        super(context, rowBitmap, totalFrames, rowFrames);
    }

    //构造具有帧动画的Sprite
    //rowBitmap：有帧图的原始动画位图
    //totalFrames：帧图的总数
    //rowFrames：原始动画位图每行的帧数
    //hp:血量
    public GameNpc(Context context, Bitmap rowBitmap, int totalFrames, int rowFrames, int hp) {
        super(context, rowBitmap, totalFrames, rowFrames);
        setHp(1);
    }


    //NPC移动逻辑重写
    public void move() {
        if (this.isActive() == true) {
            Random random = new Random(System.currentTimeMillis());
            int r = random.nextInt(3);          //0:垂直；1：左下；2：右下；
            switch (this.getDir()) {
                case LEFT:
                    this.setX(this.getX() - this.getSpeed());
                    break;
                case DOWN:
                    this.setY(this.getY() + this.getSpeed());
                    break;
                case RIGHT:
                    this.setX(this.getX() + this.getSpeed());
                    break;
                case UP:
                    this.setY(this.getY() - this.getSpeed());
                    break;
                case LEFTDOWN:
                    this.setY(this.getY() + this.getSpeed() * (float) Math.sin(Math.PI / 2));
                    this.setX(this.getX() - this.getSpeed() * (float) Math.sin(Math.PI / 2));
/*                    if (this.getX() <= 0)
                    {
                        this.setDir(this.RIGHTDOWN);
                        this.setX(0);
                    }*/
                    break;
                case RIGHTDOWN:
                    this.setY(this.getY() + this.getSpeed() * (float) Math.sin(Math.PI / 2));
                    this.setX(this.getX() + this.getSpeed() * (float) Math.sin(Math.PI / 2));
/*                    if (this.getX() >= ScreenWidth - this.getWidth())
                    {
                        this.setDir(this.LEFTDOWN);
                        this.setX(ScreenWidth - this.getWidth());
                    }*/
                    break;
            }
        }
    }

    //边界判断（不判断上下边界）
    public void NpcBoundJudge(float ScreenWidth, float ScreenHeight) {
        if (this.isActive() == true) {
            switch (this.getDir()) {
                case LEFT:
                    if (this.getX() < 0 - this.getWidth())
                    {
                        this.setDir(this.RIGHT);
                        this.setX(0);
                    }
                    break;
/*                case DOWN:
                    if (this.getY() > ScreenHeight - this.getHeight())
                    {
                        this.setDir(this.LEFT);
                    }
                    break;*/
                case RIGHT:
                    if (this.getX() > ScreenWidth + this.getWidth())
                    {
                        this.setDir(this.LEFT);
                        this.setX(ScreenWidth - this.getWidth());
                    }
                    break;
/*                case UP:
                    break;*/
                case LEFTDOWN:
                    if (this.getX() <= 0)
                    {
                        this.setDir(this.RIGHTDOWN);
                        this.setX(0);
                    }
                    break;
                case RIGHTDOWN:
                    if (this.getX() >= ScreenWidth - this.getWidth())
                    {
                        this.setDir(this.LEFTDOWN);
                        this.setX(ScreenWidth - this.getWidth());
                    }
                    break;
            }
        }
    }

    //垂直翻转位图函数
    public Bitmap getRotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        int centX = source.getWidth() / 2;
        int centY = source.getHeight() / 2;
        matrix.postScale(1, -1, centX, centY);
        return Bitmap.createBitmap(source, 0, 0,
                source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void decreaseHP() {
        this.setHp(this.getHp() - 1);
        if(this.getHp() <= 0)
        {
            this.setActive(false);
        }
    }

    public long getFireStartTime() {
        return fireStartTime;
    }

    public void setFireStartTime(long fireStartTime) {
        this.fireStartTime = fireStartTime;
    }

    public long getFireCurTime() {
        return fireCurTime;
    }

    public void setFireCurTime(long fireCurTime) {
        this.fireCurTime = fireCurTime;
    }
}
