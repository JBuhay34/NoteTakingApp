package com.example.justinbuhay.myownkeep;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import static android.R.attr.description;

/**
 * Created by justinbuhay on 11/22/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private List<Note> mNotes;
    private Context mContext;

    public NotesAdapter(Context context, List<Note> notes){

        mNotes = notes;
        mContext = context;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView mNoteTitleTextView;
        public TextView mNoteDescriptionTextView;

        public MyViewHolder(View v){
            super(v);
            mNoteTitleTextView = (TextView) v.findViewById(R.id.the_note_title);
            mNoteDescriptionTextView = (TextView) v.findViewById(R.id.the_note_description);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.mNoteTitleTextView.setText(mNotes.get(position).getNoteTitle());
        holder.mNoteDescriptionTextView.setText(mNotes.get(position).getNoteDescription());

    }


    @Override
    public int getItemCount() {
        return mNotes.size();
    }
}
