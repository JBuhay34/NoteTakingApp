package com.example.justinbuhay.myownkeep;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static android.R.attr.data;

public class AddedNoteActivity extends AppCompatActivity {

    private FloatingActionButton saveButton;
    private EditText noteTitle;
    private EditText noteDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_note);

        saveButton = (FloatingActionButton) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new saveButtonListener());

        noteTitle = (EditText) findViewById(R.id.titleEditText);
        noteDescription = (EditText) findViewById(R.id.noteEditText);


    }

    private class saveButtonListener implements View.OnClickListener {


        @Override
        public void onClick(View view) {
            Intent returnedInformationIntent = new Intent();

            returnedInformationIntent.putExtra("titleResult", noteTitle.getText().toString());
            returnedInformationIntent.putExtra("noteDescriptionResult", noteDescription.getText().toString());
            setResult(Activity.RESULT_OK, returnedInformationIntent);
            finish();
        }
    }
}
