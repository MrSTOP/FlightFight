package com.flightfight.flightfight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flightfight.flightfight.yzc.SaveGameAdapter;

import java.util.ArrayList;
import java.util.List;

public class LoadGameActivity extends AppCompatActivity {

    private List<Object> gameInfoList = new ArrayList<>();
    private SaveGameAdapter mlistAdapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        //gameInfoList = ;
        mlistAdapter = new SaveGameAdapter(LoadGameActivity.this, gameInfoList);
        mListView = findViewById(R.id.load_game_list);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
         //  Intent intent = new Intent(this, GameActivity.class);
            //intent.pu

          //  startActivity(intent);
        });
    }
}
