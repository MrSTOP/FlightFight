package com.flightfight.flightfight.yankunwei.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.Instant;

import javax.sql.StatementEvent;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "flightFight.db";
    public static final String TABLE_LEADER_BOARD = "LeaderBoard";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_LEADER_BOARD + "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Time INTEGER, Score INTEGER, PlayerName TEXT)";
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL = "DROP TABLE IF EXISTS " + TABLE_LEADER_BOARD;
        db.execSQL(SQL);
        onCreate(db);
    }
}
