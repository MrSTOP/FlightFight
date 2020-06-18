package com.flightfight.flightfight;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.flightfight.flightfight.yzc.PauseFragment;

public class MainActivity extends AppCompatActivity {

    private TextView startGame;
    private TextView aboutGame;
    private TextView loadGame;
    private TextView quitGame;
    private ImageView musicControl;
    private ImageView pause;
    private boolean isPlayMusic = true;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        musicControl = findViewById(R.id.music_control);
        startGame = findViewById(R.id.main_start_game);
        aboutGame = findViewById(R.id.main_about_game);
        loadGame = findViewById(R.id.main_load_game);
        quitGame = findViewById(R.id.main_quit_game);

        musicControl.setOnClickListener(v -> {
            if(isPlayMusic){
                musicControl.setImageResource(R.drawable.ic_volume_off_black_24dp);
                isPlayMusic = false;
            }else {
                musicControl.setImageResource(R.drawable.ic_volume_up_black_24dp);
                isPlayMusic = true;
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
