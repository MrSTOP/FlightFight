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
import java.util.UUID;

public class GameSaveService extends Service {
    public static final String SHARED_PREFERENCES_NAME = "gameAchieves";

    public static final String SERVICE_ACTION_SAVE_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".action.save.player.record";
    public static final String SERVICE_RESPONSE_SAVE_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".response.save.player.record";
    public static final String SERVICE_ACTION_LOAD_ALL_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".action.load.player.record.all";
    public static final String SERVICE_RESPONSE_LOAD_ALL_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".response.load.player.record.all";
    public static final String SERVICE_ACTION_SAVE_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".action.save.game.state";
    public static final String SERVICE_RESPONSE_SAVE_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".response.save.game.state";
    public static final String SERVICE_ACTION_LOAD_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".action.load.game.state";
    public static final String SERVICE_RESPONSE_LOAD_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".response.load.game.state";
    public static final String SERVICE_ACTION_DELETE_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".action.delete.game.state";
    public static final String SERVICE_RESPONSE_DELETE_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".response.delete.game.state";
    public static final String SERVICE_ACTION_GET_ALL_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".action.load.game.state.all";
    public static final String SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE = GameSaveService.class.getPackage().getName() + ".response.load.game.state.all";

    public static final String SERVICE_ACTION_SAVE_PLAYER_RECORD_ARG = "playerRecord";
    public static final String SERVICE_RESPONSE_SAVE_PLAYER_RECORD_ARG = "result";
    public static final String SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_TIME = "gameAchieveTime";
    public static final String SERVICE_RESPONSE_SAVE_GAME_ACHIEVE_ARG = "result";
    public static final String SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG = "gameTime";
    public static final String SERVICE_ACTION_DELETE_GAME_ACHIEVE_ARG = "uuid";
    public static final String SERVICE_RESPONSE_DELETE_GAME_ACHIEVE_ARG = "result";

    public static final String SAVED_GAME_ACHIEVE_SUFFIX = "_ACHIEVE_TIME";


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
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_DELETE_GAME_ACHIEVE)) {
            deleteGameAchieve(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void deleteGameAchieve(Intent intent) {
        String uuid = intent.getStringExtra(SERVICE_ACTION_DELETE_GAME_ACHIEVE_ARG);
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(uuid);
        editor.remove(uuid + SAVED_GAME_ACHIEVE_SUFFIX);
        boolean result = editor.commit();
        Intent deleteGameAchieve = new Intent(SERVICE_RESPONSE_DELETE_GAME_ACHIEVE);
        deleteGameAchieve.putExtra(SERVICE_RESPONSE_DELETE_GAME_ACHIEVE_ARG, result);
        sendBroadcast(deleteGameAchieve);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
    }

    private void getAllGameAchieve() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allGameAchieve = sharedPreferences.getAll();
        ArrayList<GameAchieveInfo> gameAchieveInfoList = new ArrayList<>(allGameAchieve.size());
        for (Map.Entry<String, ?> entry : allGameAchieve.entrySet()) {
            if (entry.getKey().endsWith(SAVED_GAME_ACHIEVE_SUFFIX)) {
                GameAchieveInfo gameAchieveInfo = new GameAchieveInfo(
                        entry.getKey().replace(SAVED_GAME_ACHIEVE_SUFFIX, ""),
                        Long.parseLong(entry.getValue().toString()));
                gameAchieveInfoList.add(gameAchieveInfo);
            }
        }
        Intent gameAchieveDates = new Intent(SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE);
        ValueContainer.SERVICE_RESPONSE_GET_ALL_GAME_ACHIEVE_ARG_DATA = Utils.GSON.toJson(gameAchieveInfoList);
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
        resultIntent.putExtra(SERVICE_RESPONSE_SAVE_PLAYER_RECORD_ARG, result);
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
        if (time == -1) {
            throw new IllegalArgumentException(SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_TIME + " not exist");
        }
        String uuidStr = UUID.randomUUID().toString();
        if (gameArchiveJson == null) {
            throw new IllegalArgumentException("\"SERVICE_ACTION_SAVE_GAME_ACHIEVE_ARG_DATA\"  can not be null");
        }
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(uuidStr, gameArchiveJson);
        editor.putString(uuidStr + SAVED_GAME_ACHIEVE_SUFFIX, String.valueOf(time));
//        System.out.println(uuidStr + " @ " + gameArchiveJson);
//        System.out.println(uuidStr + SAVED_GAME_ACHIEVE_SUFFIX + " @ " + time);
        boolean result = editor.commit();
        Intent saveResult = new Intent(SERVICE_RESPONSE_SAVE_GAME_ACHIEVE);
        saveResult.putExtra(SERVICE_RESPONSE_SAVE_GAME_ACHIEVE_ARG, result);
        sendBroadcast(intent);
    }

    private void loadGameAchieve(Intent intent) {
        String uuid = intent.getStringExtra(SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG);
        if (uuid == null) {
            throw new IllegalArgumentException("Game achieve ID:[" + SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG + "] can not be null");
        }
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        ValueContainer.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG_DATA = sharedPreferences.getString(uuid, null);
        if (ValueContainer.SERVICE_ACTION_LOAD_GAME_ACHIEVE_ARG_DATA == null) {
            throw new IllegalArgumentException("Game achieve ID:[" + uuid + "] not exist");
        }
        Intent gameAchieveIntent = new Intent(SERVICE_RESPONSE_LOAD_GAME_ACHIEVE);
        sendBroadcast(gameAchieveIntent);
    }
}
