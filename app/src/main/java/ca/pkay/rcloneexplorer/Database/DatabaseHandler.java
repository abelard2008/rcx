package ca.pkay.rcloneexplorer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "rcloneExplorer.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DatabaseInfo.SQL_CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public List<Task> getAllTasks(){
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                Task.COLUMN_NAME_ID,
                Task.COLUMN_NAME_TITLE,
                Task.COLUMN_NAME_REMOTE_ID,
                Task.COLUMN_NAME_REMOTE_TYPE,
                Task.COLUMN_NAME_REMOTE_PATH,
                Task.COLUMN_NAME_LOCAL_PATH,
                Task.COLUMN_NAME_SYNC_DIRECTION
        };

        String selection = "";
        String[] selectionArgs = {};

        String sortOrder = Task.COLUMN_NAME_ID + " ASC";

        Cursor cursor = db.query(
                Task.TABLE_NAME,  // The table to query
                projection,                                  // The array of columns to return (pass null to get all)
                selection,                                   // The columns for the WHERE clause
                selectionArgs,                               // The values for the WHERE clause
                null,                               // don't group the rows
                null,                                // don't filter by row groups
                sortOrder                                   // The sort order
        );
        List<Task> results = new ArrayList<>();
        while(cursor.moveToNext()) {

            Task task = new Task(cursor.getLong(0));
            task.setTitle(cursor.getString(1));
            task.setRemote_id(cursor.getString(2));
            task.setRemote_type(cursor.getInt(3));
            task.setRemote_path(cursor.getString(4));
            task.setLocal_path(cursor.getString(5));
            task.setDirection(cursor.getInt(6));

            results.add(task);
            Log.e("app!", "List: "+task.toString());
        }
        cursor.close();

        return results;

    }

    public Task createEntry(Task taskToStore){
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Task.COLUMN_NAME_TITLE, taskToStore.getTitle());
        values.put(Task.COLUMN_NAME_LOCAL_PATH, taskToStore.getLocal_path());
        values.put(Task.COLUMN_NAME_REMOTE_ID, taskToStore.getRemote_id());
        values.put(Task.COLUMN_NAME_REMOTE_PATH, taskToStore.getRemote_path());
        values.put(Task.COLUMN_NAME_REMOTE_TYPE, taskToStore.getRemote_type());
        values.put(Task.COLUMN_NAME_SYNC_DIRECTION, taskToStore.getDirection());


        Log.e("app!", "Pre Store: "+taskToStore.toString());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Task.TABLE_NAME, null, values);

        Task newObject = new Task(newRowId);
        newObject.setTitle(taskToStore.getTitle());
        newObject.setLocal_path(taskToStore.getLocal_path());
        newObject.setRemote_id(taskToStore.getRemote_id());
        newObject.setRemote_path(taskToStore.getRemote_path());
        newObject.setRemote_type(taskToStore.getRemote_type());
        newObject.setDirection(taskToStore.getDirection());

        Log.e("app!", "Post Store: "+newObject.toString());

        return newObject;

    }

    public int deleteEntry(long id){
        SQLiteDatabase db = getWritableDatabase();
        String selection = Task.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        return db.delete(Task.TABLE_NAME, selection, selectionArgs);

    }

}