package com.flightfight.flightfight.yankunwei.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flightfight.flightfight.yankunwei.database.bean.PlayerRecord;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardDAO {

    private DBHelper dbHelper;

    public LeaderBoardDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public boolean insertPlayerRecord(PlayerRecord playerRecord) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("Time", playerRecord.getTime());
            values.put("Score", playerRecord.getScore());
            values.put("PlayerName", playerRecord.getPlayerName());
            boolean result = database.insert(DBHelper.TABLE_LEADER_BOARD, null, values) != -1;
            return result;
        } finally {
            database.close();
        }
    }

    public List<PlayerRecord> getAllPlayerRecord() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        List<PlayerRecord> playerRecords = new ArrayList<>();
        try (Cursor cursor = database.query(DBHelper.TABLE_LEADER_BOARD, null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                PlayerRecord playerRecord = new PlayerRecord();
                playerRecord.setId(cursor.getInt(cursor.getColumnIndexOrThrow("ID")));
                playerRecord.setTime(cursor.getLong(cursor.getColumnIndexOrThrow("Time")));
                playerRecord.setScore(cursor.getInt(cursor.getColumnIndexOrThrow("Score")));
                playerRecord.setPlayerName(cursor.getString(cursor.getColumnIndexOrThrow("PlayerName")));
                playerRecords.add(playerRecord);
            }
            return playerRecords;
        }finally {
            database.close();
        }
    }

}
