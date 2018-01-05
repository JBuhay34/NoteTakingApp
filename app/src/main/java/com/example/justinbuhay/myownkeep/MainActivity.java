package com.example.justinbuhay.myownkeep;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.justinbuhay.myownkeep.database.KeepReaderDbHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int NEW_NOTE_REQUEST = 1;
    public static final int DELETE_NOTE_REQUEST = 2;
    public static final String NOTE_TITLE = "notetitle";
    public static final String ACTUAL_NOTE = "actualnote";
    public static final String NOTE_IMAGE_PATH = "noteimagepath";
    public static final String NOTE_IMAGE_UUID = "noteimageuuid";
    public static int SELECT_IMAGE_REQUEST = 3;
    public static int ADD_THE_IMAGE_REQUEST = 4;
    public static String IMAGE_URL = "theURL";
    public static String theUUID = "theUUID";
    private final String LOG_TAG = MainActivity.class.getName();
    private final String noteCollection = "noteCollection";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NotesAdapter mAdapter;
    private Button addNoteButton;
    private ImageButton addImageButton;
    private KeepReaderDbHelper databaseHelper;
    private TextView noNotesFound;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchView searchView;
    private FirebaseFirestore mFireStore;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private DocumentReference mDocumentReference;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private TextView mUserName;
    private ImageView mUserPhoto;
    private LinearLayout mLinearLayout;
    private ProgressBar mProgressBar;

    // This method returns the real path from a uri.
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.length() > 0 && searchView.getWidth() > 0) {

                    doMyOwnSearch(query);

                } else {
                    updateAllNotesIncludingCloud();
                    noNotesFound.setVisibility(View.GONE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0 && searchView.getWidth() > 0) {

                    doMyOwnSearch(newText);
                    Log.e(LOG_TAG, "doMyOwnSearch onquery text");

                } else {
                    updateAllNotesIncludingCloud();
                    noNotesFound.setVisibility(View.GONE);
                }
                return true;
            }
        });


        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void makeToast(String sayThis) {
        Toast.makeText(this, sayThis, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mFireStore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mNavView = findViewById(R.id.navigation_view);
        mLinearLayout = findViewById(R.id.linearLayout);
        mProgressBar = findViewById(R.id.progress_bar);

        View headerLayout = mNavView.getHeaderView(0);
        mUserName = headerLayout.findViewById(R.id.user_name_text_view);
        mUserPhoto = headerLayout.findViewById(R.id.user_image_view);

        setupDrawerContent(mNavView);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle("What's this closed");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("What's this opened");
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_action_name);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });


        databaseHelper = KeepReaderDbHelper.getInstance(this);

        mDocumentReference = mFireStore.document("users/" + mFirebaseAuth.getCurrentUser().getUid());
        mStorageReference = mFirebaseStorage.getReference();

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        updateAllNotesIncludingCloud();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        mRecyclerView = findViewById(R.id.notes_recycler_view);
        addNoteButton = findViewById(R.id.add_note_button);
        addImageButton = findViewById(R.id.camera_action_button);
        noNotesFound = findViewById(R.id.no_notes_found_text_view);

        addNoteButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);

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
            public void onItemClick(View view, int position, String noteTitle, String noteDescription, String notePath) {
                if (position != RecyclerView.NO_POSITION) {
                    Intent i = new Intent(MainActivity.this, AddedNoteActivity.class);
                    i.putExtra("position", position);
                    i.putExtra("titleResult", noteTitle);
                    i.putExtra("noteDescriptionResult", noteDescription);
                    i.putExtra("thePictureURL", notePath);
                    startActivityForResult(i, DELETE_NOTE_REQUEST);

                }
            }
        });


        updateAllNotesIncludingCloud();

        mRecyclerView.setAdapter(mAdapter);


    }

    private void setupDrawerContent(NavigationView navigationView) {

        if (mFirebaseAuth.getCurrentUser() != null) {
            mUserName.setText(mFirebaseAuth.getCurrentUser().getEmail());
            Uri userPhoto = mFirebaseAuth.getCurrentUser().getPhotoUrl();
            Glide.with(this).load(userPhoto).transform(new CircleTransform(this)).override(200, 200).into(mUserPhoto);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                }
        );
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.sign_out_navigation:
                makeToast("Sign out clicked!!!");
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                intent.putExtra("signout", 5);
                startActivity(intent);
                finish();
                break;
            case R.id.notes_navigation:
                makeToast("notes nav clicked");
                break;
            case R.id.settings_navigation:
                makeToast("settings nav clicked");
                break;
        }
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void doMyOwnSearch(String queryString) {
        Cursor c = databaseHelper.getWordMatches(queryString);

        LinkedList<Note> notesLinkedList = databaseHelper.getQueriedNotes(c);

        mAdapter.setmNotes(notesLinkedList);
        if (notesLinkedList.size() <= 0) {
            noNotesFound.setVisibility(View.VISIBLE);
        } else {
            noNotesFound.setVisibility(View.GONE);
        }

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // The add note button at bottom of Main is clicked.
            case R.id.add_note_button:
                Intent i = new Intent(MainActivity.this, AddedNoteActivity.class);
                startActivityForResult(i, NEW_NOTE_REQUEST);
                break;
            case R.id.camera_action_button:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_THE_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                makeToast("Didn't work");
            } else {
                addImageButton.setEnabled(true);
                mLinearLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                Log.e("MainActivity.class", data.getStringExtra("titleResult"));
                Log.e("MainActivity.class", data.getStringExtra("noteDescriptionResult"));
                final String titleResult = data.getStringExtra("titleResult");
                final String noteDescription = data.getStringExtra("noteDescriptionResult");
                final String noteImagePath = data.getStringExtra("theURL");
                final String noteImageUUID = data.getStringExtra("theUUID");

                Map<String, Object> noteToAdd = new HashMap<String, Object>();
                noteToAdd.put(NOTE_TITLE, titleResult);
                noteToAdd.put(ACTUAL_NOTE, noteDescription);
                noteToAdd.put(NOTE_IMAGE_PATH, noteImagePath);
                noteToAdd.put(NOTE_IMAGE_UUID, noteImageUUID);

                mFireStore.collection("users").document(mFirebaseAuth.getCurrentUser().getUid()).collection(noteCollection).add(noteToAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        noNotesFound.setVisibility(View.GONE);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error adding document", e);
                    }
                });

                updateAllNotesIncludingCloud();
            }
            // When the user creates a new note with an image, getCameraImage is called
        } else if (requestCode == SELECT_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                makeToast("Cancelled");
            } else {
                getCameraImage(data);
            }
            // When the user creates a new note without an image.
        } else if (requestCode == NEW_NOTE_REQUEST) {
            if (resultCode == RESULT_OK) {

                final String titleResult = data.getStringExtra("titleResult");
                final String noteDescription = data.getStringExtra("noteDescriptionResult");

                Map<String, Object> noteToAdd = new HashMap<String, Object>();
                noteToAdd.put(NOTE_TITLE, titleResult);
                noteToAdd.put(ACTUAL_NOTE, noteDescription);

                mFireStore.collection("users").document(mFirebaseAuth.getCurrentUser().getUid()).collection(noteCollection).add(noteToAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        noNotesFound.setVisibility(View.GONE);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error adding document", e);
                    }
                });

                updateAllNotesIncludingCloud();
            }
        } else if (requestCode == DELETE_NOTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", -1);
                // This is resulted when the user deletes a note
                if (data.getBooleanExtra("update", true) == false) {

                    String uniqueStorageID = mAdapter.getmNotes().get(position).getUniqueStorageID();
                    makeToast("\"" + mAdapter.getmNotes().get(position).getNoteTitle() + "\" deleted");

                    mDocumentReference.collection(noteCollection).document(uniqueStorageID).delete();
                    if (mAdapter.getmNotes().get(position).getNoteImageUUID() != null) {
                        mFirebaseStorage.getReference().child("users/" + mFirebaseAuth.getCurrentUser().getUid() + "/" + mAdapter.getmNotes().get(position).getNoteImageUUID() + ".png").delete();
                    }

                    updateAllNotesIncludingCloud();
                    noNotesFound.setVisibility(View.GONE);

                    //This is resulted when the user updates the note.
                } else {
                    String title = data.getStringExtra("titleResult");
                    String description = data.getStringExtra("noteDescriptionResult");
                    Log.i(LOG_TAG, title + description);


                    Map<String, Object> noteToAdd = new HashMap<String, Object>();
                    noteToAdd.put(NOTE_TITLE, title);
                    noteToAdd.put(ACTUAL_NOTE, description);


                    mDocumentReference.collection(noteCollection).document(databaseHelper.getAllNotes().get(position).getUniqueStorageID()).update(noteToAdd);
                    updateAllNotesIncludingCloud();
                    noNotesFound.setVisibility(View.GONE);
                }

            }
        }
    }

    private void getCameraImage(Intent data) {
        if (data != null) {


            final String theImageUUID = UUID.randomUUID().toString();

            String path = "users/" + mFirebaseAuth.getCurrentUser().getUid() + "/" + theImageUUID + ".png";

            String actualImageFilePath = getPath(MainActivity.this, data.getData());

            Intent intent = new Intent(MainActivity.this, AddedNoteActivity.class);
            intent.putExtra("intentdata", data);
            intent.putExtra("DocRefPath", path);
            intent.putExtra("ActualImagePath", actualImageFilePath);
            intent.putExtra(NOTE_IMAGE_UUID, theImageUUID);
            startActivityForResult(intent, ADD_THE_IMAGE_REQUEST);


        }

    }

    // This method should sync all of the notes that are both in the SQLiteDatabase and the notes in Firebase FireStore
    private void updateAllNotesIncludingCloud() {

        // the get() method obtains a task array with all of the data in firebase.
        mDocumentReference.collection(noteCollection).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    List<Note> notesOnDatabase = databaseHelper.getAllNotes();
                    List<Note> notesOnFireStore = new LinkedList<Note>();

                    // This for-each loop goes through all of the notes on the firestore.
                    for (DocumentSnapshot document : task.getResult()) {

                        // The note listed on the firestore.
                        Note newNote2;
                        if (document.getString(NOTE_IMAGE_PATH) != null) {
                            newNote2 = new Note(document.getString("notetitle"), document.getString("actualnote"), document.getId(), document.getString(NOTE_IMAGE_PATH), document.getString(NOTE_IMAGE_UUID));
                        } else {
                            newNote2 = new Note(document.getString("notetitle"), document.getString("actualnote"), document.getId());
                        }
                        notesOnFireStore.add(newNote2);

                        boolean isItThere = false;

                        int position = 0;

                        // This for-each loop goes through all the notes on the database
                        for (Note theNote : notesOnDatabase) {

                            if (theNote.getUniqueStorageID().equals(newNote2.getUniqueStorageID())) {
                                isItThere = true;
                                if (notesOnDatabase.size() != 0) {
                                    if (theNote != null && (!theNote.getNoteTitle().equals(newNote2.getNoteTitle()) || !theNote.getNoteDescription().equals(newNote2.getNoteDescription()))) {
                                        Log.e(LOG_TAG, position + " was changed");
                                        databaseHelper.updateNote(databaseHelper.getAllNotes().get(position), newNote2.getNoteTitle(), newNote2.getNoteDescription());
                                    }
                                }

                            }

                            position++;

                        }

                        if (!isItThere) {
                            databaseHelper.addNote(newNote2);
                        }

                    }

                    for (int i = 0; i < notesOnDatabase.size(); i++) {
                        boolean isItThere2 = false;

                        for (Note note : notesOnFireStore) {
                            if (notesOnDatabase.get(i).getUniqueStorageID().equals(note.getUniqueStorageID())) {
                                isItThere2 = true;

                            }
                        }

                        if (!isItThere2) {
                            databaseHelper.deleteNote(notesOnDatabase.get(i));
                        }
                    }

                    mAdapter.setmNotes(databaseHelper.getAllNotes());

                } else {
                    Log.e(LOG_TAG, "Error getting documents: ", task.getException());
                }
            }

        });

    }
}
