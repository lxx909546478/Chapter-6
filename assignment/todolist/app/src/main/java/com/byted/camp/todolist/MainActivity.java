package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.operation.db.FeedReaderContract;
import com.byted.camp.todolist.ui.NoteListAdapter;

import org.w3c.dom.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;
    public static final String TEG = "LXX";

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper=new TodoDbHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        try {
            notesAdapter.refresh(loadNotesFromDatabase());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            try {
                notesAdapter.refresh(loadNotesFromDatabase());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Note> loadNotesFromDatabase() throws ParseException {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Note> todoList=new ArrayList<>();

        String[] projection={
                TodoContract.TodoItem._ID,
                TodoContract.TodoItem.COLUMN_NAME_DATE,
                TodoContract.TodoItem.COLUMN_NAME_STATE,
                TodoContract.TodoItem.COLUMN_NAME_CONTENT,
                TodoContract.TodoItem.COLUMN_NAME_PRIORITY
        };

        final String sortOrder= TodoContract.TodoItem.COLUMN_NAME_PRIORITY+" DESC";

        Cursor cursor = db.query(
                TodoContract.TodoItem.TABLE_NAME,      // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,         // The columns for the WHERE clause
                null,     // The values for the WHERE clause
                null,         // don't group the rows
                null,          // don't filter by row groups
                sortOrder          // The sort order
        );
        // 取数据
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TodoContract.TodoItem._ID));
            Note note = new Note(id);
            // String转Date
            String str=cursor.getString(cursor.getColumnIndex(TodoContract.TodoItem.COLUMN_NAME_DATE));
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Date date = (Date) format.parse(str);
            note.setDate(date);
            note.setContent(
                    cursor.getString(cursor.getColumnIndex(TodoContract.TodoItem.COLUMN_NAME_CONTENT))
            );

            State state=State.from(cursor.getInt(cursor.getColumnIndex(TodoContract.TodoItem.COLUMN_NAME_STATE)));
            note.setState(state);

            note.setPriority(cursor.getInt(cursor.getColumnIndex(TodoContract.TodoItem.COLUMN_NAME_PRIORITY)));

            todoList.add(note);
        }

        cursor.close();
        return todoList;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection= TodoContract.TodoItem._ID+"=?";
        String[] value={String.valueOf(note.id)};
        Log.d(TEG,"delete id:"+ TodoContract.TodoItem._ID);
        int deleteRows=db.delete(TodoContract.TodoItem.TABLE_NAME,selection,value);
        Log.d(TEG,"delete columns:"+deleteRows);
        try {
            notesAdapter.refresh(loadNotesFromDatabase());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateNode(Note note) {
        // 更新数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TodoContract.TodoItem._ID+"=?";
        String[] selectionArgs={String.valueOf(note.id)};
        ContentValues values=new ContentValues();
        values.put(TodoContract.TodoItem.COLUMN_NAME_STATE,note.getState().intValue);
        db.update(TodoContract.TodoItem.TABLE_NAME,values,selection,selectionArgs);
        try {
            notesAdapter.refresh(loadNotesFromDatabase());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
