package com.flightfight.flightfight.yankunwei;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


import com.flightfight.flightfight.yankunwei.database.LeaderBoardDAO;
import com.flightfight.flightfight.yankunwei.database.bean.PlayerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameSaveService extends Service {
    public static final String SERVICE_ACTION_SAVE_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".action.save.player.record";
    public static final String SERVICE_ACTION_LOAD_ALL_PLAYER_RECORD = GameSaveService.class.getPackage().getName() + ".action.load.player.record.all";
    public static final String SERVICE_ACTION_SAVE_GAME = GameSaveService.class.getPackage().getName() + ".action.save.game.state";

//    class GameSaveServiceBinder extends Binder {
//        public GameSaveService getService() {
//            return GameSaveService.this;
//        }
//    }

//    private GameSaveServiceBinder binder = new GameSaveServiceBinder();

    public GameSaveService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
//        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Objects.equals(intent.getAction(), SERVICE_ACTION_SAVE_PLAYER_RECORD)) {
            PlayerRecord playerRecord = (PlayerRecord) intent.getSerializableExtra("playerRecord");
            if (playerRecord == null) {
                throw new IllegalArgumentException("PlayerRecord cannot be null");
            }
            LeaderBoardDAO leaderBoardDAO = new LeaderBoardDAO(this);
            boolean result = leaderBoardDAO.insertPlayerRecord(playerRecord);
            Intent resultIntent = new Intent(SERVICE_ACTION_SAVE_PLAYER_RECORD);
            resultIntent.putExtra("result", result);
            sendBroadcast(resultIntent);
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_LOAD_ALL_PLAYER_RECORD)) {
            LeaderBoardDAO leaderBoardDAO = new LeaderBoardDAO(this);
            ArrayList<PlayerRecord> playerRecords = new ArrayList<>(leaderBoardDAO.getAllPlayerRecord());
            Intent records = new Intent(SERVICE_ACTION_LOAD_ALL_PLAYER_RECORD);
            records.putExtra("playerRecords", playerRecords);
            sendBroadcast(intent);
        } else if (Objects.equals(intent.getAction(), SERVICE_ACTION_SAVE_GAME)) {

        }
        return super.onStartCommand(intent, flags, startId);
    }
}
