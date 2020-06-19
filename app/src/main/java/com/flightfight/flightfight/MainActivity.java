package com.flightfight.flightfight;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.flightfight.flightfight.yankunwei.GameMusicManager;
import com.flightfight.flightfight.yzc.PauseFragment;

public class MainActivity extends AppCompatActivity {

    private TextView startGame;
    private TextView aboutGame;
    private TextView loadGame;
    private TextView scoreGame;
    private TextView quitGame;
    private ImageView musicControl;
    private ImageView pause;
    private final int STOP_SPLASH = 0;
    private final int SPLASH_TIME = 3000;

    private ConstraintLayout splashLt;
    private ConstraintLayout main_layout;
    public static final String TAG_EXIT = "exit";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra(TAG_EXIT, false);
            if (isExit) {
                this.finish();
            }
        }
    }


    private Handler splashHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_SPLASH:
                    splashLt.setVisibility(View.GONE);
                    main_layout.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameMusicManager.getInstance().init(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        main_layout = findViewById(R.id.main_layout);
        splashLt = findViewById(R.id.splash);
        main_layout.setVisibility(View.GONE);
        Message msg = new Message();
        msg.what = STOP_SPLASH;

        // 注：这里必须用延迟发送消息的方法，否则ImageView不会显示出来
        splashHandler.sendMessageDelayed(msg, SPLASH_TIME);

        musicControl = findViewById(R.id.music_control);
        startGame = findViewById(R.id.main_start_game);
        aboutGame = findViewById(R.id.main_about_game);
        loadGame = findViewById(R.id.main_load_game);
        scoreGame = findViewById(R.id.main_score_game);
        quitGame = findViewById(R.id.main_quit_game);

        musicControl.setOnClickListener(v -> {
            GameMusicManager.getInstance().init(MainActivity.this);
            if(!GameMusicManager.getInstance().isMute()){
                musicControl.setImageResource(R.drawable.ic_volume_off_black_24dp);
                GameMusicManager.getInstance().setMute(true);
            }else {
                musicControl.setImageResource(R.drawable.ic_volume_up_black_24dp);
                GameMusicManager.getInstance().setMute(false);
            }
        });

        startGame.setOnClickListener(v->{

            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        aboutGame.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });
        scoreGame.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScoreActivity.class);
            startActivity(intent);
        });

        quitGame.setOnClickListener(v -> {
            showExitAlert();
        });



        loadGame.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoadGameActivity.class);
            startActivity(intent);
        });


    }

    public void showExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告").setIcon(R.drawable.ic_launcher_foreground).setMessage("要退出 App 吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                intent.putExtra(MainActivity.TAG_EXIT, true);
                                startActivity(intent);

                            }
                        }
                ).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
