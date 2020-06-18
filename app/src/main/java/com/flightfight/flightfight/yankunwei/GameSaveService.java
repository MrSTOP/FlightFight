package com.flightfight.flightfight.yankunwei;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.flightfight.flightfight.yankunwei.database.LeaderBoardDAO;
import com.flightfight.flightfight.yankunwei.database.bean.PlayerRecord;

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
    public static final String SERVICE_ACTION_GET_ALL_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".action.load.game.state.all";
    public static final String SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".response.load.game.state.all";

    public static final String SERVICE_ACTION_SAVE_PLAYER_RECORD_ARG = "playerRecord";
    public static final String SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_TIME = "gameAchieveTime";
    public static final String SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG = "gameTime";


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
            savePlayerRecord(intent);
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_LOAD_ALL_PLAYER_RECORD)) {
            getLeaderBoard();
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_SAVE_GAME_ACHIEVE)) {
            saveGameAchieve(intent);
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_LOAD_GAME_ACHIEVE)) {
            loadGameAchieve(intent);
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
        ArrayList<Date> dateList = new ArrayList<>(allGameAchieve.size());
        for (Map.Entry<String, ?> entry : allGameAchieve.entrySet()) {
            Date date = new Date(Long.parseLong(entry.getKey()));
            dateList.add(date);
        }
        Intent gameAchieveDates = new Intent(SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE);
        ValueContainer.SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE_ARG_DATA = Utils.GSON.toJson(dateList);
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
    }

    private void getLeaderBoard() {
        LeaderBoardDAO leaderBoardDAO = new LeaderBoardDAO(this);
        ArrayList<PlayerRecord> playerRecords = new ArrayList<>(leaderBoardDAO.getAllPlayerRecord());
        ValueContainer.SERVICE_RESPONSE_LOAD_ALL_GAME_RECORD_ARG_DATA = Utils.GSON.toJson(playerRecords);
        Intent records = new Intent(SERVICE_RESPONSE_LOAD_ALL_PLAYER_RECORD);
        sendBroadcast(records);
    }

    private void saveGameAchieve(Intent intent) {
        String gameArchiveJson = ValueContainer.SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_DATA;
        ValueContainer.SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_DATA = null;
        long time = intent.getLongExtra(SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_TIME, -1);
        if (gameArchiveJson == null) {
            throw new IllegalArgumentException("\"SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_DATA\"  can not be null");
        }
        if (time == -1) {
            throw new IllegalArgumentException(SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_TIME + " not exist");
        }
        SharedPreferences.Editor editor = getSharedPreferences("gameAchieves", Context.MODE_PRIVATE).edit();
        editor.putString(String.valueOf(time), gameArchiveJson);
        System.out.println(time + " @ " + gameArchiveJson);
        editor.apply();
    }

    private void loadGameAchieve(Intent intent) {
        long time = intent.getLongExtra(SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG, -1);
        if (time == -1) {
            throw new IllegalArgumentException("Game achieve ID:[" + SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG + "] can not be null");
        }
        SharedPreferences sharedPreferences = getSharedPreferences("gameAchieves", Context.MODE_PRIVATE);
        ValueContainer.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG_DATA = sharedPreferences.getString(String.valueOf(time), null);
        if (ValueContainer.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG_DATA == null) {
            throw new IllegalArgumentException("Game achieve ID:[" + time + "] not exist");
        }
        Intent gameAchieveIntent = new Intent(SERVICE_RESPONSE_LOAD_GAME_ACHIEVE);
        sendBroadcast(gameAchieveIntent);
    }
}
