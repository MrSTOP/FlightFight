package com.flightfight.flightfight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static int TIME_IN_FRAME = 24;
    private SurfaceHolder mHolder;
    private boolean isRunning;//控制绘画线程的标志位
    private GameManager game;
    private GameControl controller;

    public GameSurfaceView(Context context, int ScreenWidth, int ScreenHeight) {
        super(context);
        initView();
        game = new GameManager(context, ScreenWidth, ScreenHeight);
        controller = new GameControl(ScreenWidth, ScreenHeight);
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
        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
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
                        game.updateAnimation();
                        game.updateHappyFish();
                        game.updateBubblePos();
                        game.draw(surfCanvas);
                        controller.draw(surfCanvas);
                    }
                } finally {
                    mHolder.unlockCanvasAndPost(surfCanvas);
                    game.clearBubbles();
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
        if (controller.isNoTouched() || !controller.isJoyTouched()) {
            game.setPlayerActive(false);
        }
        if (controller.isJoyTouched()) {
            game.setPlayerActive(true);
        }
        if (controller.isFireTouched()) {
            game.loadBubbles();
        }
        game.setPlayerDir(controller.getCurrentDir());
        return true;
    }


   public void drawPause(Bitmap memBitmap, Canvas canvas, Bitmap nowBitmap, int ScreenWidth, int ScreenHeight){
        Rect rect = new Rect();
        rect.left = ScreenWidth/4;
        rect.top = ScreenHeight/3;
        rect.right = ScreenWidth*3/4;
        rect.bottom = ScreenHeight/3*2;

       Paint bckpaint = new Paint();
       bckpaint.setARGB(125,0,125,200);
       bckpaint.setDither(true);

       Paint textPaint = new Paint();
       textPaint.setARGB(254, 220, 0, 0);
       textPaint.setTextAlign(Paint.Align.CENTER);
       textPaint.setFakeBoldText(true);
       textPaint.setTextSize(80);



   }
}