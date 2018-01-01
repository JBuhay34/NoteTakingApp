package com.example.justinbuhay.myownkeep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.justinbuhay.myownkeep.database.KeepReaderDbHelper;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import static com.example.justinbuhay.myownkeep.MainActivity.IMAGE_URL;
import static com.example.justinbuhay.myownkeep.MainActivity.theUUID;

public class AddedNoteActivity extends AppCompatActivity {

    private final String LOG_TAG = AddedNoteActivity.class.getName();
    private FloatingActionButton saveButton;
    private EditText noteTitle;
    private EditText noteDescription;
    private int notePosition = -1;
    private KeepReaderDbHelper databaseHelper;
    private ImageView noteImage;
    private String pathForImage;

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
        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        saveButton = findViewById(R.id.save_button);
        noteImage = findViewById(R.id.note_image_view);
        noteImage.setVisibility(View.GONE);
        saveButton.setOnClickListener(new saveButtonListener());

        noteTitle = findViewById(R.id.titleEditText);
        noteDescription = findViewById(R.id.noteEditText);

        Intent intent = getIntent();
        if (intent.getStringExtra("titleResult") != null && intent.getStringExtra("noteDescriptionResult") != null) {
            noteTitle.setText(intent.getStringExtra("titleResult"), TextView.BufferType.EDITABLE);
            noteDescription.setText(intent.getStringExtra("noteDescriptionResult"), TextView.BufferType.EDITABLE);
            notePosition = intent.getIntExtra("position", -1);

        } else if (intent.getStringExtra(IMAGE_URL) != null && intent.getStringExtra(theUUID) != null) {
            noteImage.setVisibility(View.VISIBLE);
            pathForImage = "users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + intent.getStringExtra(theUUID) + ".png";
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(FirebaseStorage.getInstance().getReference(pathForImage))
                    .into(noteImage);
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
                } else if (getIntent().getIntExtra("requestCode", -1) == MainActivity.ADD_THE_IMAGE_REQUEST) {
                    returnedInformationIntent.putExtra("thePath", pathForImage);
                }

                finish();
            }
        }
        }

}
