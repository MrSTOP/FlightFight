package com.flightfight.flightfight;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.flightfight.flightfight.ZhuJintao.GameNpc;
import com.flightfight.flightfight.ZhuJintao.GameNpcControl;
import com.flightfight.flightfight.yankunwei.GameArchive;
import com.flightfight.flightfight.yankunwei.GameBulletFactory;
import com.flightfight.flightfight.yankunwei.GameMusicManager;
import com.flightfight.flightfight.yankunwei.GamePlayerSprite;
import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yankunwei.Utils;
import com.flightfight.flightfight.yankunwei.ValueContainer;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameManager {
    private int ScreenWidth;
    private int ScreenHeight;
    private Context context;
    private Random rand;
    private GameSprite[] bubbles;
    private GamePlayerSprite player;
    private GameNpcControl npcControl;
    private Bitmap currentBackground;
    private Bitmap backBmp;
    private Bitmap backBmp2;
    private Bitmap backBmp3;
    private long bubbleStartTime;
    private float density;
    private int gameLevel;
    private boolean gameLevelChanged;
    private GameMusicManager gameMusicManager;
    private RectF windowRectF;

    Rect srcRect;
    Rect destRect;
    Paint paint;

    public GameManager(Context context, int ScreenWidth, int ScreenHeight) {
        this.context = context;
        this.ScreenWidth = ScreenWidth;
        this.ScreenHeight = ScreenHeight;
        this.windowRectF = new RectF();
        Utils.setRectF(windowRectF, 0, 0, ScreenWidth, ScreenHeight);
        density = context.getResources().getDisplayMetrics().density;
        backBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.background);
        backBmp2 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.background2);
        backBmp3 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.background3);
        this.srcRect = new Rect(0, 0, backBmp.getWidth(), backBmp.getHeight());
        this.destRect = new Rect();
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.gameLevel = 1;
        rand = new Random(System.currentTimeMillis());
        GameBulletFactory.getInstance().initFactory(context, density);
        initHappyFish();
        npcControl = new GameNpcControl(context, ScreenWidth, ScreenHeight);
        initGame();
        this.gameMusicManager = GameMusicManager.getInstance();
        this.gameMusicManager.init(context);
        this.gameMusicManager.playBGM();
    }

    public void initGame() {
        GamePlayerSprite temp = player;
        initHappyFish();
        if (temp != null) {
            this.player.setSavedScore(temp);
        }
        npcControl.startNewRound(gameLevel);
        currentBackground = getCurrentBackgroundByLevel();
    }

    public Bitmap getCurrentBackgroundByLevel() {
        return this.getCurrentBackgroundByLevel(this.gameLevel);
    }

    public Bitmap getCurrentBackgroundByLevel(int gameLevel) {
        switch (gameLevel) {
            case 1:
                return backBmp;
            case 2:
                return backBmp2;
            case 3:
                return backBmp3;
            default:
                return backBmp;
        }
    }

    public void setPlayerAngelArc(double angle) {
        player.setAngelArc(angle);
    }

    public void setPlayerActive(boolean active) {
        player.setActive(active);
    }

    public void setPlayerDestination(float x, float y) {
        player.setDestination(x, y);
    }

    public void setPlayerFlip(boolean flip) {
        player.setFlip(flip);
    }

    public void updateAnimation() {
        player.loopFrame();
    }

    private void initHappyFish() {
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1);
        Bitmap left = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_left);
        Bitmap right = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player1_right);
        player = new GamePlayerSprite(context, source, left, right, 12);
        player.setSpeed(10 * density);
        player.setActive(false);
        player.setRatio(0.5f * density);
        player.setAngelArc(0);
        player.setScreenSize(ScreenWidth, ScreenHeight);
        float px = (ScreenWidth - player.getWidth()) / 2;
        float py = ScreenHeight - player.getHeight() * 1.5F;
        player.setX(px);
        player.setY(py);
    }

    public RectF getPlayerRectF() {
        return player.getBoundRectF();
    }

    public void draw(Canvas canvas) {
        destRect.left = 0;
        destRect.right = canvas.getWidth();
        destRect.top = 0;
        destRect.bottom = canvas.getHeight();
        paint.setDither(true);
        canvas.drawBitmap(currentBackground, null, destRect, paint);
        bulletLogic();
        player.draw(canvas);
        npcControl.GameNpcAllManager(canvas);
        npcControl.draw(canvas);
//        System.out.println(("DEAD:" + isPlayerDead()));
        if (npcControl.isBossDead() && !gameLevelChanged && !isPlayerDead()) {
            gameLevel++;
            gameLevelChanged = true;
            currentBackground = getCurrentBackgroundByLevel();
        }
    }

    public void updateHappyFish() {
        player.move();
    }

    public boolean isPlayerDead() {
        return player.getLife() <= 0 && player.getHp() <= 0;
    }

    public int getPlayerHp() {
        return player.getHp();
    }

    public int getPlayerLife() {
        return player.getLife();
    }

    public void save() {
        save(System.currentTimeMillis());
    }

    public void save(long time) {
        save(new Date(time));
    }

    public void save(Date date) {
        GameArchive gameArchive = new GameArchive();
        gameArchive.setGameDate(date);
        gameArchive.setPlayer(player);
        gameArchive.setEnemyList(npcControl.getNpcList());
        gameArchive.setEnemyBulletList(npcControl.getBulletsList());
        gameArchive.setGameLevel(gameLevel);
        gameArchive.setSpareNPC(npcControl.spareNpc());
        String str = Utils.GSON.toJson(gameArchive);
        Intent save = new Intent(context, GameSaveService.class);
        save.setAction(GameSaveService.SERVICE_ACTION_SAVE_GAME_ACHIEVE);
        ValueContainer.SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_DATA = str;
        save.putExtra(GameSaveService.SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_TIME, gameArchive.getGameDate().getTime());
        save.putExtra(GameSaveService.SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_LEVEL, gameArchive.getGameLevel());
        context.startService(save);
    }

    public void load(String uuid) {
        Intent load = new Intent(context, GameSaveService.class);
        load.setAction(GameSaveService.SERVICE_ACTION_LOAD_GAME_ACHIEVE);
        load.putExtra(GameSaveService.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG, uuid);
        context.startService(load);
    }

    public void setAchieveData(GameControl controller) {
        GameArchive gameArchive = Utils.parseGameAchieve(context);
        this.player = gameArchive.getPlayer();
        this.gameLevel = gameArchive.getGameLevel();
        currentBackground = getCurrentBackgroundByLevel();
        controller.setPlayerRect(this.player.getBoundRectF());
        this.npcControl.setNowRound(this.gameLevel);
        this.npcControl.setBulletsList(gameArchive.getEnemyBulletList());
        this.npcControl.setNpcList(gameArchive.getEnemyList());
        this.npcControl.setNpcCur(npcControl.getNpcSum() - gameArchive.getSpareNPC());
    }

    private void bulletLogic() {
        List<GameSprite> playerBulletList = player.getPlayerBulletListSafeForIteration();
        List<GameSprite> enemyBulletList = npcControl.getBulletsList();
        List<GameNpc> enemyList = npcControl.getNpcList();
        ////////////////////////////////////玩家子弹命中敌人检测//////////////////////////////////
        Iterator<GameSprite> playerBulletIterator = playerBulletList.iterator();
        while (playerBulletIterator.hasNext()) {
            GameSprite playerBullet = playerBulletIterator.next();
            for (GameNpc enemy : enemyList) {
                if (Utils.rectCollide(playerBullet.getBoundRectF(), enemy.getBoundRectF())) {
                    enemy.decreaseHP();
                    if (!enemy.isActive()) {
                        gameMusicManager.play(GameMusicManager.SOUND_EXPLOSION);
                        player.increaseKilledEnemy();
                    }
                    playerBulletIterator.remove();
                    break;
                }
            }
        }
        ////////////////////////////////////清除玩家无效子弹////////////////////////////////////
        if (playerBulletList.size() > 30) {
            playerBulletIterator = playerBulletList.iterator();
            while (playerBulletIterator.hasNext()) {
                GameSprite playerBullet = playerBulletIterator.next();
                if (!Utils.rectCollide(windowRectF, playerBullet.getBoundRectF())) {
                    playerBulletIterator.remove();
                }
            }
        }
        ////////////////////////////////////敌人子弹命中玩家检测/////////////////////////////////
        Iterator<GameSprite> enemyBulletIterator = enemyBulletList.iterator();
        while (enemyBulletIterator.hasNext()) {
            GameSprite enemyBullet = enemyBulletIterator.next();
            if (Utils.collideWithPlayer(player.getCollideBoxes(), enemyBullet.getBoundRectF())) {
                int life = player.getLife();
                player.decreaseHP();
                if (player.getLife() != life) {
                    gameMusicManager.play(GameMusicManager.SOUND_EXPLOSION);
                }
                enemyBulletIterator.remove();
            }
        }
        player.setPlayerBulletList(playerBulletList);
        ////////////////////////////////////敌人玩家碰撞检测/////////////////////////////////
        for (GameNpc npc : enemyList) {
            if (npc.isActive() && Utils.collideWithPlayer(player.getCollideBoxes(), npc.getBoundRectF())) {
                boolean isBoss = npc.getNpcType() == GameNpc.isBoss;
                if (isBoss) {
                    player.setLife(0);
                }
                player.setHp(0);
                npc.setHp(0);
                gameMusicManager.play(GameMusicManager.SOUND_EXPLOSION);
                player.increaseKilledEnemy(isBoss ? 5 : 1);
            }
        }
//        System.out.println("LIFE:" + player.getLife() + " HP:" + player.getHp());
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(int gameLevel) {
        this.gameLevel = gameLevel;
    }

    public boolean isGameLevelChanged() {
        return gameLevelChanged;
    }

    public void setGameLevelChanged(boolean gameLevelChanged) {
        this.gameLevelChanged = gameLevelChanged;
    }

    public int getGameScore() {
        return player.getScore();
    }

    public int getEnemyCount() {
        return npcControl.spareNpc();
    }
}
