package com.example.kshitijverma.beat_my_recipie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Adapters.commentAdapter;
import Model.User;
import Model.comment;
import Model.post;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private commentAdapter commentAdapter;
    private List<comment> commentList;
    private EditText addcomment;
    private ImageView user_image_commentSEction;
    ImageButton commentSection_toHome;
    FirebaseUser firebaseUser;
    TextView post_comment;
    private String PostId;
    private String authorID;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar=findViewById(R.id.comment_section_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        context=getApplicationContext();
        recyclerView=findViewById(R.id.comment_activity_recyclerView_xml);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent=getIntent();
        PostId=intent.getStringExtra("postId");
        authorID=intent.getStringExtra("authorId"); // author/composer of the post.
        commentList=new ArrayList<>();
        commentAdapter=new commentAdapter(this,commentList,PostId);
        addcomment=findViewById(R.id.add_comment_xml);
        user_image_commentSEction=findViewById(R.id.profile_image_commentSection_xml);
        post_comment=findViewById(R.id.add_new_comment_textview_xml);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        getUserImage();
        recyclerView.setAdapter(commentAdapter);
        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(post_comment.getText().toString()))
                {
                    Toast.makeText(CommentsActivity.this,"No COmment Added",Toast.LENGTH_LONG).show();
                }
                else
                {
                    putComment();
                }
            }
        });
        getComment();
    }

    private void getComment() {
        FirebaseDatabase.getInstance().getReference().child("comments").child(PostId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot snap:snapshot.getChildren())
                {
                    comment comment=snap.getValue(Model.comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putComment() {

        HashMap<String,Object> map=new HashMap<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("comments").child(PostId);
        final String notification_id=ref.push().getKey();
        Date c= Calendar.getInstance().getTime();
        map.put("notification_id",notification_id);
        map.put("comment",addcomment.getText().toString());
        map.put("publisher",firebaseUser.getUid()); // publisher of the comment.
        map.put("date",c);
        map.put("postOwner",authorID);
        ref.child(notification_id).
                setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    addcomment.setText("");
                    addNotification(PostId,authorID,'L',notification_id);
                    Toast.makeText(CommentsActivity.this,"Comment Added",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(CommentsActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getUserImage() {
        // This Function is not yet complete because users have not been created in database.
        // VIdeo tutorial : 28. TIme-20 mins from starting.
        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(snapshot.child("image_url").exists())
                {
                    if (isValidContextForGlide(context))
                    {
                        Glide.with(context)
                                .load(user.getImage_url())
                                .into(user_image_commentSEction);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }


public void addNotification(String post_Id, String authorID,char ch,String notification_id)
{
    HashMap<String,Object>map=new HashMap<>();
    Date c= Calendar.getInstance().getTime();
    map.put("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());
    map.put("text","Commented on your Post");
    map.put("postId",post_Id);
    map.put("isPost",true);
    map.put("date",c);
    map.put("notification_id",notification_id);

    // made chenges in the line below. Used "user_id" instead of FirebaseAuth.get.....getUid()
    FirebaseDatabase.getInstance().getReference().child("notifications").
            child(authorID).child(notification_id).setValue(map);

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
}
