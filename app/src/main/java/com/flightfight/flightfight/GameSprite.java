package com.flightfight.flightfight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import static com.flightfight.flightfight.DebugConst.DEBUG_DRAW_SPRITE_BOUND_RECT;

public class GameSprite implements Serializable {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int UP = 3;
    public static final int DEFAULT_SPRITE_MAX_HP = 100;
    public static final int DEFAULT_SPRITE_MAX_LIFE = 3;


    protected Bitmap[] spriteBitmaps;         //Sprite位图（有动画的大于1，无动画的为1）
    protected int totalFrames;                //动画总帧数
    protected int currentFrame;               //当前帧
    protected float width;                    //Sprite宽（有动画的Sprite，该值是帧宽）
    protected float height;                   //Sprite高（有动画的Sprite，该值是帧高）

    /**
     * Sprite的x坐标，请使用Set方法设置值
     */
    @Expose
    protected float x;
    /**
     * Sprite的y坐标，请使用Set方法设置值
     */
    @Expose
    protected float y;
    @Expose
    protected int dir;                        //Sprite方向
    @Expose
    protected float speed;                    //Sprite当前行走方向
    protected boolean active;                 //Sprite是否可动
    @Expose
    protected float ratio;                    //图像缩小比例（小于1缩小，大于1放大）
    @Expose
    protected int alpha;                      //透明度
    @Expose
    protected boolean flip;                   //是否水平翻转
    @Expose
    protected int hp;                         //血量
    @Expose
    protected int maxHP = DEFAULT_SPRITE_MAX_HP;
    @Expose
    protected int life;                       //生命数

    @Expose
    protected RectF boundRect;

    public GameSprite(Context context, Bitmap rowBitmap) {
        this(context, rowBitmap, 1, 1);
//        x = 0;
//        y = 0;
//        ratio = 1.0f;
//        dir = RIGHT;
//        alpha = 200;
//        flip = false;
//        currentFrame = 0;
//        totalFrames = 0;
//        spriteBitmaps = new Bitmap[1];
//        spriteBitmaps[0] = rowBitmap;
//        width = spriteBitmaps[0].getWidth();
//        height = spriteBitmaps[0].getHeight();

    }

    //构造具有帧动画的Sprite
    //rowBitmap：有帧图的原始动画位图
    //totalFrames：帧图的总数
    //rowFrames：原始动画位图每行的帧数
    public GameSprite(Context context, Bitmap rowBitmap, int totalFrames, int rowFrames) {
        x = 0;
        y = 0;
        ratio = 1.0f;
        dir = RIGHT;
        alpha = 200;
        flip = false;
        currentFrame = 0;
        this.totalFrames = totalFrames;
        int frame_width = rowBitmap.getWidth() / rowFrames;
        int rows = totalFrames / rowFrames;
        int frame_height = rowBitmap.getHeight() / rows;
        spriteBitmaps = new Bitmap[totalFrames];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < rowFrames; col++) {
                spriteBitmaps[row * rowFrames + col] =
                        Bitmap.createBitmap(rowBitmap, col * frame_width, row * frame_height,
                                frame_width, frame_height);
            }
        }
        width = frame_width;
        height = frame_height;

        boundRect = new RectF();
        boundRect.left = x;
        boundRect.right = boundRect.left + width;
        boundRect.top = y;
        boundRect.bottom = boundRect.top + height;
        this.hp = maxHP;
        this.life = DEFAULT_SPRITE_MAX_LIFE;
    }

    public void move() {
        if (active) {
            switch (dir) {
                case LEFT:
                    this.setX(this.x - this.speed);
                    break;
                case DOWN:
                    this.setY(this.y + this.speed);
                    break;
                case RIGHT:
                    this.setX(this.x + this.speed);
                    break;
                case UP:
                    this.setY(this.y - this.speed);
                    break;
            }
        }
    }

    public void draw(Canvas canvas) {
        if (DEBUG_DRAW_SPRITE_BOUND_RECT) {
            canvas.drawRect(boundRect, DebugConst.boundPaint);
        }

        if (spriteBitmaps[currentFrame] != null) {
            RectF dst = new RectF();
            dst.left = x;
            dst.top = y;
            if (ratio == 0) {
                ratio = 1.0f;
            }
            dst.right = x + width;
            dst.bottom = y + height;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setDither(true);
            paint.setAlpha(alpha);
            if (!flip) {
                canvas.drawBitmap(spriteBitmaps[currentFrame], null, dst, paint);
            } else {
                canvas.drawBitmap(getFlipBitmap(spriteBitmaps[currentFrame]), null, dst, paint);
            }
        }
    }

    public void releaseBitmap() {
        for (Bitmap spriteBitmap : spriteBitmaps) {
            if (!spriteBitmap.isRecycled()) {
                spriteBitmap.recycle();
            }
        }
    }

    public void loopFrame() {
        if (totalFrames > 1) {
            currentFrame = currentFrame + 1;
            if (currentFrame > totalFrames - 1) {
                currentFrame = 0;
            }
        }
    }

    public Bitmap getFlipBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        int centX = source.getWidth() / 2;
        int centY = source.getHeight() / 2;
        matrix.postScale(-1, 1, centX, centY);
        return Bitmap.createBitmap(
                source, 0, 0,
                source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        boundRect.offsetTo(x, y);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        boundRect.offsetTo(x, y);
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
        this.width = spriteBitmaps[0].getWidth() * ratio;
        this.height = spriteBitmaps[0].getHeight() * ratio;
        boundRect.right = boundRect.left + width;
        boundRect.bottom = boundRect.top + height;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public RectF getBoundRectF() {
        return this.boundRect;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp <= 0) {
            this.decreaseLife();
            if (this.life > 0) {
                this.hp = maxHP;
            }
        }
    }

    public void decreaseHP(int descHP) {
        this.setHp(this.getHp() - descHP);
    }

    public void decreaseHP() {
        this.setHp(this.getHp() - 10);
    }

    public void decreaseLife() {
        this.life--;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void initBySaved(GameSprite gameSprite) {
        this.setX(gameSprite.x);
        this.setY(gameSprite.y);
        this.setSpeed(gameSprite.speed);
        this.setRatio(gameSprite.ratio);
        this.setAlpha(gameSprite.alpha);
        this.setFlip(gameSprite.flip);
        this.setHp(gameSprite.hp);
        this.setLife(gameSprite.life);
        this.setDir(gameSprite.dir);
    }
}
