package com.example.justinbuhay.myownkeep;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.justinbuhay.myownkeep.database.KeepReaderDbHelper;

import static android.R.attr.data;

public class AddedNoteActivity extends AppCompatActivity {

    private final String LOG_TAG = AddedNoteActivity.class.getName();
    private FloatingActionButton saveButton;
    private EditText noteTitle;
    private EditText noteDescription;
    private int notePosition = -1;
    private KeepReaderDbHelper databaseHelper;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        Intent intent = getIntent();
        if (intent.getStringExtra("titleResult") != null && intent.getStringExtra("noteDescriptionResult") != null)
            inflater.inflate(R.menu.edit_note_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:

                if (notePosition != -1) {

                    Intent i = new Intent();
                    i.putExtra("position", notePosition);
                    i.putExtra("update", false);
                    setResult(RESULT_OK, i);
                    finish();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_note);

        saveButton = (FloatingActionButton) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new saveButtonListener());

        noteTitle = (EditText) findViewById(R.id.titleEditText);
        noteDescription = (EditText) findViewById(R.id.noteEditText);

        Intent intent = getIntent();
        if (intent.getStringExtra("titleResult") != null && intent.getStringExtra("noteDescriptionResult") != null) {
            noteTitle.setText(intent.getStringExtra("titleResult"), TextView.BufferType.EDITABLE);
            noteDescription.setText(intent.getStringExtra("noteDescriptionResult"), TextView.BufferType.EDITABLE);
            notePosition = intent.getIntExtra("position", -1);

        }
    }

    private class saveButtonListener implements View.OnClickListener {


        @Override
        public void onClick(View view) {
            if (view == saveButton) {
                Intent returnedInformationIntent = new Intent();

                returnedInformationIntent.putExtra("titleResult", noteTitle.getText().toString());
                returnedInformationIntent.putExtra("noteDescriptionResult", noteDescription.getText().toString());
                if (getIntent().getIntExtra("requestCode", -1) == MainActivity.NEW_NOTE_REQUEST) {
                    setResult(Activity.RESULT_OK, returnedInformationIntent);
                } else if (getIntent().getIntExtra("requestCode", -1) == MainActivity.DELETE_NOTE_REQUEST) {
                    returnedInformationIntent.putExtra("position", notePosition);
                    returnedInformationIntent.putExtra("update", true);
                    setResult(Activity.RESULT_OK, returnedInformationIntent);
                }
                finish();
            }
        }
        }

}
