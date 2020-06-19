package com.flightfight.flightfight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.flightfight.flightfight.yankunwei.GameMusicManager;
import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yzc.PauseFragment;

import java.util.Date;

public class GameActivity extends AppCompatActivity {
    private GameSurfaceView gameSurfaceView;
    private Button button;
    private PauseFragment fragment;
    private String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
        date = getIntent().getStringExtra(GameSaveService.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG);


        DisplayMetrics outMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        int screenHeight = outMetrics.heightPixels;
        boolean isAbout = getIntent().getBooleanExtra("About",false);

        gameSurfaceView = findViewById(R.id.scratch_view);
        gameSurfaceView.SetScreen(screenWidth, screenHeight);
        if(gameSurfaceView.getGame() != null){
            if(date != null){
                gameSurfaceView.getGame().load(date);
            }

        }
       // button = findViewById(R.id.nextBt);


        gameSurfaceView.setVisibility(View.VISIBLE);
        fragment = new PauseFragment();
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                gameSurfaceView.setGameState(GameSurfaceView.GameState.GAME_PAUSE);
//
//                if(!fragment.isAdded()){
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();   // 开启一个事务
//                    transaction.add(R.id.pause_content, fragment);
//                    transaction.show(fragment);
//                    transaction.commit();
//                }else {
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.show(fragment);
//                    transaction.commit();
//                }
//
//            }
//        });

        fragment.setHide(() -> {

            gameSurfaceView.setGameState(GameSurfaceView.GameState.GAME_START);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();   // 开启一个事务
            //    transaction.add(R.id.pause_content, fragment);
            transaction.hide(fragment);
            transaction.commit();
            // getFragmentManager().popBackStack();
            //  getFragmentManager().executePendingTransactions();
        });


GameMusicManager.getInstance().init(this);
        gameSurfaceView.setPauseButtonListener(()->{
            gameSurfaceView.setGameState(GameSurfaceView.GameState.GAME_PAUSE);

            if(!fragment.isAdded()){
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();   // 开启一个事务
                transaction.add(R.id.pause_content, fragment);
                transaction.show(fragment);
                transaction.commit();
            }else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.show(fragment);
                transaction.commit();
            }
        });
        //   GameSurfaceView gameView = new GameSurfaceView(this, screenWidth, screenHeight, isAbout);
        //   setContentView(gameView);
    }

    public GameSurfaceView getGameSurfaceView() {
        return gameSurfaceView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        GameMusicManager.getInstance().pauseBGM();
        GameMusicManager.getInstance().release();
    }
}
