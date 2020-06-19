package com.flightfight.flightfight;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.flightfight.flightfight.yankunwei.GameAchieveInfo;
import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yankunwei.Utils;
import com.flightfight.flightfight.yankunwei.database.bean.PlayerRecord;
import com.flightfight.flightfight.yzc.GameScoreAdapter;
import com.flightfight.flightfight.yzc.SaveGameAdapter;

import java.util.List;
import java.util.Objects;

public class ScoreActivity extends AppCompatActivity {
    private List<PlayerRecord> playerRecords;
    private GameScoreAdapter mlistAdapter;
    private ListView mListView;
    private Button bckBtn;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("gameInfo", "onReceive: ");
            if(Objects.equals(intent.getAction(), GameSaveService.SERVICE_RESPONSE_LOAD_ALL_PLAYER_RECORD))
            {
                playerRecords =  Utils.parsePlayerRecord();
                mlistAdapter = new GameScoreAdapter(ScoreActivity.this, playerRecords);
                mListView = findViewById(R.id.score_game_list);
                mListView.setAdapter(mlistAdapter);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent readScore = new Intent(this, GameSaveService.class);
        readScore.setAction(GameSaveService.SERVICE_ACTION_LOAD_ALL_PLAYER_RECORD);
        startService(readScore);

        IntentFilter intentFilter = new IntentFilter(GameSaveService.SERVICE_RESPONSE_LOAD_ALL_PLAYER_RECORD);
        registerReceiver(broadcastReceiver,intentFilter);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        bckBtn = findViewById(R.id.score_bck);
        //gameInfoList = ;




        bckBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this,MainActivity.class);
            //intent.putExtra(MainActivity.TAG_EXIT, true);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
