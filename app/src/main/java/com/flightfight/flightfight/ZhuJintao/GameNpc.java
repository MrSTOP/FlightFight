package com.flightfight.flightfight.ZhuJintao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.flightfight.flightfight.GameSprite;
import com.google.gson.annotations.Expose;

import java.util.Random;

public class GameNpc extends GameSprite {

    public static final int LEFTDOWN = 4;
    public static final int RIGHTDOWN = 5;
    public static final int LEFTUP = 6;
    public static final int RIGHTUP = 7;

    public static final int isNormal = 1;
    public static final int isBoss = 2;

    //开火时间
    long fireStartTime;
    long fireCurTime;

    //npc类型
    @Expose
    private int npcType = isNormal;

    //npc原（总）血量
    private int sumHp;

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
        setHp(2);
    }


    //NPC移动逻辑重写
    public void move() {
        if (this.isActive() == true) {
            if (this.getNpcType() != GameNpc.isBoss) {
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
            } else if (this.getNpcType() == GameNpc.isBoss) {
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
                        break;
                    case RIGHTDOWN:
                        this.setY(this.getY() + this.getSpeed() * (float) Math.sin(Math.PI / 2));
                        this.setX(this.getX() + this.getSpeed() * (float) Math.sin(Math.PI / 2));
                        break;
                    case LEFTUP:
                        this.setY(this.getY() - this.getSpeed() * (float) Math.sin(Math.PI / 2));
                        this.setX(this.getX() - this.getSpeed() * (float) Math.sin(Math.PI / 2));
                        break;
                    case RIGHTUP:
                        this.setY(this.getY() - this.getSpeed() * (float) Math.sin(Math.PI / 2));
                        this.setX(this.getX() + this.getSpeed() * (float) Math.sin(Math.PI / 2));
                        break;
                }
            }

        }
    }

    //边界判断（只有Boss判断上下边界）
    public void NpcBoundJudge(float ScreenWidth, float ScreenHeight) {
        Random random = new Random(System.currentTimeMillis());
        int r = random.nextInt(3);
        if (this.isActive() == true) {
            if (this.getNpcType() != GameNpc.isBoss) {
                switch (this.getDir()) {
                    case LEFT:
                        if (this.getX() < 0 - this.getWidth()) {
                            this.setDir(RIGHT);
                            this.setX(0);
                        }
                        break;
                    case RIGHT:
                        if (this.getX() > ScreenWidth + this.getWidth()) {
                            this.setDir(LEFT);
                            this.setX(ScreenWidth - this.getWidth());
                        }
                        break;
                    case LEFTDOWN:
                        if (this.getX() <= 0) {
                            this.setDir(RIGHTDOWN);
                            this.setX(0);
                        }
                        break;
                    case RIGHTDOWN:
                        if (this.getX() >= ScreenWidth - this.getWidth()) {
                            this.setDir(LEFTDOWN);
                            this.setX(ScreenWidth - this.getWidth());
                        }
                        break;
                }
            }
            //Boss边界判断，限定boss只在屏幕上半部分运动
            else if (this.getNpcType() == GameNpc.isBoss) {
                switch (this.getDir()) {
                    case LEFT:
                        if (this.getX() <= 0) {
                            this.setDir(RIGHT);
                            this.setX(0);
                        }
                        break;
                    case DOWN:
                        if (this.getY() > ScreenHeight / 2 - this.getHeight()) {
                            switch (r){
                                case 0:
                                    this.setDir(RIGHTUP);
                                    break;
                                case 1:
                                    this.setDir(LEFTUP);
                                    break;
                                default:
                                    this.setDir(UP);
                                    break;
                            }
                            this.setY(ScreenHeight / 2 - this.getHeight());
                        }
                        break;
                    case RIGHT:
                        if (this.getX() > ScreenWidth + this.getWidth()) {
                            this.setDir(LEFT);
                            this.setX(ScreenWidth - this.getWidth());
                        }
                        break;
                    case UP:
                        if (this.getY() < 0) {
                            switch (r){
                                case 0:
                                    this.setDir(RIGHTDOWN);
                                    break;
                                case 1:
                                    this.setDir(LEFTDOWN);
                                    break;
                                default:
                                    this.setDir(DOWN);
                                    break;
                            }
                            this.setY(0);
                        }
                        break;
                    case LEFTDOWN:
                        if (this.getX() <= 0) {
                            this.setDir(RIGHTDOWN);
                            this.setX(0);
                        }
                        if (this.getY() > ScreenHeight / 2 - this.getHeight()) {
                            this.setDir(RIGHTUP);
                            this.setY(ScreenHeight / 2 - this.getHeight());
                        }
                        break;
                    case RIGHTDOWN:
                        if (this.getX() >= ScreenWidth - this.getWidth()) {
                            this.setDir(LEFTDOWN);
                            this.setX(ScreenWidth - this.getWidth());
                        }
                        if (this.getY() > ScreenHeight / 2 - this.getHeight()) {
                            this.setDir(LEFTUP);
                            this.setY(ScreenHeight / 2 - this.getHeight());
                        }
                        break;
                    case LEFTUP:
                        if (this.getX() <= 0) {

                            this.setDir(RIGHTUP);
                            this.setX(0);
                        }
                        if (this.getY() <= 0) {
                            this.setDir(LEFTDOWN);
                            this.setY(0);
                        }
                        break;
                    case RIGHTUP:
                        if (this.getX() >= ScreenWidth - this.getWidth()) {
                            this.setDir(LEFTUP);
                            this.setX(ScreenWidth - this.getWidth());
                        }
                        if (this.getY() <= 0) {
                            this.setDir(RIGHTDOWN);
                            this.setY(0);
                        }
                        break;
                }
            }
        }
    }

    //垂直翻转位图函数
    public static Bitmap getRotateBitmap(Bitmap source) {
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
        if (this.getHp() <= 0) {
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

    public int getNpcType() {
        return npcType;
    }

    public void setNpcType(int npcType) {
        this.npcType = npcType;
    }

    public int getSumHp() {
        return sumHp;
    }

    public void setSumHp(int sumHp) {
        this.sumHp = sumHp;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (spriteBitmaps[currentFrame] != null) {
            RectF dst = new RectF();
            dst.left = x;
            dst.top = y + 5;
            if (ratio == 0) {
                ratio = 1.0f;
            }
            dst.right = x + width;
            dst.bottom = y;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setDither(true);
            paint.setAlpha(alpha);
            paint.setColor(Color.BLACK);

            RectF colDst = new RectF();
            dst.left = x;
            dst.top = y + 5;
            if (ratio == 0) {
                ratio = 1.0f;
            }
            dst.right = x + (width * ((float) getHp() / (float) getSumHp()));
            dst.bottom = y;
            Paint colPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setDither(true);
            paint.setAlpha(alpha);
            paint.setColor(Color.RED);


            canvas.drawRect(dst,paint);
            canvas.drawRect(colDst,colPaint);
        }
    }

    @Override
    public void initBySaved(GameSprite gameSprite) {
        super.initBySaved(gameSprite);
        this.setSumHp(gameSprite.getHp());
        if (gameSprite instanceof GameNpc) {
            this.setNpcType(GameNpc.isBoss);
        } else {
            throw new IllegalArgumentException("类型转换失败！");
        }
    }
}
