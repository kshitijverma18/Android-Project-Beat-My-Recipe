package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kshitijverma.beat_my_recipie.R;
import com.example.kshitijverma.beat_my_recipie.detailed_post_description;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Model.User;

import Model.notification;
import Model.post;
import io.opencensus.internal.StringUtils;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private Context mContext;
    private List<notification>mNotification;

    public NotificationAdapter(Context mContext, List<notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }
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
        } else if(diff<31*DAY_MILLIS){
            return diff / DAY_MILLIS + " days ago";
        }
        else{
            return "delete";
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final notification notification=mNotification.get(position);
        Log.i("In Notification",notification.getPostId());
        getUser(holder.user_image,holder.actual_name, notification.getUserId(),holder);
        holder.comment.setText(notification.getText());
        holder.date.setText(getTimeAgo(notification.getDate()));
        if(notification.getIsPost())
        {
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image,notification.getPostId(),holder);
        }
        else
        {
            holder.post_image.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.getIsPost())
                {
                    //mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().
                           // putString("postId",notification.getPostId()).apply();
                    Intent todetailPostIntent=new Intent(mContext, detailed_post_description.class);
                    todetailPostIntent.putExtra("postId",notification.getPostId());
                    todetailPostIntent.putExtra("authorId",notification.getUserId());
                    mContext.startActivity(todetailPostIntent);
                }
                else
                {

                }
            }
        });

    }

    private void getPostImage(final ImageView post_image, String postId, final ViewHolder holder) {
        FirebaseDatabase.getInstance().getReference().child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post post=snapshot.getValue(Model.post.class);
                if(isValidContextForGlide(mContext))
                {
                    Glide.with(mContext)
                            .load(post.getimage_url())
                            .into(post_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getUser(final ImageView user_image, final TextView actual_name, String userId, final ViewHolder holder) {
        FirebaseDatabase.getInstance().getReference().child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(snapshot.child("image_url").exists()) {
                    if(isValidContextForGlide(mContext))
                    {
                        Glide.with(holder.itemView.getContext())
                                .load(user.getImage_url())
                                .into(user_image);
                    }

                }
                actual_name.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView user_image;
        TextView actual_name;
        TextView comment;
        ImageView post_image;
        TextView date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_image=itemView.findViewById(R.id.image_profile_notification);
            actual_name=itemView.findViewById(R.id.notification_name);
            comment=itemView.findViewById(R.id.notification_comment);
            post_image=itemView.findViewById(R.id.notification_image);
            date=itemView.findViewById(R.id.notification_date);
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
