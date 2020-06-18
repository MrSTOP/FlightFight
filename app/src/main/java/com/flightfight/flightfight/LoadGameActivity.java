package com.flightfight.flightfight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yankunwei.Utils;
import com.flightfight.flightfight.yzc.SaveGameAdapter;

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
            Log.e("gameInfo", "onReceive: ");
           gameInfoList =  Utils.parseAllGameAchieveDate();
            mlistAdapter = new SaveGameAdapter(LoadGameActivity.this, gameInfoList);
            mListView = findViewById(R.id.load_game_list);
            mListView.setAdapter(mlistAdapter);
            mListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent1 = new Intent(LoadGameActivity.this, GameActivity.class);
                intent1.putExtra(GameSaveService.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG, gameInfoList.get(position).getTime());
                startActivity(intent1);
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent readStore = new Intent(this, GameSaveService.class);
        readStore.setAction(GameSaveService.SERVICE_ACTION_GET_ALL_GAME_ACHIEVE);
        startService(readStore);
        registerReceiver(broadcastReceiver,new IntentFilter(GameSaveService.SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE));


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);
        manageStore = findViewById(R.id.manage_game);
        bckBtn = findViewById(R.id.load_bck);
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
