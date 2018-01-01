package com.example.justinbuhay.myownkeep.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.justinbuhay.myownkeep.Note;
import com.example.justinbuhay.myownkeep.database.NoteTakingContract.NoteTakingEntry;

import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by justinbuhay on 11/27/17.
 */

public class KeepReaderDbHelper extends SQLiteOpenHelper {

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NoteTakingEntry.TABLE_NAME;
    public static final int DATABASE_VERSION = 6;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NoteTakingEntry.TABLE_NAME + " (" +
                    NoteTakingEntry._ID + " INTEGER PRIMARY KEY, " +
                    NoteTakingEntry.COLUMN_UNIQUE_ID + " TEXT, " +
                    NoteTakingEntry.COLUMN_NOTE_TITLE + " TEXT, " +
                    NoteTakingEntry.COLUMN_IMAGE_PATH + " TEXT, " +
                    NoteTakingEntry.COLUMN_IMAGE_UUID + " TEXT, " +
                    NoteTakingEntry.COLUMN_ACTUAL_NOTE + " TEXT)";
    private static KeepReaderDbHelper sInstance;
    private static Context mContext;

    public KeepReaderDbHelper(Context context) {
        super(context, NoteTakingEntry.TABLE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized KeepReaderDbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            mContext = context;
            sInstance = new KeepReaderDbHelper(context.getApplicationContext());
        }
        return sInstance;
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
            values.put(NoteTakingEntry.COLUMN_UNIQUE_ID, note.getUniqueStorageID());
            if (note.getNotePath() != null) {
                values.put(NoteTakingEntry.COLUMN_IMAGE_PATH, note.getNotePath());
                values.put(NoteTakingEntry.COLUMN_IMAGE_UUID, note.getNoteImageUUID());
            }


            db.insertOrThrow(NoteTakingEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch(Exception e){
            Log.d(TAG, "Error while trying to add note to database");
        } finally {
            db.endTransaction();
        }


    }

    public void deleteNote(Note noteToDelete) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            String[] whereArgs = {noteToDelete.getNoteID() + ""};
            db.delete(NoteTakingEntry.TABLE_NAME, NoteTakingEntry._ID + "= ?", whereArgs);
            db.setTransactionSuccessful();
            Toast.makeText(mContext, "Note with title " + noteToDelete.getNoteTitle() + " deleted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete notes");
        } finally {
            db.endTransaction();
        }

    }

    public int updateNote(Note noteToUpdate, String noteTitle, String noteDescription) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NoteTakingEntry.COLUMN_NOTE_TITLE, noteTitle);
        values.put(NoteTakingEntry.COLUMN_ACTUAL_NOTE, noteDescription);

        return db.update(NoteTakingEntry.TABLE_NAME, values, NoteTakingEntry._ID + " = ?", new String[]{String.valueOf(noteToUpdate.getNoteID())});
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
                    int id = cursor.getInt(cursor.getColumnIndex(NoteTakingEntry._ID));
                    String uniqueID = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_UNIQUE_ID));
                    String notePath = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_IMAGE_PATH));
                    String imageuuid = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_IMAGE_UUID));
                    Note newNote;
                    if (notePath == null) {
                        newNote = new Note(noteTitle, noteDescription, uniqueID, id);
                    } else {
                        newNote = new Note(noteTitle, noteDescription, uniqueID, id, notePath, imageuuid);
                    }

                    notes.add(newNote);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("KeepReaderDbHelper", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return notes;

    }


    public Cursor getWordMatches(String queryString) {
        String[] columns = new String[]{NoteTakingContract.NoteTakingEntry.COLUMN_NOTE_TITLE, NoteTakingContract.NoteTakingEntry.COLUMN_ACTUAL_NOTE};
        queryString = "%" + queryString + "%";

        String where1 = NoteTakingContract.NoteTakingEntry.COLUMN_NOTE_TITLE + " LIKE ?";
        String where2 = NoteTakingEntry.COLUMN_ACTUAL_NOTE + " LIKE ?";

        String[] whereArgs = new String[]{queryString};

        try {

            Cursor cursor1;
            Cursor cursor2;
            cursor1 = this.getReadableDatabase().query(NoteTakingContract.NoteTakingEntry.TABLE_NAME, columns, where1, whereArgs, null, null, null);
            cursor2 = this.getReadableDatabase().query(NoteTakingContract.NoteTakingEntry.TABLE_NAME, columns, where2, whereArgs, null, null, null);
            MergeCursor merged = new MergeCursor(new Cursor[]{cursor1, cursor2});


            return merged;


        } catch (Exception e) {
            Log.d("KeepReaderDbHelper", "SEARCH EXCEPTION! " + e);
        }
        return null;

    }



    public LinkedList<Note> getQueriedNotes(Cursor cursor) {
        LinkedList<Note> notes = new LinkedList<>();
        // Make sure there are no duplicates.
        LinkedList<Note> noteTitles = new LinkedList<>();

        // Only process a non-null cursor with rows.
        if (cursor != null && cursor.getCount() > 0) {
            // You must move the cursor to the first item.
            cursor.moveToFirst();
            // Iterate over the cursor, while there are entries.
            do {
                String noteTitle = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_NOTE_TITLE));
                String noteDescription = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_ACTUAL_NOTE));
                String uniqueid = null;
                String notePath = null;
                String imageuuid = null;
                try {
                    uniqueid = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_UNIQUE_ID));
                    notePath = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_IMAGE_PATH));
                    imageuuid = cursor.getString(cursor.getColumnIndex(NoteTakingEntry.COLUMN_IMAGE_UUID));
                } catch (IllegalStateException ex) {
                    Log.e("KeepReaderDbHelper", ex.toString());
                } catch (Exception ex) {
                    Log.e("KeepReaderDbHelper", "Generic exception");
                }
                boolean isItAlreadyThere = false;
                for (Note title : noteTitles) {
                    if (noteTitle.equals(title.getNoteTitle()) && noteDescription.equals(title.getNoteDescription())) {
                        isItAlreadyThere = true;
                    }
                }
                Note newNote;
                if (notePath == null && imageuuid == null) {
                    newNote = new Note(noteTitle, noteDescription);
                    Log.e("KeepReaderDbHelper", noteTitle + " there is no image");
                } else {
                    newNote = new Note(noteTitle, noteDescription, uniqueid, notePath, imageuuid);
                    Log.e("KeepReaderDbHelper", "There is an image");
                }
                noteTitles.add(newNote);
                if (!isItAlreadyThere) {
                    notes.add(newNote);
                }
            } while (cursor.moveToNext()); // Returns true or false
            cursor.close();
        }


        return notes;
    }
}
