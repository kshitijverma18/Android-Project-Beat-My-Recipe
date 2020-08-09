package com.example.kshitijverma.beat_my_recipie;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.favouriteAdapter;
import Model.favourite;


public class Your_Favourites extends Fragment {
    private RecyclerView recyclerView;
    private List<favourite> mFavouriteList;
    private favouriteAdapter favouriteAdapter;
    private Context mContext;

    public Your_Favourites() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your__favourites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mContext=getActivity();
        recyclerView=getView().findViewById(R.id.favourite_recyclerView);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        mFavouriteList = new ArrayList<>();
        favouriteAdapter=new favouriteAdapter(mContext,mFavouriteList);
        recyclerView.setAdapter(favouriteAdapter);
        readFavourites();
    }

    private void readFavourites() {
        FirebaseDatabase.getInstance().getReference().child("favourites").child(FirebaseAuth.getInstance().
                getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    favourite favourite=snap.getValue(Model.favourite.class);
                    mFavouriteList.add(favourite);
                }
                favouriteAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
