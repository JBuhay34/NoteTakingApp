package com.example.justinbuhay.myownkeep;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Button addNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);
        addNoteButton = (Button) findViewById(R.id.add_note_button);
        addNoteButton.setOnClickListener(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("Kitty kitty. Head nudges burrow under covers, and pooping rainbow while flying in a toasted bread costume in space, or cough hunt anything that moves, so my left donut is missing, as is my right. I am the best always hungry or curl up and sleep on the freshly laundered towels or jumps off balcony gives owner dead mouse at present then poops in litter box snatches yarn and fights with dog cat chases laser then plays in grass finds tiny spot in cupboard and sleeps all day jumps in bathtub and meows when owner fills food dish the cat knocks over the food dish cat slides down the water slide and into pool and swims even though it does not like water give attitude. Put toy mouse in food bowl run out of litter box at full speed make meme, make cute face shake treat bag, so suddenly go on wild-eyed crazy rampage or spread kitty litter all over house. Dream about hunting birds scream at teh bath human is washing you why halp oh the horror flee scratch hiss bite for run outside as soon as door open for soft kitty warm kitty little ball of furr.");
        linkedList.add("Lick human with sandpaper tongue jumps off balcony gives owner dead mouse at present then poops in litter box snatches yarn and fights with dog cat chases laser then plays in grass finds tiny spot in cupboard and sleeps all day jumps in bathtub and meows when owner fills food dish the cat knocks over the food dish cat slides down the water slide and into pool and swims even though it does not like water. Wake up human for food at 4am stare at guinea pigs, but your pillow is now my pet bed but fall asleep on the washing machine stare at ceiling light.");
        linkedList.add("Thinking longingly about tuna brine when in doubt, wash yet walk on car leaving trail of paw prints on hood and windshield cats secretly make all the worlds muffins licks paws. Behind the couch more napping, more napping all the napping is exhausting. Climb a tree, wait for a fireman jump to fireman then scratch his face cats go for world domination or missing until dinner time open the door, let me out, let me out, let me-out, let me-aow, let meaow, meaow! and always ensure to lay down in such a manner that tail can lightly brush human's nose . Please stop looking at your phone and pet me kick up litter.");
        linkedList.add("Chew iPad power cord stick butt in face claw drapes, so cats are fats i like to pets them they like to meow back, and meow go back to sleep owner brings food and water tries to pet on head, so scratch get sprayed by water because bad cat eat too much then proceed to regurgitate all over living room carpet while humans eat dinner. Meowwww mrow and slap kitten brother with paw or dont wait for the storm to pass, dance in the rain and inspect anything brought into the house. Purr when being pet gnaw the corn cob, so licks paws and claws in your leg. Lie in the sink all day damn that dog roll over and sun my belly under the bed, warm up laptop with butt lick butt fart rainbows until owner yells pee in litter box hiss at cats for catch mouse and gave it as a present chase laser.");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");
        linkedList.add("Hello assafjlafja asdjasd fjasks fjdslsofo pcocosl");


        mAdapter = new NotesAdapter(this, linkedList);
        mRecyclerView.setAdapter(mAdapter);

    }

    public static final int NEW_NOTE_REQUEST = 1;

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.add_note_button:
                Intent i = new Intent(MainActivity.this, AddedNoteActivity.class);
                startActivityForResult(i, NEW_NOTE_REQUEST);
                break;
        }
    }
}
