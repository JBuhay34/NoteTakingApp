package com.example.justinbuhay.myownkeep;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.justinbuhay.myownkeep.database.KeepReaderDbHelper;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int NEW_NOTE_REQUEST = 1;
    public static final int DELETE_NOTE_REQUEST = 2;
    private final String LOG_TAG = MainActivity.class.getName();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NotesAdapter mAdapter;
    private Button addNoteButton;
    private KeepReaderDbHelper databaseHelper;
    private TextView noNotesFound;
    private MenuItem searchItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        searchItem = menu.findItem(R.id.search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = KeepReaderDbHelper.getInstance(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);
        addNoteButton = (Button) findViewById(R.id.add_note_button);
        noNotesFound = (TextView) findViewById(R.id.no_notes_found_text_view);

        addNoteButton.setOnClickListener(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)


        mAdapter = new NotesAdapter(this, databaseHelper.getAllNotes());
        mAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String noteTitle, String noteDescription) {
                if (position != RecyclerView.NO_POSITION) {
                    Intent i = new Intent(MainActivity.this, AddedNoteActivity.class);
                    i.putExtra("position", position);
                    i.putExtra("titleResult", noteTitle);
                    i.putExtra("noteDescriptionResult", noteDescription);
                    startActivityForResult(i, DELETE_NOTE_REQUEST);

                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);


        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String queryString = intent.getStringExtra(SearchManager.QUERY);
            Cursor c = databaseHelper.getWordMatches(queryString);

            LinkedList<Note> notesLinkedList = databaseHelper.getQueriedNotes(c);
            mAdapter.setmNotes(notesLinkedList);
            if (notesLinkedList.size() <= 0) {
                noNotesFound.setVisibility(View.VISIBLE);
            } else {

                noNotesFound.setVisibility(View.GONE);
            }


        }


    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

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
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NEW_NOTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.e("MainActivity.class", data.getStringExtra("titleResult"));
                Log.e("MainActivity.class", data.getStringExtra("noteDescriptionResult"));
                Note newNote = new Note(data.getStringExtra("titleResult"), data.getStringExtra("noteDescriptionResult"));
                databaseHelper.addNote(newNote);

                mAdapter.setmNotes(databaseHelper.getAllNotes());
                noNotesFound.setVisibility(View.GONE);
            }
        } else if (requestCode == DELETE_NOTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", -1);
                if (data.getBooleanExtra("update", true) == false) {
                    Log.i(LOG_TAG, "deleted note");

                    databaseHelper.deleteNote(databaseHelper.getAllNotes().get(position));
                    mAdapter.setmNotes(databaseHelper.getAllNotes());
                    noNotesFound.setVisibility(View.GONE);
                } else {
                    String title = data.getStringExtra("titleResult");
                    String description = data.getStringExtra("noteDescriptionResult");
                    Log.i(LOG_TAG, title + description);

                    databaseHelper.updateNote(databaseHelper.getAllNotes().get(position), title, description);
                    mAdapter.setmNotes(databaseHelper.getAllNotes());
                    noNotesFound.setVisibility(View.GONE);
                }

            }
        }
    }
}
