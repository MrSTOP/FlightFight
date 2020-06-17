package com.flightfight.flightfight.ZhuJintao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.flightfight.flightfight.GameSprite;
import com.flightfight.flightfight.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameNpcControl {
    private float ScreenWidth;
    private float ScreenHeight;
    private Random rand;
    private Context context;
    private long npcStartTime;
    private float density;

    private int NpcSum = 100;               //NPC总数
    private int NpcCur = 0;                 //当前已有（包括死亡）NPC数量
    private int intervalTime = 800;         //间隔时间
    private int bulletIntervalTime = 4000;  //子弹间隔时间

    private List<GameNpc> npcList = new ArrayList<>();;
    private List<GameNpc> cloneNpcList =  new ArrayList<>();;

    //子弹类直接使用Sprite类
    private List<GameSprite> bulletsList = new ArrayList<>();
    private List<GameSprite> cloneBulletsList = new ArrayList<>();

    public GameNpcControl(Context context, int screenWidth, int screenHeight) {
        ScreenWidth = screenWidth;
        ScreenHeight = screenHeight;
        this.context = context;
        density = context.getResources().getDisplayMetrics().density;
        rand = new Random(System.currentTimeMillis());
    }

    //总体NPC控制
    public void GameNpcAllManager(Canvas canvas) {
        this.LoadNpc();
        this.updateNpcPos();
        this.updateBulletsPos();
        this.clearUnuseNBList();            //清除无用的NPC和子弹
        this.draw(canvas);
    }

    public void LoadNpc() {
        if (npcList == null) {
            npcList = new ArrayList<>();
            npcStartTime = System.currentTimeMillis();
        }
        long npcLoadTime = System.currentTimeMillis();

        if (getNpcCur() <= getNpcSum()) {
            if (npcList.size() < 10) {
                if (npcLoadTime - npcStartTime > getIntervalTime()) {
                    Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.enemy1_1);
                    GameNpc curTemNpc = new GameNpc(context, bmp, 1, 1);
                    curTemNpc.setSpeed(3 * density);
                    int r = rand.nextInt(3);          //0:垂直；1：左下；2：右下；
                    switch (r)
                    {
                        case 0:
                            curTemNpc.setDir(GameNpc.DOWN);
                            break;
                        case 1:
                            curTemNpc.setDir(GameNpc.LEFTDOWN);
                            break;
                        case 2:
                            curTemNpc.setDir(GameNpc.RIGHTDOWN);
                            break;
                    }
                    curTemNpc.setHp(1);
                    curTemNpc.setLife(1);
                    curTemNpc.setActive(true);
                    curTemNpc.setRatio(0.15f * density);
                    curTemNpc.setFireStartTime(System.currentTimeMillis());         //设置开火计时
                    float px = rand.nextInt((int) (ScreenWidth - curTemNpc.getWidth()));
                    float py = (0 - curTemNpc.getHeight());
                    curTemNpc.setX(px);
                    curTemNpc.setY(py);

                    //Log.d("NPC", "NPC X:" + curTemNpc.getX() + " Y:" + curTemNpc.getY() + "NPC W:" + curTemNpc.getWidth() + " H:" + curTemNpc.getHeight());
                    npcList.add(curTemNpc);
                    int i = getNpcCur();
                    setNpcCur(i++);
                    npcStartTime = System.currentTimeMillis();
                }
            }
        }
    }

    public void updateNpcPos() {
        if (npcList != null) {
            cloneNpcList = new ArrayList<>(npcList);
            //将原始的npcList进行克隆，每次只绘制克隆的
            //cloneNpc.addAll(npcList);
            for (GameNpc tempNpc : cloneNpcList) {
                tempNpc.move();
                tempNpc.NpcBoundJudge(ScreenWidth,ScreenHeight);
                tempNpc.setFireCurTime(System.currentTimeMillis());
                LoadBullets(tempNpc);
                //Log.d("NPC", "NPC X:" + tempNpc.getX() + " Y:" + tempNpc.getY() + "Move:NPC W:" + tempNpc.getWidth() + " H:" + tempNpc.getHeight());
            }
            cloneNpcList.clear();
        }
    }

    //清除到达屏幕外（下方）的NPC和子弹
    public void clearUnuseNBList() {
        //清除超出屏幕的NPC
        if (npcList != null) {
            //将原始的npcList进行克隆，每次只绘制克隆的
            cloneNpcList = new ArrayList<>(npcList);
            Iterator<GameNpc> it = cloneNpcList.iterator();
            while (it.hasNext()) {
                GameNpc tempNpc = it.next();
                if (tempNpc.getY() > ScreenHeight + tempNpc.getHeight() || !tempNpc.isActive()) {
                    tempNpc.releaseBitmap();
                    it.remove();
                }
            }
            npcList.clear();
            npcList.addAll(cloneNpcList);
            cloneNpcList.clear();
        }

        //清除超出屏幕的子弹
        if (bulletsList != null) {
            //将原始的npcList进行克隆，每次只绘制克隆的
            cloneBulletsList = new ArrayList<>(bulletsList);
            Iterator<GameSprite> it = cloneBulletsList.iterator();
            while (it.hasNext()) {
                GameSprite tempBullet = it.next();
                if (tempBullet.getY() > ScreenHeight + tempBullet.getHeight() || !tempBullet.isActive()) {
                    tempBullet.releaseBitmap();
                    it.remove();
                }
            }
            bulletsList.clear();
            bulletsList.addAll(cloneBulletsList);
            cloneBulletsList.clear();
        }
    }

    public void LoadBullets(GameNpc curCtrlNpc) {
        if (bulletsList == null) {
            bulletsList = new ArrayList<>();
        }

        if (curCtrlNpc.getFireCurTime() - curCtrlNpc.getFireStartTime() > getBulletIntervalTime()) {
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bullet3);
            //子弹需要翻转
            Bitmap trueBmp = curCtrlNpc.getRotateBitmap(bmp);
            GameSprite curTempBullet = new GameSprite(context, trueBmp, 2, 2);
            curTempBullet.setSpeed(5 * density);
            curTempBullet.setDir(GameSprite.DOWN);
            curTempBullet.setHp(1);
            curTempBullet.setLife(1);
            curTempBullet.setActive(true);
            curTempBullet.setRatio(0.4f * density);
            float px = curCtrlNpc.getX() + ((float) curCtrlNpc.getWidth() / 3) - curTempBullet.getWidth();
            float py = curCtrlNpc.getY() + curTempBullet.getHeight() / 2;
            curTempBullet.setX(px);
            curTempBullet.setY(py);

//            Log.d("Bullet", "Bullet X:" + curTempBullet.getX() + " Y:" + curTempBullet.getY() + "Bullet W:" + curTempBullet.getWidth() + " H:" + curTempBullet.getHeight());
            bulletsList.add(curTempBullet);
            curCtrlNpc.setFireStartTime(System.currentTimeMillis());
        }

    }

    public void updateBulletsPos() {
        if (bulletsList != null) {
            cloneBulletsList = new ArrayList<>(bulletsList);
            //将原始的npcList进行克隆，每次只绘制克隆的
            //cloneBulletsList.addAll(npcList);
            for (GameSprite tempBullet : cloneBulletsList) {
                tempBullet.move();
//                Log.d("Bullet", "Bullet X:" + tempBullet.getX() + " Y:" + tempBullet.getY() + "Move:Bullet W:" + tempBullet.getWidth() + " H:" + tempBullet.getHeight());
            }
            cloneBulletsList.clear();
        }
    }

    public void draw(Canvas canvas) {
        //绘制NPC
        if (cloneNpcList != null) {
            if (cloneNpcList == null) {
                cloneNpcList = new ArrayList<>();
            }
            //将原始的playerMissles进行克隆，每次只绘制克隆的
            cloneNpcList.addAll(npcList);
            for (GameNpc tempNpc : cloneNpcList) {
                tempNpc.setAlpha(100);
                tempNpc.draw(canvas);
            }
            //绘制完毕，清空克隆的内容
            cloneNpcList.clear();
        }

        //绘制子弹Bullet
        if (cloneBulletsList != null) {
            if (cloneBulletsList == null) {
                cloneBulletsList = new ArrayList<>();
            }
            //将原始的playerMissles进行克隆，每次只绘制克隆的
            cloneBulletsList.addAll(bulletsList);
            for (GameSprite tempBullet : cloneBulletsList) {
                tempBullet.setAlpha(100);
                tempBullet.draw(canvas);
            }
            //绘制完毕，清空克隆的内容
            cloneBulletsList.clear();
        }
    }

    public int getNpcSum() {
        return NpcSum;
    }

    public void setNpcSum(int npcSum) {
        NpcSum = npcSum;
    }

    public int getNpcCur() {
        return NpcCur;
    }

    public void setNpcCur(int npcCur) {
        NpcCur = npcCur;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getBulletIntervalTime() {
        return bulletIntervalTime;
    }

    public void setBulletIntervalTime(int bulletIntervalTime) {
        this.bulletIntervalTime = bulletIntervalTime;
    }

    public List<GameNpc> getNpcList() {
        return npcList;
    }

    public void setNpcList(List<GameNpc> npcList) {
        this.npcList = npcList;
    }

    public List<GameSprite> getBulletsList() {
        return bulletsList;
    }

    public void setBulletsList(List<GameSprite> bulletsList) {
        this.bulletsList = bulletsList;
    }
}
