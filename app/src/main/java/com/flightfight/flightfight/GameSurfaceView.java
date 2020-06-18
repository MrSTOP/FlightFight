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
import android.graphics.drawable.Drawable;
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
    private Paint paintback;
    private Canvas mCanvas;
    private Bitmap memBmp;
    private BanButtonListener banButtonListener;
    private Rect winAndFaildbtn;
    private Rect pauseRect;
    private GameState gameState;
    private PauseButtonListener pauseButtonListener;

    private Drawable pauseButtonDrawable;

    private Bitmap pauseBitmap;
    private boolean readyDrawFaild = false;
    private final Object lock = new Object();

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

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
  //      int[] attrsArray = new int[] {android.R.attr.background};
     //   TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
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
        paintback = new Paint();

        textPaint = new Paint();
        textPaint.setARGB(254, 220, 0, 0);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(80);
        this.setKeepScreenOn(true);
        setGameState(GameState.GAME_START);
      //  pauseBitmap = new Bitmap();
        Resources resources = context.getApplicationContext().getResources();
        pauseBitmap = BitmapFactory.decodeResource(resources, R.mipmap.bullet1);
        pauseButtonDrawable = context.getApplicationContext().getResources().getDrawable(R.drawable.ic_pause_black_24dp);
    }

    public void SetScreen(int ScreenWidth, int ScreenHeight){
        this.ScreenWidth = ScreenWidth;
        this.ScreenHeight = ScreenHeight;
        game = new GameManager(context, ScreenWidth, ScreenHeight);
        controller = new GameControl(ScreenWidth, ScreenHeight);
        controller.setPlayerRect(game.getPlayerRectF());
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
                        if(gameState == GameState.GAME_START && game.isPlayerDead())
                        {

                                game.updateAnimation();
                                game.updateHappyFish();
                                game.draw(mCanvas);
                                controller.draw(mCanvas);

                                drawStaus(mCanvas, ScreenWidth, ScreenHeight, game.getPlayerHp(), 100);
                                pauseRect = drawPauseBtn(mCanvas);
                        }
                        if(!game.isPlayerDead() && !readyDrawFaild){
                            Resources resources = context.getApplicationContext().getResources();
                            Bitmap bmp = BitmapFactory.decodeResource(resources, R.mipmap.enemy1_2);
                            drawFaildAndVictory(mCanvas, bmp);
                        }
                    }

                    Rect currentSrcRect = new Rect();
                    currentSrcRect.left = 0;
                    currentSrcRect.top = 0;
                    currentSrcRect.right = memBmp.getWidth();
                    currentSrcRect.bottom = memBmp.getHeight();


                    Rect desRect = new Rect();
                    desRect.left = 0;
                    desRect.top = 0;
                    desRect.right = mCanvas.getWidth();
                    desRect.bottom = mCanvas.getHeight();
                    surfCanvas.drawBitmap(memBmp,currentSrcRect,desRect, paintback);
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
        if(gameState == GameState.GAME_START && !readyDrawFaild){
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

            if(event.getX() >= pauseRect.left && event.getX() <= pauseRect.right && event.getY() >= pauseRect.top && event.getY() <= pauseRect.bottom){
                pauseButtonListener.pauseListener();
            }

        }

//        System.out.println("DX: " + controller.getPlayerDestinationX() + "DY: " + controller.getPlayerDestinationY());

        if(readyDrawFaild){
            banButtonListener.banButtonListener();
            if(event.getX() >= winAndFaildbtn.left && event.getX() <= winAndFaildbtn.right && event.getY() >= winAndFaildbtn.top && event.getY() <= winAndFaildbtn.bottom){
                Intent intent = new Intent(context,MainActivity.class);
                context.startActivity(intent);
            }

        }
        return true;


    }


    public void drawStaus(Canvas mcanvas, int screenWidth, int screenHeight, int hp, int enemyCount){

        textPaint.setTextAlign(Paint.Align.CENTER);
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
        textPaint.setTextAlign(Paint.Align.LEFT);
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

        readyDrawFaild = true;
    }

    public Rect drawPauseBtn(Canvas mCanvas){
        Rect desRect = new Rect();
        desRect.top = 10;
        desRect.left =ScreenWidth - pauseBitmap.getWidth();
        desRect.right = ScreenWidth;
        desRect.bottom = pauseBitmap.getHeight();


        Rect srcRect = new Rect(0,0,pauseBitmap.getWidth(),pauseBitmap.getHeight());

        pauseButtonDrawable.setBounds(desRect);
        pauseButtonDrawable.draw(mCanvas);
        //mCanvas.drawBitmap(pauseBitmap,srcRect,desRect,paintback);

        return desRect;


    }
    public void setBanButtonListener(BanButtonListener banButtonListener) {
        this.banButtonListener = banButtonListener;
    }
    public interface BanButtonListener{
        void banButtonListener();
    }


    public interface PauseButtonListener{
        void pauseListener();
    }
    public void setPauseButtonListener(PauseButtonListener pauseButtonListener) {
        this.pauseButtonListener = pauseButtonListener;
    }

    private void drawAbout(Canvas canvas){
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(60);
        int xPos = (canvas.getWidth()/2);
        int yPos = (int)((canvas.getHeight()/2) - (3*(textPaint.descent() - textPaint.ascent())/2));
        Rect desRec = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawRect(desRec, textPaintBack);
        canvas.drawText("关于", xPos, yPos, textPaint);
        yPos+=(int)(textPaint.descent() - textPaint.ascent());
        canvas.drawText("App:飞机大战1.0", xPos, yPos, textPaint);
        yPos+=(int)(textPaint.descent() - textPaint.ascent());
        canvas.drawText("学号：8002117042", xPos, yPos, textPaint);
        yPos+=(int)(textPaint.descent() - textPaint.ascent());
        canvas.drawText("实验日期：2020-6-12", xPos, yPos, textPaint);
    }


    public enum GameState{GAME_START, GAME_PAUSE, GAME_ABOUT}


}