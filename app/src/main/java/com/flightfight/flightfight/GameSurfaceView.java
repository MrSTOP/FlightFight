package com.flightfight.flightfight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.flightfight.flightfight.yankunwei.GameArchive;
import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yankunwei.Utils;
import com.flightfight.flightfight.yankunwei.ValueContainer;

import java.util.Date;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static int TIME_IN_FRAME = 24;
    private SurfaceHolder mHolder;
    private boolean isRunning;//控制绘画线程的标志位
    private GameManager game;
    private GameControl controller;
    private Context context;

    private final Object lock = new Object();

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            game.setAchieveData();
        }
    };

    public GameSurfaceView(Context context, int ScreenWidth, int ScreenHeight) {
        super(context);
        initView();
        game = new GameManager(context, ScreenWidth, ScreenHeight);
        controller = new GameControl(ScreenWidth, ScreenHeight);
        controller.setPlayerRect(game.getPlayerRectF());
        this.context = context;
    }

    private void initView() {
        mHolder = getHolder();//获取SurfaceHolder 对象
        mHolder.addCallback(this);//注册Surface Holder 的回调方法
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        context.registerReceiver(receiver, new IntentFilter(GameSaveService.SERVICE_RESPONSE_LOAD_GAME_ACHIEVE));
        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        context.unregisterReceiver(receiver);
    }

    @Override
    public void run() {
        while (isRunning) {
            Canvas surfCanvas = null;
            if (mHolder == null) {
                return;
            }
            //获得当前的毫秒
            long frameStartTime = System.currentTimeMillis();
            surfCanvas = mHolder.lockCanvas();
            if (surfCanvas != null) {
                try {
                    synchronized (mHolder) {
                        synchronized (lock) {
                            game.updateAnimation();
                            game.updateHappyFish();
                            game.draw(surfCanvas);
                            controller.draw(surfCanvas);
                        }
                    }
                } finally {
                    mHolder.unlockCanvasAndPost(surfCanvas);
                }
            }
            //取得更新结束的时间
            long frameEndTime = System.currentTimeMillis();
            //计算出—次更新的毫秒数
            long diffTime = frameEndTime - frameStartTime; //一次更新轰秒数
            long interval = 1000 / TIME_IN_FRAME;
            //确保每次更新时间为mFramesPerSec 帧
            while (diffTime <= interval) {
                diffTime = (int) (System.currentTimeMillis() - frameStartTime);
                Thread.yield();//线程等待
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.getTouch(event);
        if (controller.isNoTouched() || !controller.isPlayerTouched()) {
            game.setPlayerActive(false);
        }
        if (controller.isPlayerTouched()) {
            game.setPlayerActive(true);
        }
        if (controller.isFireTouched()) {
            synchronized (lock) {
                game.load();
            }
//            game.loadBubbles();
        }
        game.setPlayerAngelArc(controller.getPlayerAngleArc());
        game.setPlayerDestination(controller.getPlayerDestinationX(), controller.getPlayerDestinationY());
        return true;
    }
}