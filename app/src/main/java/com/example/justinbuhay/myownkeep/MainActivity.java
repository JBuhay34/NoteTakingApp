package com.example.justinbuhay.myownkeep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");


        mAdapter = new NotesAdapter(this, linkedList);
        mRecyclerView.setAdapter(mAdapter);
    }
}
