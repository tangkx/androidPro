package com.tkx.keyboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by tkx on 2017/5/3.
 */

public class DBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "Flag.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabase dbWrite;
    public SQLiteDatabase dbReader;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbWrite = this.getWritableDatabase();
        dbReader = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table flag " +
                "(_flag INTEGER)");
        ContentValues c = new ContentValues();
        c.put("_flag",0);
        //db.execSQL("INSERT INTO flag VALUES (0)");
        db.insert("flag",null,c);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int query(){

        Cursor c = dbReader.query("flag",null,null,null,null,null,null);
        int id = 0;
        while(c.moveToNext()){
            id = c.getInt(c.getColumnIndex("_flag"));
        }

        return id;
    }

    public void update(int val){

        ContentValues c = new ContentValues();
        c.put("_flag",val);
        dbWrite.update("flag",c,null,null);
    }
}
