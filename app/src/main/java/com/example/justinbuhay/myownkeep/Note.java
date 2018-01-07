package com.example.justinbuhay.myownkeep;

/**
 * Created by jbuha on 12/16/2017.
 */

public class Note {

    private String noteTitle;
    private String noteDescription;
    private int noteID;
    private String uniqueStorageID;
    private String notePath;
    private String noteImageUUID;

    public Note(String noteTitle, String noteDescription) {
        this.noteTitle = noteTitle;
        this.noteDescription = noteDescription;
    }

    public Note(String noteTitle, String noteDescription, String uniqueStorageID) {
        this(noteTitle, noteDescription);
        this.uniqueStorageID = uniqueStorageID;
    }

    public Note(String noteTitle, String noteDescription, String uniqueStorageID, String notePath, String noteImageUUID) {
        this(noteTitle, noteDescription, uniqueStorageID);
        this.notePath = notePath;
        this.noteImageUUID = noteImageUUID;
    }


    public Note(String noteTitle, String noteDescription, String uniqueStorageID, int noteID) {
        this(noteTitle, noteDescription, uniqueStorageID);
        this.noteID = noteID;
    }

    public Note(String noteTitle, String noteDescription, String uniqueStorageID, int noteID, String notePath) {
        this(noteTitle, noteDescription, uniqueStorageID, noteID);
        this.notePath = notePath;
    }

    public Note(String noteTitle, String noteDescription, String uniqueStorageID, int noteID, String notePath, String noteImageUUID) {
        this(noteTitle, noteDescription, uniqueStorageID, noteID, notePath);
        this.noteImageUUID = noteImageUUID;
    }


    public String getNoteImageUUID() {
        return noteImageUUID;
    }

    public void setNoteImageUUID(String noteImageUUID) {
        this.noteImageUUID = noteImageUUID;
    }

    public String getUniqueStorageID() {
        return uniqueStorageID;
    }


    public String getNotePath() {
        return notePath;
    }

    public void setNotePath(String notePath) {
        this.notePath = notePath;
    }

    public int getNoteID() {
        return noteID;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteDescription() {
        return noteDescription;
    }
}
