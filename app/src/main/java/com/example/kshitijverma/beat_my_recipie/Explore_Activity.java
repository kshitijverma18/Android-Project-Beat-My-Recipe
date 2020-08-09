package com.example.kshitijverma.beat_my_recipie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.firebase.database.FirebaseDatabase;

public class Explore_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private explore_recycler_adapter adapter;
    ImageButton explore_toHome;
    ImageButton explore_search;
    EditText explore_editsearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        explore_toHome=(ImageButton)findViewById(R.id.explore_toHome_xml);
        explore_editsearch=(EditText)findViewById(R.id.explore_editsearch_xml);
        explore_editsearch.setVisibility(View.GONE);
        explore_search=(ImageButton)findViewById(R.id.explore_search_xml);
        recyclerView=(RecyclerView)this.findViewById(R.id.explore_recycler_view_activity_xml);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);

        FirebaseRecyclerOptions<explore_class> options =
                new FirebaseRecyclerOptions.Builder<explore_class>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("explore_adapter_class"), explore_class.class)
                        .build();

        adapter=new explore_recycler_adapter(options);
        recyclerView.setAdapter(adapter);
        explore_toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explore_toHomeIntent=new Intent(Explore_Activity.this,MainActivity.class);
                startActivity(explore_toHomeIntent);
            }
        });
        explore_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explore_editsearch.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}