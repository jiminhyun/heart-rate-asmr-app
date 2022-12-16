package com.example.moveair5;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

interface DBContract {
    static final String TABLE_NAME="LOGIN";
    static final String COL_EMAIL="EMAIL";
    static final String COL_PASSWORD="PASSWORD";
    static final String COL_CHECK1="CHECK1";

    static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + "(" +
            COL_EMAIL + " TEXT, " +
            COL_PASSWORD + " TEXT, "  +
            COL_CHECK1 + " TEXT)";
    static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    static final String SQL_LOAD = "SELECT * FROM " + TABLE_NAME;
}

class DBHelper extends SQLiteOpenHelper {
    static final String DB_FILE = "login.db";
    static final int DB_VERSION = 1;

    DBHelper(Context context) {
        super(context, DB_FILE, null, DB_VERSION); // 세번째 인자 : cursor factory
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.SQL_DROP_TABLE);
        onCreate(db);
    }

}