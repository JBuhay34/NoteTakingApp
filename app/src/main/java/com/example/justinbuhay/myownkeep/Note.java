package com.example.justinbuhay.myownkeep;

/**
 * Created by jbuha on 12/16/2017.
 */

public class Note {

    private String noteTitle;
    private String noteDescription;
    private int noteID;
    private String uniqueStorageID;

    public Note(String noteTitle, String noteDescription) {
        this.noteTitle = noteTitle;
        this.noteDescription = noteDescription;
    }

    public Note(String noteTitle, String noteDescription, int noteID) {
        this(noteTitle, noteDescription);
        this.noteID = noteID;

    }

    public Note(String noteTitle, String noteDescription, String uniqueStorageID) {
        this(noteTitle, noteDescription);
        this.uniqueStorageID = uniqueStorageID;
    }

    public Note(String noteTitle, String noteDescription, String uniqueStorageID, int noteID) {
        this(noteTitle, noteDescription, uniqueStorageID);
        this.noteID = noteID;
    }

    public String getUniqueStorageID() {
        return uniqueStorageID;
    }

    public void setUniqueStorageID(String uniqueStorageID) {
        this.uniqueStorageID = uniqueStorageID;
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
