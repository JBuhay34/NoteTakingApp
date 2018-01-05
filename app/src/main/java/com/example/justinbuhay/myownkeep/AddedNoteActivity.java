package com.example.justinbuhay.myownkeep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.example.justinbuhay.myownkeep.HelperMethods.modifyOrientation;
import static com.example.justinbuhay.myownkeep.MainActivity.ADD_THE_IMAGE_REQUEST;
import static com.example.justinbuhay.myownkeep.MainActivity.IMAGE_PATH_FOR_PHOTOS;
import static com.example.justinbuhay.myownkeep.MainActivity.INTENT_DATA;

public class AddedNoteActivity extends AppCompatActivity {

    private final String LOG_TAG = AddedNoteActivity.class.getName();
    private FloatingActionButton saveButton;
    private EditText noteTitle;
    private EditText noteDescription;
    private int notePosition = -1;
    private ImageView noteImage;
    private String pathForImage;
    private String theUUID;
    private byte[] dataImage;

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
        // Called when the user clicks on an view from the MainActivity.
        if (intent.getStringExtra("titleResult") != null && intent.getStringExtra("noteDescriptionResult") != null) {
            noteTitle.setText(intent.getStringExtra("titleResult"), TextView.BufferType.EDITABLE);
            noteDescription.setText(intent.getStringExtra("noteDescriptionResult"), TextView.BufferType.EDITABLE);
            notePosition = intent.getIntExtra("position", -1);
            if (intent.getStringExtra("thePictureURL") != null) {
                noteImage.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(intent.getStringExtra("thePictureURL"))
                        .centerCrop()
                        .into(noteImage);
            }

            //
        } else if (intent.getIntExtra("requestCode", -1) == ADD_THE_IMAGE_REQUEST) {
            noteImage.setVisibility(View.VISIBLE);
            Log.e(LOG_TAG, "should be visible");

            //TODO set this up on an async task, because too much info is being loaded on main thread.

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddedNoteActivity.this.getContentResolver(), Uri.parse(intent.getStringExtra(INTENT_DATA)));
                String pathforbitmap = intent.getStringExtra(IMAGE_PATH_FOR_PHOTOS);

                Log.e(LOG_TAG, "this is the path" + pathforbitmap);
                Bitmap orientedBitmap = null;

                orientedBitmap = modifyOrientation(LOG_TAG, bitmap, pathforbitmap.toString());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                orientedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                noteImage.setImageBitmap(orientedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


            //Uri bitmapuri = Uri.parse(intent.getStringExtra(NOTE_IMAGE_BITMAP));
            //noteImage.setImageURI(bitmapuri);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getIntExtra("requestCode", -1) == ADD_THE_IMAGE_REQUEST) {
            FirebaseStorage.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + theUUID + ".png").delete();
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
                } else if (getIntent().getIntExtra("requestCode", -1) == ADD_THE_IMAGE_REQUEST) {

                    setResult(Activity.RESULT_OK, returnedInformationIntent);
                }

                finish();
            }
        }
    }
}
