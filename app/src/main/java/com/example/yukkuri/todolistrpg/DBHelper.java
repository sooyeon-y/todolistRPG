package com.example.yukkuri.todolistrpg;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db)
    {
        String sql = "create table user (level integer, exp integer, coupon integer);";
        db.execSQL(sql);
        String sql2 = "create table drug (date text PRIMARY KEY, morning integer, night integer, dawn integer);";
        db.execSQL(sql2);
        String sql3 = "create table todolist (date text, name text, checkflag integer, exp integer);";
        db.execSQL(sql3);
        String sql4 = "create table memo (date text, memo text);";
        db.execSQL(sql4);
        String sql5 = "insert into user values(1, 0, 0)";
        db.execSQL(sql5);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        String sql = "drop table if exists user";
        db.execSQL(sql);
        String sql2 = "drop table if exists drug";
        db.execSQL(sql2);
        String sql3 = "drop table if exists todolist";
        db.execSQL(sql3);
        String sql4 = "drop table if exists memo";
        db.execSQL(sql4);
        onCreate(db);
    }

}