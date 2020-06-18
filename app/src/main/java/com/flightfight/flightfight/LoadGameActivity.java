package com.flightfight.flightfight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yankunwei.Utils;
import com.flightfight.flightfight.yzc.SaveGameAdapter;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoadGameActivity extends AppCompatActivity {

    private List<Date> gameInfoList;
    private SaveGameAdapter mlistAdapter;
    private ListView mListView;
    private Button manageStore;
    private Button bckBtn;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           gameInfoList =  Utils.parsePlayerRecord();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent readStore = new Intent(this, GameSaveService.class);
        readStore.setAction(GameSaveService.SERVICE_ACTION_GET_ALL_GAME_ACHIEVE);
        startService(readStore);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);
        manageStore = findViewById(R.id.manage_game);
        bckBtn = findViewById(R.id.load_bck);
        //gameInfoList = ;
        mlistAdapter = new SaveGameAdapter(LoadGameActivity.this, gameInfoList);
        mListView = findViewById(R.id.load_game_list);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
         //  Intent intent = new Intent(this, GameActivity.class);
            //intent.pu

          //  startActivity(intent);
        });



        bckBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this,MainActivity.class);
            //intent.putExtra(MainActivity.TAG_EXIT, true);
            startActivity(intent);
        });
    }



}
