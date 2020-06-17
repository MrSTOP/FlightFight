package com.flightfight.flightfight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
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
    private int ScreenWidth;
    private int ScreenHeight;
    private Paint textPaint;
    private Paint textPaintBack;
    private Canvas mCanvas;
    private Bitmap memBmp;
    private BanButtonListener banButtonListener;
    private Rect winAndFaildbtn;
    private final Object lock = new Object();

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            game.setAchieveData(controller);
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

    public GameSurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
        int[] attrsArray = new int[] {android.R.attr.background};
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        //   Drawable background = ta.getDrawable(0);
        Resources res = context.getApplicationContext().getResources();
//        prizeBmp = ((BitmapDrawable)background).getBitmap();

        //    coverBmp = BitmapFactory.decodeResource(res, R.drawable.scratch_area);
        initView();
        //      game = new GameManager(context, ScreenWidth, ScreenHeight);
        //     controller = new GameControl(ScreenWidth, ScreenHeight);

    }

    private void initView() {
        mHolder = getHolder();//获取SurfaceHolder 对象
        mHolder.addCallback(this);//注册Surface Holder 的回调方法
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }

    public void SetScreen(int ScreenWidth, int ScreenHeight){
        this.ScreenWidth = ScreenWidth;
        this.ScreenHeight = ScreenHeight;
        game = new GameManager(context, ScreenWidth, ScreenHeight);
        controller = new GameControl(ScreenWidth, ScreenHeight);
        memBmp = Bitmap.createBitmap(ScreenWidth, ScreenHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(memBmp);
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

        game.setPlayerAngelArc(controller.getPlayerAngleArc());
        game.setPlayerDestination(controller.getPlayerDestinationX(), controller.getPlayerDestinationY());

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
//        System.out.println("DX: " + controller.getPlayerDestinationX() + "DY: " + controller.getPlayerDestinationY());
        return true;
    }


    public void drawStaus(Canvas mcanvas, int screenWidth, int screenHeight, int hp, int enemyCount){
        textPaint = new Paint();
        textPaint.setARGB(254, 220, 0, 0);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(40);
        textPaintBack = new Paint();
        textPaintBack.setARGB(125,0,125,200);
        textPaintBack.setDither(true);
        String hpText = "HP:";
        String enemyText = "Enemy:" + enemyCount;
        int hpWidth;
        hpWidth = (int) textPaint.measureText(hpText);
        Rect rect = new Rect(20+hpWidth,20,220+hpWidth,5+(int)(textPaint.descent()-textPaint.ascent()));
        mcanvas.drawText(hpText, 50, 50, textPaint);
        mcanvas.drawRect(rect,textPaintBack);
        Rect hprect = new Rect(20+hpWidth,20,20+hp*2+hpWidth,5+(int)(textPaint.descent()-textPaint.ascent()));
        textPaintBack.setARGB(254,224,125,200);
        mcanvas.drawRect(hprect,textPaintBack);

        mcanvas.drawText(enemyText, 100, 50+(int)(textPaint.descent()-textPaint.ascent()), textPaint);
    }

    public void drawFaildAndVictory(Canvas canvas, Bitmap faildBitmap) {
        winAndFaildbtn = new Rect();
        textPaint = new Paint();
        textPaint.setARGB(254, 220, 0, 0);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(80);
        Rect desRect = new Rect();
        desRect.top = 0;
        desRect.left = canvas.getWidth()/4;
        desRect.right = canvas.getWidth()*3/4;
        desRect.bottom = canvas.getHeight();

        Rect srcRect = new Rect(faildBitmap.getWidth()/4,0,faildBitmap.getWidth()*3/4,faildBitmap.getHeight());

        canvas.drawBitmap(faildBitmap,srcRect,desRect,textPaint);

        Paint paint = new Paint();
        paint.setARGB(20,50,50,50);
        Rect bckRect = new Rect(ScreenWidth/4,0,ScreenWidth*3/4,ScreenHeight);
        mCanvas.drawRect(bckRect,paint);
        System.out.println(("SW: " + ScreenWidth + " SH: " + ScreenHeight));
        mCanvas.drawText("菜单",ScreenWidth/2-80, ScreenHeight-200, textPaint);
        // winAndFaildbtn = textPaint.getTextBounds();
        int width = (int) textPaint.measureText("菜单");
        winAndFaildbtn.left = ScreenWidth/2-100;
        winAndFaildbtn.right = winAndFaildbtn.left + width + 20;
        winAndFaildbtn.top = ScreenHeight-210 - (int)(textPaint.descent()-textPaint.ascent());
        winAndFaildbtn.bottom = ScreenHeight-190;
    }

    public void setBanButtonListener(BanButtonListener banButtonListener) {
        this.banButtonListener = banButtonListener;
    }
    public interface BanButtonListener{
        void banButtonListener();
    }
}