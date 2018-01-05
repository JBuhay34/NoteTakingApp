package com.example.justinbuhay.myownkeep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.justinbuhay.myownkeep.asynctasks.LoadingImageAyncTaskLoader;

import static com.example.justinbuhay.myownkeep.MainActivity.ADD_THE_IMAGE_REQUEST;
import static com.example.justinbuhay.myownkeep.MainActivity.IMAGE_PATH_FOR_PHOTOS;
import static com.example.justinbuhay.myownkeep.MainActivity.INTENT_DATA;

public class AddedNoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Bitmap> {

    private final String LOG_TAG = AddedNoteActivity.class.getName();
    private FloatingActionButton saveButton;
    private EditText noteTitle;
    private EditText noteDescription;
    private int notePosition = -1;
    private ImageView noteImage;
    private String pathforbitmap;
    private Bitmap bitmap;
    private Uri uriForBitmap;
    private ProgressBar mProgressBar;



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
        mProgressBar = findViewById(R.id.mImageProgressBar);

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

            // Called when the user clicks on an image from their photos.
        } else if (intent.getIntExtra("requestCode", -1) == ADD_THE_IMAGE_REQUEST) {
            noteImage.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            Log.e(LOG_TAG, "should be visible");

            uriForBitmap = Uri.parse(intent.getStringExtra(INTENT_DATA));
            pathforbitmap = intent.getStringExtra(IMAGE_PATH_FOR_PHOTOS);

            getSupportLoaderManager().initLoader(0, null, this).forceLoad();

        }
    }


    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        return new LoadingImageAyncTaskLoader(this, uriForBitmap, pathforbitmap, LOG_TAG);
    }

    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
        bitmap = data;
        mProgressBar.setVisibility(View.GONE);
        noteImage.setImageBitmap(bitmap);
    }

    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {

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
