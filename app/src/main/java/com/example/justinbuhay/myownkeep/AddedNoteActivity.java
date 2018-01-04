package com.example.justinbuhay.myownkeep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.justinbuhay.myownkeep.database.KeepReaderDbHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static com.example.justinbuhay.myownkeep.MainActivity.ADD_THE_IMAGE_REQUEST;
import static com.example.justinbuhay.myownkeep.MainActivity.NOTE_IMAGE_UUID;

public class AddedNoteActivity extends AppCompatActivity {

    private final String LOG_TAG = AddedNoteActivity.class.getName();
    private FloatingActionButton saveButton;
    private EditText noteTitle;
    private EditText noteDescription;
    private int notePosition = -1;
    private KeepReaderDbHelper databaseHelper;
    private ImageView noteImage;
    private String pathForImage;
    private String urlForImage;
    private String theUUID;
    private ProgressBar mImageProgressBar;
    private ImageButton mLeftRotateButton;
    private ImageButton mRightRotateButton;

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
        mImageProgressBar = findViewById(R.id.image_progress_bar);
        mLeftRotateButton = findViewById(R.id.rotate_left_button);
        mRightRotateButton = findViewById(R.id.rotate_right_button);
        noteImage.setVisibility(View.GONE);
        saveButton.setOnClickListener(new saveButtonListener());
        mLeftRotateButton.setOnClickListener(new RotateButtonListener());
        mRightRotateButton.setOnClickListener(new RotateButtonListener());



        noteTitle = findViewById(R.id.titleEditText);
        noteDescription = findViewById(R.id.noteEditText);

        Intent intent = getIntent();
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

        } else if (intent.getIntExtra("requestCode", -1) == ADD_THE_IMAGE_REQUEST) {
            /*intent.getParcelableExtra("bitmap") != null && intent.getStringExtra("DocRefPath") != null*/
            performImageRequestAction(intent);
        }
    }

    private class RotateButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(view == mLeftRotateButton){

                theImage = rotateBitmap(true);

                noteImage.setImageBitmap(theImage);
            } else if(view == mRightRotateButton){

                theImage = rotateBitmap(false);

                noteImage.setImageBitmap(theImage);
            }
        }
    }

    private Bitmap rotateBitmap(boolean isItLeft){

        Matrix matrix = new Matrix();
        if(isItLeft){
            matrix.postRotate(theCurrentRotation + 90);
        } else if (!isItLeft){
            Log.e(LOG_TAG, "rightRotateCalled");
            matrix.postRotate(theCurrentRotation - 90);

        }

        return Bitmap.createBitmap(theImage, 0, 0, theImage.getWidth(), theImage.getHeight(), matrix, true);

    }

    private void performImageRequestAction(Intent intent) {
        theUUID = intent.getStringExtra(NOTE_IMAGE_UUID);
        pathForImage = intent.getStringExtra("DocRefPath");
        mImageProgressBar.setVisibility(View.GONE);
        noteImage.setVisibility(View.VISIBLE);
        Intent data = intent.getParcelableExtra("intentdata");

        Log.e(LOG_TAG, "should be visible");
        Bitmap theBitmap;
        try {
            theBitmap = MediaStore.Images.Media.getBitmap(AddedNoteActivity.this.getContentResolver(), data.getData());
            theImage = getRotatedBitmap(theBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            theImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            noteImage.setImageBitmap(theImage);

            byte[] data1 = baos.toByteArray();
            final String theImageUUID = UUID.randomUUID().toString();
            Log.e(LOG_TAG, theImageUUID + "Let's see");

            final String path = "users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + theImageUUID + ".png";
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference(path);

            UploadTask uploadTask = mStorageReference.putBytes(data1);
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.the_relative_layout),
                    "Uploading Image...", Snackbar.LENGTH_LONG);
            mySnackbar.show();
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e(LOG_TAG, "It didn't work");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    urlForImage = downloadUrl.toString();
                    /*
                    Glide.with(AddedNoteActivity.this)
                            .load(downloadUrl)
                            .listener(new RequestListener<Uri, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    mImageProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .centerCrop()
                            .into(noteImage);
                    */

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private Bitmap getRotatedBitmap(Bitmap bmp) {

        Matrix matrix = new Matrix();
        matrix.postRotate(theCurrentRotation);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getIntExtra("requestCode", -1) == MainActivity.ADD_THE_IMAGE_REQUEST) {
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
                } else if (getIntent().getIntExtra("requestCode", -1) == MainActivity.ADD_THE_IMAGE_REQUEST) {
                    returnedInformationIntent.putExtra("thePath", urlForImage);
                    returnedInformationIntent.putExtra("theUUID", theUUID);
                    setResult(Activity.RESULT_OK, returnedInformationIntent);
                }

                finish();
            }
        }
    }
}
