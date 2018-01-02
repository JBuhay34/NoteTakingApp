package com.example.justinbuhay.myownkeep.database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.justinbuhay.myownkeep.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

import static com.example.justinbuhay.myownkeep.MainActivity.NOTE_IMAGE_PATH;
import static com.example.justinbuhay.myownkeep.MainActivity.NOTE_IMAGE_UUID;

/**
 * Created by justinbuhay on 1/1/18.
 */

public class NotesListLoader extends android.support.v4.content.AsyncTaskLoader<LinkedList<Note>> {

    private KeepReaderDbHelper databaseHelper;
    private DocumentReference mDocumentReference;

    public NotesListLoader(Context context, KeepReaderDbHelper databaseHelper, DocumentReference mDocumentReference) {
        super(context);
        this.databaseHelper = databaseHelper;
        this.mDocumentReference = mDocumentReference;
        Log.e("NotesListLoader", "it is called1");

    }


    // This method should sync all of the notes that are both in the SQLiteDatabase and the notes in Firebase FireStore

    @Override
    public LinkedList<Note> loadInBackground() {
        LinkedList<Note> notesToDisplay;

        Log.e("NotesListLoader", "it is called");
        // the get() method obtains a task array with all of the data in firebase.
        mDocumentReference.collection("noteCollection").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                if (task.isSuccessful()) {

                    List<Note> notesOnDatabase = databaseHelper.getAllNotes();
                    List<Note> notesOnFireStore = new LinkedList<Note>();

                    // This for-each loop goes through all of the notes on the firestore.
                    for (DocumentSnapshot document : task.getResult()) {

                        // The note listed on the firestore.
                        Note newNote2;
                        if (document.getString(NOTE_IMAGE_PATH) != null) {
                            newNote2 = new Note(document.getString("notetitle"), document.getString("actualnote"), document.getId(), document.getString(NOTE_IMAGE_PATH), document.getString(NOTE_IMAGE_UUID));
                        } else {
                            newNote2 = new Note(document.getString("notetitle"), document.getString("actualnote"), document.getId());
                        }
                        notesOnFireStore.add(newNote2);

                        boolean isItThere = false;

                        int position = 0;

                        // This for-each loop goes through all the notes on the database
                        for (Note theNote : notesOnDatabase) {

                            if (theNote.getUniqueStorageID().equals(newNote2.getUniqueStorageID())) {
                                isItThere = true;
                                if (notesOnDatabase.size() != 0) {
                                    if (theNote != null && (!theNote.getNoteTitle().equals(newNote2.getNoteTitle()) || !theNote.getNoteDescription().equals(newNote2.getNoteDescription()))) {
                                        Log.e("NotesListLoader", position + " was changed");
                                        databaseHelper.updateNote(databaseHelper.getAllNotes().get(position), newNote2.getNoteTitle(), newNote2.getNoteDescription());
                                    }
                                }

                            }

                            position++;

                        }

                        if (!isItThere) {
                            databaseHelper.addNote(newNote2);
                        }

                    }

                    for (int i = 0; i < notesOnDatabase.size(); i++) {
                        boolean isItThere2 = false;

                        for (Note note : notesOnFireStore) {
                            if (notesOnDatabase.get(i).getUniqueStorageID().equals(note.getUniqueStorageID())) {
                                isItThere2 = true;

                            }
                        }

                        if (!isItThere2) {
                            databaseHelper.deleteNote(notesOnDatabase.get(i));
                        }
                    }


                } else {
                    Log.e("NotesListLoader", "Error getting documents: ", task.getException());
                }
            }

        });

        notesToDisplay = (LinkedList<Note>) databaseHelper.getAllNotes();
        Log.e("NotesListLoader", notesToDisplay.toString());
        return notesToDisplay;


    }
}
