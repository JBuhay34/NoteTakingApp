package com.justlive.justinbuhay.myownkeep;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by justinbuhay on 11/22/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private List<Note> mNotes;
    private Context mContext;
    private OnItemClickListener mListener;

    public NotesAdapter(Context context, List<Note> notes){

        mNotes = notes;
        mContext = context;

    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
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

        holder.mNoteTitleTextView.setVisibility(View.VISIBLE);
        holder.mNoteDescriptionTextView.setVisibility(View.VISIBLE);
        String noteTitle = mNotes.get(position).getNoteTitle();
        String noteDescription = mNotes.get(position).getNoteDescription();

        if ((noteTitle.equals("") || noteTitle == null)) {
            holder.mNoteTitleTextView.setVisibility(View.GONE);
        }
        if ((noteDescription.equals("") || noteDescription == null)) {
            holder.mNoteDescriptionTextView.setVisibility(View.GONE);
        }

        holder.mNoteTitleTextView.setText(noteTitle);
        holder.mNoteDescriptionTextView.setText(noteDescription);
        if (mNotes.get(position).getNotePath() != null && mNotes.get(position).getNoteImageUUID() != null) {
            Glide.with(mContext).load(mNotes.get(position).getNotePath()).override(800, 800).into(holder.mNoteImageView);
            holder.mNoteImageView.setVisibility(View.VISIBLE);
        } else {
            holder.mNoteImageView.setVisibility(View.GONE);
        }

    }

    public List<Note> getmNotes() {
        return mNotes;
    }

    public void setmNotes(List<Note> mNotes) {
        this.mNotes = mNotes;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, String noteTitle, String noteDescription, String notePath);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mNoteTitleTextView;
        public TextView mNoteDescriptionTextView;
        public ImageView mNoteImageView;

        public MyViewHolder(View v) {
            super(v);

            mNoteTitleTextView = v.findViewById(R.id.the_note_title);
            mNoteDescriptionTextView = v.findViewById(R.id.the_note_description);
            mNoteImageView = v.findViewById(R.id.image_for_list_item);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(itemView, position, mNoteTitleTextView.getText().toString(), mNoteDescriptionTextView.getText().toString(), mNotes.get(position).getNotePath());
                        }
                    }
                }
            });
        }
    }
}
