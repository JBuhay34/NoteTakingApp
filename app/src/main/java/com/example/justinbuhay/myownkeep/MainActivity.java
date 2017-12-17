package com.example.justinbuhay.myownkeep;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.justinbuhay.myownkeep.database.KeepReaderDbHelper;

import java.util.LinkedList;

import static android.os.Build.VERSION_CODES.N;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Button addNoteButton;

    private KeepReaderDbHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = KeepReaderDbHelper.getInstance(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);
        addNoteButton = (Button) findViewById(R.id.add_note_button);
        addNoteButton.setOnClickListener(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)


        mAdapter = new NotesAdapter(this, databaseHelper.getAllNotes());
        mRecyclerView.setAdapter(mAdapter);

    }

    public static final int NEW_NOTE_REQUEST = 1;

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.add_note_button:
                Intent i = new Intent(MainActivity.this, AddedNoteActivity.class);
                startActivityForResult(i, NEW_NOTE_REQUEST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == NEW_NOTE_REQUEST){
            if(resultCode == RESULT_OK){
                Log.e("MainActivity.class", data.getStringExtra("titleResult"));
                Log.e("MainActivity.class", data.getStringExtra("noteDescriptionResult"));
                Note newNote = new Note(data.getStringExtra("titleResult"), data.getStringExtra("noteDescriptionResult"));
                databaseHelper.addNote(newNote);

                mAdapter = new NotesAdapter(this, databaseHelper.getAllNotes());
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
}
