package Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kshitijverma.beat_my_recipie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Model.User;
import Model.comment;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.ViewHolder>{
    private Context mContext;
    private List<comment> mComment;
    private FirebaseUser fuser;
    String PostId;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public commentAdapter(Context mContext, List<comment> mComment,String PostId) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.PostId=PostId;
    }
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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        return new commentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        final comment comment=mComment.get(position);
        holder.date.setText(getTimeAgo(comment.getDate()));
        holder.comment.setText(comment.getComment());
        // Extracting username from firebase. Username has not been inserted by me yet in firebase.

        FirebaseDatabase.getInstance().getReference().child("users").child((comment.getPublisher())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                holder.username.setText(user.getName());
                // Insert userImage Here.
                if(user.getImage_url()!=null)
                {
                    if (isValidContextForGlide(mContext))
                    {
                        Glide.with(mContext)
                                .load(user.getImage_url())
                                .into(holder.imageProfile);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(comment.getPublisher().endsWith(fuser.getUid()))
                {
                    AlertDialog alertDialog=new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Are you sure you want to delete the comment");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            final String notification_id=comment.getNotification_id();
                            FirebaseDatabase.getInstance().getReference().child("comments").child(PostId).
                                    child(comment.getNotification_id()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                        //FirebaseDatabase.getInstance().getReference().child("notifications").child(comment.getPostOwner()).child(notification_id).removeValue();
                                        FirebaseDatabase.getInstance().getReference().child("notifications").child(comment.getPostOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.child(notification_id).exists())
                                                {
                                                    FirebaseDatabase.getInstance().getReference().child("notifications").child(comment.getPostOwner()).child(notification_id).removeValue();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        Toast.makeText(mContext,"Comment Deleted Successfully",Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
                return true;
            }
        });

    }



    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageProfile;
        public TextView username;
        public TextView comment;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile=itemView.findViewById(R.id.comment_item_userProfile);
            username=itemView.findViewById(R.id.comment_item_user_name);
            comment=itemView.findViewById(R.id.comment_item_insertComment);
            date=itemView.findViewById(R.id.comment_item_date);
        }
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
