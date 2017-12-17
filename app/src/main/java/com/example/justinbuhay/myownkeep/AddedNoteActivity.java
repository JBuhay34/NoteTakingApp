package com.example.justinbuhay.myownkeep;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddedNoteActivity extends AppCompatActivity {

    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_note);

        saveButton = (FloatingActionButton) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new saveButtonListener());


    }

    private class saveButtonListener implements View.OnClickListener {


        @Override
        public void onClick(View view) {

        }
    }
}
