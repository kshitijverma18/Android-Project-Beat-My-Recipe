package com.example.kshitijverma.beat_my_recipie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Adapters.photoAdapter;
import Model.User;
import Model.comment;
import Model.post;

public class user_profile extends AppCompatActivity {
    ImageButton toHOme_from_profile;
    TextView profile_name_userprofile;
    RecyclerView recyclerView;
    private photoAdapter photoAdapter;
    private List<post> myPhotoList;
    String profileId;
    private FirebaseUser fuser;
    DatabaseReference databaseReference;
    ImageView user_profile_image_yourprofile;
    Toolbar toolbar;
    Button edit_profile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profile_name_userprofile=findViewById(R.id.profile_name_userprofile);
        user_profile_image_yourprofile=findViewById(R.id.user_profile_image_yourprofile);
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        final Context context=getApplicationContext();
        toolbar=findViewById(R.id.user_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edit_profile=findViewById(R.id.edit_profile_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHomeIntent=new Intent(user_profile.this,MainActivity.class);
                startActivity(toHomeIntent);
                Log.i("checking finish","checking finish");
                finish();
            }
        });
        databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(snapshot.child("image_url").exists())
                {
                    profile_name_userprofile.setText(user.getName());
                    if (isValidContextForGlide(context))
                    {
                        Glide.with(context)
                                .load(user.getImage_url())
                                .into(user_profile_image_yourprofile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String data=this.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId","none");
        if(data.equals("none"))
        {
            profileId=fuser.getUid();
        }
        else
        {
            profileId=data;
        }

        recyclerView=findViewById(R.id.user_profile_recyclerVIew);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        myPhotoList=new ArrayList<>();
        photoAdapter=new photoAdapter(this,myPhotoList);
        recyclerView.setAdapter(photoAdapter);


        myPhotos();
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAccountSettingsIntent=new Intent(user_profile.this,SetupAccountActivity.class);
                startActivity(toAccountSettingsIntent);
                finish();
            }
        });
    }
    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoList.clear();
                for(DataSnapshot snap:snapshot.getChildren())
                {
                    post post=snap.getValue(Model.post.class);
                    // note that in the video, here it was post.hetProfileId in the line below.
                    if(post.getUser_id().equals(profileId))
                    {
                        myPhotoList.add(post);
                    }
                }
                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void onBackPressed()
    {
        Intent toHomeIntent=new Intent(user_profile.this,MainActivity.class);
        startActivity(toHomeIntent);
        finish();
    }

}
