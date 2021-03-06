package com.flightfight.flightfight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yankunwei.database.bean.PlayerRecord;

import java.util.Objects;

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

    private Rect winAndFaildbtn;
    private Rect playNameRect;
    private Rect pauseRect;
    private GameState gameState;
    private PauseButtonListener pauseButtonListener;

    private Drawable pauseButtonDrawable;
    private long frameTime = -1;
    private Bitmap pauseBitmap;
    private boolean readyDrawFaildAndWin = false;
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
            if (Objects.equals(intent.getAction(), GameSaveService.SERVICE_RESPONSE_LOAD_GAME_ACHIEVE)) {
                game.setAchieveData(controller);
            } else if (Objects.equals(intent.getAction(), GameSaveService.SERVICE_RESPONSE_SAVE_PLAYER_RECORD)) {
                boolean result = intent.getBooleanExtra(GameSaveService.SERVICE_RESPONSE_SAVE_PLAYER_RECORD_ARG, false);
                    new AlertDialog.Builder(GameSurfaceView.this.context)
                    .setTitle("保存结果")
                    .setMessage(result ? "保存成功" : "保存失败")
                    .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                    .show();
            }
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

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //      int[] attrsArray = new int[] {android.R.attr.background};
        //   TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        //   Drawable background = ta.getDrawable(0);
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

    public void SetScreen(int ScreenWidth, int ScreenHeight) {
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
        IntentFilter intentFilter = new IntentFilter(GameSaveService.SERVICE_RESPONSE_LOAD_GAME_ACHIEVE);
        intentFilter.addAction(GameSaveService.SERVICE_RESPONSE_SAVE_PLAYER_RECORD);
        context.registerReceiver(receiver, intentFilter);
        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.game = new GameManager(context, width, height);
        this.controller = new GameControl(ScreenWidth, ScreenHeight);
        this.controller.setPlayerRect(game.getPlayerRectF());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        context.unregisterReceiver(receiver);
    }

    @Override
    public void run() {
        int levelPassFrames = 0;
        while (isRunning) {
            Canvas surfCanvas;
            if (mHolder == null) {
                return;
            }
            //获得当前的毫秒
            long frameStartTime = System.currentTimeMillis();
            surfCanvas = mHolder.lockCanvas();
            if (surfCanvas != null) {
                try {
                    synchronized (mHolder) {
                        if (game.isPlayerDead()) {
                            gameState = GameState.GAME_FAILD;
                        }
                        if (gameState == GameState.GAME_START) {

                            game.updateAnimation();
                            game.updateHappyFish();
                            game.draw(mCanvas);

                            drawStaus(mCanvas, ScreenWidth, ScreenHeight, game.getPlayerHp(), game.getEnemyCount(), game.getGameScore());
                            pauseRect = drawPauseBtn(mCanvas);
                            if (game.isGameLevelChanged()) {
                                game.setGameLevelChanged(false);
                                if (game.getGameLevel() >= 4) {
                                    gameState = GameState.GAME_WIN;
                                } else {
                                    gameState = GameState.GAME_PAST;
                                }
                                game.initGame();
                                controller = new GameControl(ScreenWidth, ScreenHeight);
                                controller.setPlayerRect(game.getPlayerRectF());
                            }
                        } else if (gameState == GameState.GAME_FAILD && !readyDrawFaildAndWin) {
                            Resources resources = context.getApplicationContext().getResources();
                            Bitmap bmp = BitmapFactory.decodeResource(resources, R.drawable.failed);
                            drawFaildAndVictory(mCanvas, bmp);
                        } else if (gameState == GameState.GAME_PAST) {
                            drawGameGoing(mCanvas, ScreenWidth, ScreenHeight, game.getGameLevel());
                        } else if (gameState == GameState.GAME_WIN && !readyDrawFaildAndWin) {
                            Resources resources = context.getApplicationContext().getResources();
                            Bitmap bmp = BitmapFactory.decodeResource(resources, R.drawable.win);
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
                    surfCanvas.drawBitmap(memBmp, currentSrcRect, desRect, paintback);
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
        if (gameState == GameState.GAME_START && !readyDrawFaildAndWin) {
            controller.getTouch(event);

            game.setPlayerAngelArc(controller.getPlayerAngleArc());
            game.setPlayerDestination(controller.getPlayerDestinationX(), controller.getPlayerDestinationY());

            if (controller.isNoTouched() || !controller.isPlayerTouched()) {
                game.setPlayerActive(false);
            }
            if (controller.isPlayerTouched()) {
                game.setPlayerActive(true);
            }

            if (event.getX() >= pauseRect.left && event.getX() <= pauseRect.right && event.getY() >= pauseRect.top && event.getY() <= pauseRect.bottom) {
                pauseButtonListener.pauseListener();
            }

        }

//        System.out.println("DX: " + controller.getPlayerDestinationX() + "DY: " + controller.getPlayerDestinationY());

        if (readyDrawFaildAndWin) {
            // banButtonListener.banButtonListener();
            if (gameState == GameState.GAME_WIN) {
                if (event.getX() >= playNameRect.left && event.getX() <= playNameRect.right && event.getY() >= playNameRect.top && event.getY() <= playNameRect.bottom && event.getAction() == MotionEvent.ACTION_UP) {
                    inputTitleDialog();
                }
            }

            if (event.getX() >= winAndFaildbtn.left && event.getX() <= winAndFaildbtn.right && event.getY() >= winAndFaildbtn.top && event.getY() <= winAndFaildbtn.bottom) {
                readyDrawFaildAndWin = false;
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }

        }
        return true;


    }


    public void drawStaus(Canvas mcanvas, int screenWidth, int screenHeight, int hp, int enemyCount, int score) {

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40);
        textPaintBack = new Paint();
        textPaintBack.setARGB(125, 0, 125, 200);
        textPaintBack.setDither(true);
        String hpText = "HP:";
        String enemyText = "Enemy: " + enemyCount;
        int hpWidth;
        hpWidth = (int) textPaint.measureText(hpText);
        Rect rect = new Rect(20 + hpWidth, 20, 220 + hpWidth, 5 + (int) (textPaint.descent() - textPaint.ascent()));
        mcanvas.drawText(hpText, 50, 50, textPaint);
        mcanvas.drawRect(rect, textPaintBack);
        Rect hprect = new Rect(20 + hpWidth, 20, 20 + hp * 2 + hpWidth, 5 + (int) (textPaint.descent() - textPaint.ascent()));
        textPaintBack.setARGB(254, 224, 125, 200);

        String scoreText = "分数: " + score;
        mcanvas.drawText(scoreText, ScreenWidth / 2, 50, textPaint);
        mcanvas.drawRect(hprect, textPaintBack);

        mcanvas.drawText(enemyText, 130, 50 + (int) (textPaint.descent() - textPaint.ascent()), textPaint);
    }


    public void drawGameGoing(Canvas mcanvas, int screenWidth, int screenHeight, int round) {
        Bitmap bitmap = game.getCurrentBackgroundByLevel();
        if (frameTime == -1) {
            frameTime = System.currentTimeMillis();
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(100);
        textPaintBack = new Paint();
        textPaintBack.setARGB(125, 0, 125, 200);
        textPaintBack.setDither(true);
        String hpText = "Round:" + round;
        mcanvas.drawBitmap(bitmap, null, new RectF(0, 0, screenWidth, screenHeight), paintback);
        mcanvas.drawText(hpText, screenWidth / 2, screenHeight / 2, textPaint);

        long frameEndTime = System.currentTimeMillis();

        if (frameEndTime - frameTime > 2000) {
            gameState = GameState.GAME_START;
            frameTime = -1;
        }

    }

    public void drawFaildAndVictory(Canvas canvas, Bitmap faildBitmap) {
        winAndFaildbtn = new Rect();

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(80);

        Paint paint = new Paint();
        Rect desRect = new Rect();
        desRect.top = canvas.getHeight() / 3;
        desRect.left = canvas.getWidth() / 4;
        desRect.right = canvas.getWidth() * 3 / 4;
        desRect.bottom = canvas.getHeight() * 2 / 3;

        Rect srcRect = new Rect(0, 0, faildBitmap.getWidth(), faildBitmap.getHeight());

        canvas.drawBitmap(faildBitmap, srcRect, desRect, paint);

        //Paint paint = new Paint();
        paint.setARGB(20, 50, 50, 50);
        Rect bckRect = new Rect(0, 0, ScreenWidth, ScreenHeight);
        mCanvas.drawRect(bckRect, paint);
        String btn = "返回界面";
//        System.out.println(("SW: " + ScreenWidth + " SH: " + ScreenHeight));
        mCanvas.drawText(btn, (ScreenWidth / 2) - 150, ScreenHeight - 300, textPaint);
        // winAndFaildbtn = textPaint.getTextBounds();
        int width = (int) textPaint.measureText(btn);
        winAndFaildbtn.left = ScreenWidth / 2 - 160;
        winAndFaildbtn.right = winAndFaildbtn.left + width + 20;
        winAndFaildbtn.top = ScreenHeight - 310 - (int) (textPaint.descent() - textPaint.ascent());
        winAndFaildbtn.bottom = ScreenHeight - 290;
        if (gameState == GameState.GAME_WIN) {
            playNameRect = new Rect();
            btn = "留下称谓";
            mCanvas.drawText(btn, (ScreenWidth / 2) - 150, ScreenHeight - 150, textPaint);
            width = (int) textPaint.measureText(btn);
            playNameRect.left = ScreenWidth / 2 - 160;
            playNameRect.right = winAndFaildbtn.left + width + 20;
            playNameRect.top = ScreenHeight - 160 - (int) (textPaint.descent() - textPaint.ascent());
            playNameRect.bottom = ScreenHeight - 140;
        }
        readyDrawFaildAndWin = true;


    }

    public Rect drawPauseBtn(Canvas mCanvas) {
        Rect desRect = new Rect();
        desRect.top = 10;
        desRect.left = ScreenWidth - pauseBitmap.getWidth();
        desRect.right = ScreenWidth;
        desRect.bottom = pauseBitmap.getHeight();


        pauseButtonDrawable.setBounds(desRect);
        pauseButtonDrawable.draw(mCanvas);
        //mCanvas.drawBitmap(pauseBitmap,srcRect,desRect,paintback);

        return desRect;


    }


    public interface PauseButtonListener {
        void pauseListener();
    }

    public void setPauseButtonListener(PauseButtonListener pauseButtonListener) {
        this.pauseButtonListener = pauseButtonListener;
    }


    public enum GameState {GAME_START, GAME_PAUSE, GAME_ABOUT, GAME_PAST, GAME_WIN, GAME_FAILD}

    public void saveGame() {
        if (game != null) {
            game.save();
        }
    }

    public GameManager getGame() {
        return game;
    }


    public void inputTitleDialog() {
        EditText input = new EditText(this.context);
        input.setFocusable(true);
        Log.e("TAG", "inputTitleDialog: " );
        AlertDialog alertDialog = new AlertDialog.Builder(this.context)
                .setTitle("恭喜,请输入你的名字")
                .setView(input).setPositiveButton("确定", (dialog, which) -> {
                    String playerName = input.getText().toString();
                    PlayerRecord playerRecord = new PlayerRecord(System.currentTimeMillis(), game.getGameScore(), playerName);
                    Intent intent = new Intent(GameSurfaceView.this.context, GameSaveService.class);
                    intent.setAction(GameSaveService.SERVICE_ACTION_SAVE_PLAYER_RECORD);
                    intent.putExtra(GameSaveService.SERVICE_ACTION_SAVE_PLAYER_RECORD_ARG, playerRecord);
                    GameSurfaceView.this.context.startService(intent);
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.cancel()).create();
        alertDialog.show();

    }


}