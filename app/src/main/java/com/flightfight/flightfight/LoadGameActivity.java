package com.flightfight.flightfight;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.flightfight.flightfight.yankunwei.GameAchieveInfo;
import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yankunwei.Utils;
import com.flightfight.flightfight.yzc.SaveGameAdapter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LoadGameActivity extends AppCompatActivity {

    private List<GameAchieveInfo> gameInfoList;
    private SaveGameAdapter mlistAdapter;
    private ListView mListView;

    private Button bckBtn;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Log.e("gameInfo", "onReceive: ");
            if(Objects.equals(intent.getAction(), GameSaveService.SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE))
            {
                gameInfoList =  Utils.parseAllGameAchieveInfo();
                mlistAdapter = new SaveGameAdapter(LoadGameActivity.this, gameInfoList);
                mListView = findViewById(R.id.load_game_list);
                mListView.setAdapter(mlistAdapter);
                mListView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent1 = new Intent(LoadGameActivity.this, GameActivity.class);
                    intent1.putExtra(GameSaveService.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG, gameInfoList.get(position).uuid);
                    startActivity(intent1);
                });
            }
            if(Objects.equals(intent.getAction(), GameSaveService.SERVICE_RESPONSE_DELETE_GAME_ACHIEVE)){

                new AlertDialog.Builder(context).setTitle("成功").setMessage("删除成功").setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                }).create().show();
                mlistAdapter.notifyDataSetChanged();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent readStore = new Intent(this, GameSaveService.class);
        readStore.setAction(GameSaveService.SERVICE_ACTION_GET_ALL_GAME_ACHIEVE);
        startService(readStore);

        IntentFilter intentFilter = new IntentFilter(GameSaveService.SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE);
        intentFilter.addAction(GameSaveService.SERVICE_RESPONSE_DELETE_GAME_ACHIEVE);
        registerReceiver(broadcastReceiver,intentFilter);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

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
