package com.flightfight.flightfight.yankunwei;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.flightfight.flightfight.yankunwei.database.LeaderBoardDAO;
import com.flightfight.flightfight.yankunwei.database.bean.PlayerRecord;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class GameSaveService extends Service {
    public static final String SERVICE_ACTION_SAVE_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".action.save.player.record";
    public static final String SERVICE_RESPONSE_SAVE_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".response.save.player.record";
    public static final String SERVICE_ACTION_LOAD_ALL_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".action.load.player.record.all";
    public static final String SERVICE_RESPONSE_LOAD_ALL_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".response.load.player.record.all";
    public static final String SERVICE_ACTION_SAVE_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".action.save.game.state";
    public static final String SERVICE_ACTION_LOAD_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".action.load.game.state";
    public static final String SERVICE_RESPONSE_LOAD_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".response.load.game.state";
    public static final String SERVICE_ACTION_GET_ALL_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".action.load.game.state";
    public static final String SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".response.load.game.state";

    public static final String SERVICE_ACTION_SAVE_PLAYER_RECORD_ARG = "playerRecord";
    public static final String SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG = "gameAchieve";
    public static final String SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE_ARG = "achieveTimes";
    public static final String SERVICE_RESPONSE_LOAD_ALL_GAME_RECORD_ARG = "playerRecords";

//    class GameSaveServiceBinder extends Binder {
//        public GameSaveService getService() {
//            return GameSaveService.this;
//        }
//    }

//    private GameSaveServiceBinder binder = new GameSaveServiceBinder();

    public GameSaveService() {

    }

//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//        }
//    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
//        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SERVICE_ACTION_LOAD_ALL_PLAYER_RECORD);
        intentFilter.addAction(SERVICE_ACTION_SAVE_PLAYER_RECORD);
        intentFilter.addAction(SERVICE_ACTION_GET_ALL_GAME_ACHIEVE);
        intentFilter.addAction(SERVICE_ACTION_LOAD_GAME_ACHIEVE);
        intentFilter.addAction(SERVICE_ACTION_SAVE_GAME_ACHIEVE);
//        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Objects.equals(intent.getAction(), SERVICE_ACTION_SAVE_PLAYER_RECORD)) {
            Log.d("SERVICE", "SaveRecord");
            savePlayerRecord(intent);
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_LOAD_ALL_PLAYER_RECORD)) {
            getLeaderBoard();
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_SAVE_GAME_ACHIEVE)) {
            GameArchive gameArchive = (GameArchive) intent.getSerializableExtra(SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG);
            saveGameAchieve(gameArchive);
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_LOAD_GAME_ACHIEVE)) {
            loadGameAchieve(intent.getLongExtra("gameTime", 0));
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_GET_ALL_GAME_ACHIEVE)) {
            getAllGameAchieve();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
    }

    private void getAllGameAchieve() {
        SharedPreferences sharedPreferences = getSharedPreferences("gameAchieves", Context.MODE_PRIVATE);
        Map<String, ?> allGameAchieve = sharedPreferences.getAll();
        ArrayList<Date> dates = new ArrayList<>(allGameAchieve.size());
        for (Map.Entry<String, ?> entry : allGameAchieve.entrySet()) {
            Date date = new Date(Long.parseLong(entry.getKey()));
            dates.add(date);
        }
        Intent gameAchieveDates = new Intent(SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE);
        gameAchieveDates.putExtra(SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE_ARG, gameAchieveDates);
        sendBroadcast(gameAchieveDates);
    }

    private void savePlayerRecord(Intent intent) {
        PlayerRecord playerRecord = (PlayerRecord) intent.getSerializableExtra(SERVICE_ACTION_SAVE_PLAYER_RECORD_ARG);
        if (playerRecord == null) {
            throw new IllegalArgumentException("PlayerRecord cannot be null");
        }
        LeaderBoardDAO leaderBoardDAO = new LeaderBoardDAO(this);
        boolean result = leaderBoardDAO.insertPlayerRecord(playerRecord);
        Intent resultIntent = new Intent(SERVICE_RESPONSE_SAVE_PLAYER_RECORD);
        resultIntent.putExtra("result", result);
        sendBroadcast(resultIntent);
        GameArchive gameArchive = new GameArchive();
        gameArchive.setGameDate(new Date(System.currentTimeMillis()));
        saveGameAchieve(gameArchive);
    }

    private void getLeaderBoard() {
        LeaderBoardDAO leaderBoardDAO = new LeaderBoardDAO(this);
        ArrayList<PlayerRecord> playerRecords = new ArrayList<>(leaderBoardDAO.getAllPlayerRecord());
        Intent records = new Intent(SERVICE_RESPONSE_LOAD_ALL_PLAYER_RECORD);
        records.putExtra(SERVICE_RESPONSE_LOAD_ALL_GAME_RECORD_ARG, playerRecords);
        sendBroadcast(records);
    }

    private void saveGameAchieve(GameArchive gameArchive) {
        Gson gson = new Gson();
        String gameAchieveJson = gson.toJson(gameArchive);
        SharedPreferences.Editor editor = getSharedPreferences("gameAchieves", Context.MODE_PRIVATE).edit();
        editor.putString(String.valueOf(gameArchive.getGameDate().getTime()), gameAchieveJson);
        editor.apply();
    }

    private void loadGameAchieve(long time) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("gameAchieves", Context.MODE_PRIVATE);
        String gameAchieveJson = sharedPreferences.getString(String.valueOf(time), "");
        GameArchive gameArchive = gson.fromJson(gameAchieveJson, GameArchive.class);
        Intent gameAchieve = new Intent(SERVICE_RESPONSE_LOAD_GAME_ACHIEVE);
        gameAchieve.putExtra("gameAchieve", gameAchieve);
        sendBroadcast(gameAchieve);
    }
}
