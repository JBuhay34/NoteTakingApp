package com.example.justinbuhay.myownkeep.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TabLayout;
import android.util.Log;

import com.example.justinbuhay.myownkeep.Note;
import com.example.justinbuhay.myownkeep.database.NoteTakingContract.NoteTakingEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by justinbuhay on 11/27/17.
 */

public class KeepReaderDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NoteTakingEntry.TABLE_NAME + " (" +
                    NoteTakingEntry._ID + " INTEGER PRIMARY KEY, " +
                    NoteTakingEntry.COLUMN_NOTE_TITLE + " TEXT, " +
                    NoteTakingEntry.COLUMN_ACTUAL_NOTE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NoteTakingEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;

    private static KeepReaderDbHelper sInstance;

    public static synchronized KeepReaderDbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new KeepReaderDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    public KeepReaderDbHelper(Context context) {
        super(context, NoteTakingEntry.TABLE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
            onCreate(sqLiteDatabase);

    }

    public void addNote(Note note){

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(NoteTakingEntry.COLUMN_NOTE_TITLE, note.getNoteTitle());
            values.put(NoteTakingEntry.COLUMN_ACTUAL_NOTE, note.getNoteDescription());

            db.insertOrThrow(NoteTakingEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch(Exception e){
            Log.d(TAG, "Error while trying to add note to database");
        } finally {
            db.endTransaction();
        }


    }

    public List<Note> getAllNotes(){
        List<Note> notes = new LinkedList<>();

        String NOTES_SELECT_QUERY = String.format("SELECT * FROM " + NoteTakingEntry.TABLE_NAME);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(NOTES_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String noteTitle = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_NOTE_TITLE));
                    String noteDescription = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_ACTUAL_NOTE));
                    Note newNote = new Note(noteTitle, noteDescription);

                    notes.add(newNote);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return notes;

    }
}
