package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

import com.byted.camp.todolist.beans.Note;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS "+TodoItem.TABLE_NAME+" ("
            +TodoItem._ID+" INTEGER PRIMARY KEY, "
            +TodoItem.COLUMN_NAME_DATE+" TEXT, "
            +TodoItem.COLUMN_NAME_STATE+" INTEGER, "
            +TodoItem.COLUMN_NAME_CONTENT+" TEXT)";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "+TodoItem.TABLE_NAME;

    public static final String SQL_ADD_NEW_PRIORITY="ALTER TABLE "+TodoItem.TABLE_NAME+
            " ADD COLUMN "+TodoItem.COLUMN_NAME_PRIORITY+" INTEGER DEFAULT 0";

    private TodoContract() {
    }
    // 表信息
    public static class TodoItem implements BaseColumns{

        public static final String TABLE_NAME="todo_list";

        public static final String COLUMN_NAME_DATE ="date";

        public static final String COLUMN_NAME_STATE="state";

        public static final String COLUMN_NAME_CONTENT="content";

        public static final String COLUMN_NAME_PRIORITY="priority";
    }

}
