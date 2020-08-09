package com.example.kshitijverma.beat_my_recipie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.varunest.sparkbutton.SparkButton;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import Model.User;
import Model.likes;
import Model.post;

public class detailed_post_description extends AppCompatActivity {
    TextView post_composer_name_detail;
    ImageView post_composer_image_detail;

    ImageView dish_pic_post;
    private String PostId;
    private String authorId;
    private String Image_url;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference userdatabasereference;
    SparkButton like_dish;
    TextView no_of_likes;
    ImageView comment_dish;
    String postId_shared_with_notification;
    TextView comment_count;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    private Context mContext;
    Toolbar toolbar;
    TextView chef_bio;
    TextView steps;
    TextView ingredients;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        mContext=getApplicationContext();
        firebaseUser=mAuth.getCurrentUser();
        setContentView(R.layout.activity_detailed_post_description);
        post_composer_image_detail=findViewById(R.id.post_composer_image_detail);
        post_composer_name_detail=findViewById(R.id.post_composer_name_detail);
        dish_pic_post=findViewById(R.id.dish_pic_post);
        Intent intent=getIntent();
        toolbar=findViewById(R.id.detail_post_description_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        postId_shared_with_notification=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).getString("postId","none");
        PostId=intent.getStringExtra("postId");
        authorId=intent.getStringExtra("authorId");
        like_dish=findViewById(R.id.dish_like_xml);
        no_of_likes=findViewById(R.id.dish_likesCount_xml);
        comment_dish=findViewById(R.id.dish_comment_xml);
        comment_count=findViewById(R.id.dish_comment_count);
        chef_bio=findViewById(R.id.chef_note_detailPost);
        ingredients=findViewById(R.id.ingredients_detailPost);
        steps=findViewById(R.id.steps_detailPost);
        userdatabasereference=FirebaseDatabase.getInstance().getReference().child("users");
        //databaseReference=FirebaseDatabase.getInstance().getReference().child("posts").child(PostId);
        databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference2=databaseReference.child("image_url");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("posts").child(PostId).exists()) {


                post post = snapshot.child("posts").child(PostId).getValue(Model.post.class);
                Image_url = post.getimage_url().toString();
                final Context context = getApplicationContext();
                if (snapshot.child("users").child(post.getUser_id()).exists()) {
                    User user = snapshot.child("users").child(post.getUser_id()).getValue(User.class);
                    if (snapshot.child("users").child(post.getUser_id()).child("image_url").exists()) {
                        post_composer_name_detail.setText(user.getName());
                        if (isValidContextForGlide(context)) {
                            Glide.with(context)
                                    .load(user.getImage_url())
                                    .into(post_composer_image_detail);
                        }
                    }

                }
                chef_bio.setText(post.getChef_note());
                steps.setText(post.getSteps());
                ingredients.setText(post.getIngredients());

                Log.i("Image URL in DEtain", Image_url);
                if (isValidContextForGlide(mContext)) {
                    Glide.with(context).load(Image_url).into(dish_pic_post);
                }

                isLiked(PostId, like_dish);
                noOfLikes(PostId, no_of_likes);
                getComments(PostId, comment_count);
                like_dish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (like_dish.getTag().equals("Like")) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            String Notification_Id = ref.push().getKey();
                            like_dish.playAnimation();
                            HashMap<String, Object> map = new HashMap<>();
                            map.clear();
                            map.put("hasLiked", true);
                            map.put("notification_id", Notification_Id);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(PostId).
                                    child(mAuth.getCurrentUser().getUid()).setValue(map);
                            addNotification(PostId, authorId, 'L', Notification_Id);
                            //like_dish.playAnimation();
                            //FirebaseDatabase.getInstance().getReference().child("Likes").child(PostId).child(mAuth.getCurrentUser().getUid()).setValue(true);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(PostId).
                                    child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        likes likes = snapshot.getValue(Model.likes.class);
                                        String notification_id = likes.getNotification_id();
                                        if (notification_id != null) {
                                            removeNotification(PostId, authorId, 'L', notification_id);
                                            //
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            //FirebaseDatabase.getInstance().getReference().child("Likes").child(PostId).child(mAuth.getCurrentUser().getUid()).removeValue();
                        }
                    }
                });
                comment_dish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toCOmmentIntent = new Intent(mContext, CommentsActivity.class);
                        toCOmmentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        toCOmmentIntent.putExtra("postId", PostId);
                        toCOmmentIntent.putExtra("authorId", authorId);
                        mContext.startActivity(toCOmmentIntent);
                    }
                });
                comment_count.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toCOmmentIntent = new Intent(mContext, CommentsActivity.class);
                        toCOmmentIntent.putExtra("postId", PostId);
                        toCOmmentIntent.putExtra("authorId", authorId);
                        mContext.startActivity(toCOmmentIntent);
                    }
                });
            }
        }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getComments(String postId, final TextView comment_count) {
        FirebaseDatabase.getInstance().getReference().child("comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comment_count.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void noOfLikes(String postId, final TextView no_of_likes) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child((postId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                no_of_likes.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(String postId, final SparkButton like_dish) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(firebaseUser!=null) {
                    if (snapshot.child(mAuth.getCurrentUser().getUid()).exists()) {
                        like_dish.setChecked(true);
                        like_dish.setTag("Liked");
                    } else {
                        like_dish.setChecked(false);
                        like_dish.setTag("Like");
                    }
                }
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
    public void addNotification(String post_Id, String user_Id, char ch, String Notification_Id) {
        Date c = Calendar.getInstance().getTime();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("text", "Liked your Post");
        map.put("postId", post_Id);
        map.put("isPost", true);
        map.put("date", c);
        map.put("notification_id", Notification_Id);

        // made chenges in the line below. Used "user_id" instead of FirebaseAuth.get.....getUid()
        FirebaseDatabase.getInstance().getReference().child("notifications").
                child(user_Id).child(Notification_Id).setValue(map);

    }
    public void removeNotification(final String post_Id, final String user_Id, char ch, final String notification_id) {
        // user_id is the composer of the post, to whom notification was sent.
        FirebaseDatabase.getInstance().getReference().child("notifications").child(user_Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(notification_id).exists()) {
                    FirebaseDatabase.getInstance().getReference().child("notifications").child(user_Id).child(notification_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post_Id).child(mAuth.getCurrentUser().getUid()).removeValue();

                        }
                    });
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post_Id).child(mAuth.getCurrentUser().getUid()).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    @Override
    public void onBackPressed()
    {
        Intent toHomeIntent=new Intent(detailed_post_description.this,MainActivity.class);
        startActivity(toHomeIntent);
        finish();
    }
}
