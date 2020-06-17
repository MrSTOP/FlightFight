package com.flightfight.flightfight;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicControl = findViewById(R.id.music_control);
        startGame = findViewById(R.id.main_start_game);
        aboutGame = findViewById(R.id.main_about_game);
        loadGame = findViewById(R.id.main_load_game);
        quitGame = findViewById(R.id.main_quit_game);
        pause = findViewById(R.id.pause);

        musicControl.setOnClickListener(v -> {

        });

        startGame.setOnClickListener(v->{
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        aboutGame.setOnClickListener(v -> {

        });

        quitGame.setOnClickListener(v -> {
            showExitAlert();
        });



        loadGame.setOnClickListener(v -> {

        });

        pause.setOnClickListener(v -> {
            PauseFragment fragment = new PauseFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();   // 开启一个事务
            transaction.replace(R.id.pause_content, fragment);
            transaction.commit();
        });
    }

    public void showExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告").setIcon(R.drawable.ic_launcher_foreground).setMessage("要退出 App 吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
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
