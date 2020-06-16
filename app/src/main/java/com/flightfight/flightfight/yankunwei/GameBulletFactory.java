package com.flightfight.flightfight.yankunwei;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import com.flightfight.flightfight.GameSprite;
import com.flightfight.flightfight.R;

import java.lang.ref.WeakReference;

public class GameBulletFactory {
    public static final int DEFAULT_BULLET_SPEED = 15;


    public static final int BULLET_PLAYER = 1;
    public static final int BULLET_ENEMY = 2;

    private Bitmap playerBullet;
    private Bitmap playerBoostBullet;
    private Bitmap enemyBullet;
    private WeakReference<Context> contextWeakRef;
    private float density;
    private boolean ready = false;
//    public static final int BULLET_

    private static GameBulletFactory instance = new GameBulletFactory();

    public static GameBulletFactory getInstance() {
        return instance;
    }

    public void initFactory(Context context, float density) {
        this.contextWeakRef = new WeakReference<>(context);
        playerBullet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bullet1);
        playerBoostBullet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bullet2);
        enemyBullet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bullet3);
        this.density = density;
        ready = true;
    }

    public GameSprite getPlayerBullet(final int bulletType, final RectF bound) {
        Context context = checkStatusAndGetContext();
        Bitmap bulletBitmap;
        switch (bulletType) {
            case BULLET_PLAYER:
                bulletBitmap = playerBullet;
                break;
            case BULLET_ENEMY:
                bulletBitmap = enemyBullet;
                break;
            default:
                throw new IllegalArgumentException("Bullet type [" + bulletType + "] not exist");
        }
        GameSprite bullet = new GameSprite(context, bulletBitmap, 2, 2);
        bullet.setSpeed(DEFAULT_BULLET_SPEED * density);
        bullet.setActive(true);
        bullet.setRatio(0.4f * density);
        bullet.setX(bound.left + (bound.width() - bullet.getWidth()) / 2);
        bullet.setY(bound.top + (bound.height() - bullet.getHeight()) / 2);
        bullet.setDir(GameSprite.UP);
        return bullet;
    }

    private Context checkStatusAndGetContext() {
        if (!ready) {
            throw new IllegalStateException("Factory not ready yet");
        }
        Context context = contextWeakRef.get();
        if (context == null) {
            throw new IllegalStateException("Context not exist");
        }
        return context;
    }
}
