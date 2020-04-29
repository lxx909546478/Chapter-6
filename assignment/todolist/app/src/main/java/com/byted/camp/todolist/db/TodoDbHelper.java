package com.byted.camp.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.byted.camp.todolist.db.TodoContract.SQL_CREATE_TABLE;
import static com.byted.camp.todolist.db.TodoContract.SQL_DROP_TABLE;
import static com.byted.camp.todolist.db.TodoContract.SQL_ADD_NEW_PRIORITY;


/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    // TODO 定义数据库名、版本；创建数据库
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "TodoList.db";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("LXX","hello");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("LXX",String.valueOf(db.getVersion()));
        db.execSQL(SQL_CREATE_TABLE);
        db.execSQL(SQL_ADD_NEW_PRIORITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
        if ((oldVersion==1||oldVersion==0) && newVersion==2) {
            db.execSQL(SQL_ADD_NEW_PRIORITY);
        }
    }
}
