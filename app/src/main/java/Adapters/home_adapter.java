package Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kshitijverma.beat_my_recipie.CommentsActivity;
import com.example.kshitijverma.beat_my_recipie.R;
import com.example.kshitijverma.beat_my_recipie.detailed_post_description;
import com.example.kshitijverma.beat_my_recipie.explore_class;
import com.example.kshitijverma.beat_my_recipie.explore_recycler_adapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.varunest.sparkbutton.SparkButton;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import Model.User;
import Model.comment;
import Model.likes;
import Model.notification;
import Model.
        post;


public class home_adapter extends FirebaseRecyclerAdapter<post, home_adapter.home_viewHolder> {
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private Context mContext;
    DatabaseReference databaseReference;
    String user_image="";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(Date date) {
        long time = date.getTime();
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate().getTime();
        if (time > now || time <= 0) {
            return "in the future";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
    public home_adapter(@NonNull FirebaseRecyclerOptions<post> options, Context mContext) {
        super(options);
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    @Override
    public void onDataChanged()
    {

    }

    @Override
    protected void onBindViewHolder(@NonNull final home_adapter.home_viewHolder holder, final int position, @NonNull final post post) {
        Log.i("image URL", post.getimage_url());
        holder.progressBar.setVisibility(View.VISIBLE);
        post_composer_info(holder, post.getPost_id(),holder.progressBar);
        getTitle(holder.chef_title,holder.chef_title_image);
        if(isValidContextForGlide(mContext))
        {
            Glide.with(mContext).load(post.getimage_url()).into(holder.post_dish_image);
        }

        isLiked(post.getPost_id(), holder.like_dish);
        isAddedtoFavourite(post.getPost_id(),holder.favourites_button);
        noOfLikes(post.getPost_id(), holder.likes_count);
        getComments(post.getPost_id(), holder.comment_count);
        holder.timestamp.setText(getTimeAgo(post.getDate()));

        holder.like_dish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (holder.like_dish.getTag().equals("Like")) {

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    String Notification_Id = ref.push().getKey();
                    holder.like_dish.playAnimation();
                    HashMap<String, Object> map = new HashMap<>();
                    map.clear();
                    map.put("hasLiked", true);


                    //FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPost_id()).
                     //child(mAuth.getCurrentUser().getUid()).child("hasLiked").setValue(true);

                    map.put("notification_id", Notification_Id);
                    DatabaseReference likesReference=FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPost_id());
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPost_id()).
                            child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPost_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    FirebaseDatabase.getInstance().getReference().child("posts").child(post.getPost_id()).child("popularity").setValue(-1*(snapshot.getChildrenCount())+"");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                    addNotification(post.getPost_id(), post.getUser_id(), 'L', Notification_Id);



                } else {
                   // FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPost_id()).child(mAuth.getCurrentUser().getUid()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPost_id()).
                            child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                likes likes = snapshot.getValue(Model.likes.class);
                                String notification_id = likes.getNotification_id();
                                if (notification_id != null) {
                                    removeNotification(post.getPost_id(), post.getUser_id(), 'L', notification_id);
                                    //
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });

                    //FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPost_id()).child(mAuth.getCurrentUser().getUid()).removeValue();

                }



            }


        });

        holder.comment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCOmmentIntent = new Intent(mContext, CommentsActivity.class);
                toCOmmentIntent.putExtra("postId", post.getPost_id());
                toCOmmentIntent.putExtra("authorId", post.getUser_id());// Author/composer of the post.
                mContext.startActivity(toCOmmentIntent);
            }
        });
        holder.comment_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCOmmentIntent = new Intent(mContext, CommentsActivity.class);
                toCOmmentIntent.putExtra("postId", post.getPost_id());
                toCOmmentIntent.putExtra("authorId", post.getUser_id());
                mContext.startActivity(toCOmmentIntent);
            }
        });
        holder.post_dish_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetail_post_description_Intent = new Intent(mContext, detailed_post_description.class);
                toDetail_post_description_Intent.putExtra("postId", post.getPost_id());
                toDetail_post_description_Intent.putExtra("authorId", post.getUser_id());
                mContext.startActivity(toDetail_post_description_Intent);
            }
        });

        holder.report_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(mContext,holder.report_image);
                popupMenu.inflate(R.menu.each_post_menu);
                if(!TextUtils.equals(post.getUser_id(),FirebaseAuth.getInstance().getCurrentUser().getUid().toString()))
                {
                    popupMenu.getMenu().findItem(R.id.delete_post_menu).setVisible(false);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId())
                        {
                            case R.id.report_menu :
                                return true;
                            case R.id.delete_post_menu:
                                Log.i("delete post","delete post button pressed");
                                String Post_Id=post.getPost_id();
                                String image_url_OfPostToDelete=post.getimage_url();
                                deletePostFunction(Post_Id,image_url_OfPostToDelete);
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });

        holder.favourites_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String,Object>map=new HashMap<>();

                if(holder.favourites_button.getTag().equals("Add"))
                {
                    holder.favourites_button.playAnimation();

                    map.clear();
                    map.put("post_id",post.getPost_id());
                    map.put("Author_id",post.getUser_id());
                    map.put("dish_name",post.getDish_name());
                    map.put("dish_imageUrl",post.getimage_url());
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference();


                    ref.child("favourites").child(mAuth.getCurrentUser().getUid()).child(post.getPost_id()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(mContext,"Added to Favourites",Toast.LENGTH_LONG).show();
                        }
                    });


                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("favourites").child(mAuth.getCurrentUser().getUid()).child(post.getPost_id()).removeValue();
                }

            }
        });
    }

    private void getTitle(final TextView textView, final ImageView imageView) {

        FirebaseDatabase.getInstance().getReference().child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                for(DataSnapshot snap:snapshot.getChildren())
                {
                    if(TextUtils.equals(snap.child("user_id").getValue().toString(),FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        count++;
                    }
                }


                if(count>=5 && count<=10)
                {
                    if(isValidContextForGlide(mContext))
                    {
                        Glide.with(mContext)
                                .load("https://firebasestorage.googleapis.com/v0/b/beat-my-recipe.appspot.com/o/chef_titles%2Fchef2title.png?alt=media&token=2c8e8057-a6bc-433b-a3a3-6ad8d2f40f55")
                                .into(imageView);
                    }
                    textView.setText("Commis Chef");

                }
                else if(count>10 && count<=30)
                {
                    if(isValidContextForGlide(mContext))
                    {
                        Glide.with(mContext)
                                .load("https://firebasestorage.googleapis.com/v0/b/beat-my-recipe.appspot.com/o/chef_titles%2Fchef3title.png?alt=media&token=aa863c94-9724-41fe-b541-15234c34be08")
                                .into(imageView);
                    }
                    textView.setText("Chef de Partie");

                }
                else if(count>30 && count<=100)
                {
                    if(isValidContextForGlide(mContext))
                    {
                        Glide.with(mContext)
                                .load("https://firebasestorage.googleapis.com/v0/b/beat-my-recipe.appspot.com/o/chef_titles%2Fchef4title.png?alt=media&token=7e7f6f14-5bc1-49f8-ba9b-119db9eec4c5")
                                .into(imageView);
                    }
                    textView.setText("Chef de Cuisine (Head Chef)");

                }
                else if(count>100)
                {
                    if(isValidContextForGlide(mContext))
                    {
                        Glide.with(mContext)
                                .load("https://firebasestorage.googleapis.com/v0/b/beat-my-recipe.appspot.com/o/chef_titles%2Fchef5title.png?alt=media&token=496e4d04-1394-4f57-a3a8-066cfe25ef74")
                                .into(imageView);
                    }
                    textView.setText("Executive Chef");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deletePostFunction(final String Post_Id, final String image_url_OfPostToDelete) {
        AlertDialog alertDialog=new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Are you sure you want to delete your post? This task cannot be undone.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference().child("posts").child(Post_Id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseStorage.getInstance().getReferenceFromUrl(image_url_OfPostToDelete).delete();
                        // Added this line below.
                        FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child(Post_Id).exists())
                                {
                                    FirebaseDatabase.getInstance().getReference().child("Likes").child(Post_Id).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        /*
                        FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.child(Post_Id).exists()) {
                                    likes likes=snapshot.child(Post_Id).getValue(Model.likes.class);
                                    String notification_id=likes.getNotification_id();
                                    removeNotification_deletePost(Post_Id,notification_id,'L');

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                         */
                        // Added this line below.
                        FirebaseDatabase.getInstance().getReference().child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child(Post_Id).exists())
                                {
                                    FirebaseDatabase.getInstance().getReference().child("comments").child(Post_Id).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                            FirebaseDatabase.getInstance().getReference().child("notifications").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot snap:snapshot.getChildren())
                                    {
                                        notification notification=snap.getValue(Model.notification.class);
                                        if(TextUtils.equals(notification.getPostId(),Post_Id))
                                        {
                                            String notification_id=notification.getNotification_id();
                                            FirebaseDatabase.getInstance().getReference().child("notifications").child(mAuth.getCurrentUser().getUid()).child(notification_id).removeValue();
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        /*
                        FirebaseDatabase.getInstance().getReference().child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child(Post_Id).exists())
                                {
                                    comment comment=snapshot.getValue(Model.comment.class);
                                    String notification_id=comment.getNotification_id();

                                    removeNotification_deletePost(Post_Id,notification_id,'C');
                                   // FirebaseDatabase.getInstance().getReference().child("comments").child(Post_Id).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                         */
                    }
                });

            }
        });
        alertDialog.show();
    }


    private void post_composer_info(final home_viewHolder holder, final String post_id, final ProgressBar progressBar) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("posts").child(post_id).exists()) {
                post post = snapshot.child("posts").child(post_id).getValue(Model.post.class);
                if (snapshot.child("users").child(post.getUser_id()).exists()) {
                    User user = snapshot.child("users").child(post.getUser_id()).getValue(User.class);
                    if (snapshot.child("users").child(post.getUser_id()).child("image_url").exists()) {
                        holder.post_composer_name.setText(user.getName());
                        if(isValidContextForGlide(mContext))
                        {
                            Glide.with(holder.itemView.getContext())
                                    .load(user.getImage_url())
                                    .into(holder.post_composer_image);
                        }

                    }
                }
            }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public home_adapter.home_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_layout, parent, false);
        return new home_adapter.home_viewHolder(view);
    }

    class home_viewHolder extends RecyclerView.ViewHolder {
        ImageView post_composer_image;
        TextView post_composer_name;
        ImageView post_dish_image;
        //ImageView like_dish;
        TextView likes_count;
        TextView timestamp;
        ImageView comment_image;
        TextView comment_count;

        ImageView report_image;
        ProgressBar p;
        SparkButton like_dish;
        ProgressBar progressBar;
        SparkButton favourites_button;
        TextView chef_title;
        ImageView chef_title_image;


        public home_viewHolder(@NonNull View itemView) {
            super(itemView);
            like_dish = itemView.findViewById(R.id.like_dish_xml);
            favourites_button=itemView.findViewById(R.id.favourites_button);
            post_composer_image = itemView.findViewById(R.id.post_composer_image_xml);
            post_composer_name = itemView.findViewById(R.id.post_composer_name_xml);
            post_dish_image = itemView.findViewById(R.id.post_dish_image_xml);
            //like_dish=itemView.findViewById(R.id.like_dish_xml);
            likes_count = itemView.findViewById(R.id.dish_likesCount_xml);
            comment_image = itemView.findViewById(R.id.dish_comment_xml);
            comment_count = itemView.findViewById(R.id.dish_comment_count);

            report_image = itemView.findViewById(R.id.report_post_xml);
            timestamp = itemView.findViewById(R.id.home_timestamp);
            p = itemView.findViewById(R.id.spin_kit2);
            firebaseUser = mAuth.getCurrentUser();
            progressBar=itemView.findViewById(R.id.progress_bar_postLayout);
            chef_title=itemView.findViewById(R.id.chef_title_homeName);
            chef_title_image=itemView.findViewById(R.id.chef_title_homeImage);

        }
    }

    private void isLiked(String postId, final SparkButton imageView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (firebaseUser != null) {
                    if (snapshot.child(mAuth.getCurrentUser().getUid()).exists()) {
                        //imageView.setImageResource(R.drawable.delicious_emoji);
                        imageView.setChecked(true);
                        imageView.setTag("Liked");
                    } else {
                        //imageView.setImageResource(R.drawable.delicious_emoji_transparent);
                        imageView.setChecked(false);
                        imageView.setTag("Like");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void isAddedtoFavourite(String PostId,final SparkButton imageView)
    {
        FirebaseDatabase.getInstance().getReference().child("favourites").child(mAuth.getCurrentUser().getUid()).child(PostId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(firebaseUser!=null)
                {
                    if(snapshot.exists())
                    {
                        imageView.setChecked(true);
                        imageView.setTag("Added");
                    }
                    else {
                        imageView.setChecked(false);
                        imageView.setTag("Add");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noOfLikes(final String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child((postId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getComments(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

        // made changes in the line below. Used "user_id" instead of FirebaseAuth.get.....getUid()
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
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post_Id).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post_Id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            FirebaseDatabase.getInstance().getReference().child("posts").child(post_Id).child("popularity").setValue(-1*(snapshot.getChildrenCount())+"");

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });

                        }
                    });
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post_Id).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post_Id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    FirebaseDatabase.getInstance().getReference().child("posts").child(post_Id).child("popularity").setValue(-1*(snapshot.getChildrenCount())+"");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void removeNotification_deletePost(final String Post_Id, final String notification_id, final char ch)
    {
        FirebaseDatabase.getInstance().getReference().child("notifications").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(notification_id).exists())
                {
                    FirebaseDatabase.getInstance().getReference().child("notifications").
                            child(mAuth.getCurrentUser().getUid()).child(notification_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(ch=='L')
                            {
                                FirebaseDatabase.getInstance().getReference().child("Likes").child(Post_Id).removeValue();
                            }
                            else if(ch=='C')
                            {
                                FirebaseDatabase.getInstance().getReference().child("comments").child(Post_Id).removeValue();
                            }


                        }
                    });
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

}
