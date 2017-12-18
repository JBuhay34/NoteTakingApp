package com.example.justinbuhay.myownkeep;

/**
 * Created by jbuha on 12/16/2017.
 */

public class Note {

    private String noteTitle;
    private String noteDescription;
    private int noteID;

    public Note(String noteTitle, String noteDescription, int noteID) {
        this.noteTitle = noteTitle;
        this.noteDescription = noteDescription;
        this.noteID = noteID;
    }

    public Note(String noteTitle, String noteDescription) {
        this.noteTitle = noteTitle;
        this.noteDescription = noteDescription;
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
