package com.example.justinbuhay.myownkeep.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.justinbuhay.myownkeep.database.NoteTakingContract.NoteTakingEntry;

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
}
