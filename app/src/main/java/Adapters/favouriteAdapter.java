package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kshitijverma.beat_my_recipie.R;
import com.example.kshitijverma.beat_my_recipie.detailed_post_description;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Model.User;
import Model.favourite;
import Model.post;


public class favouriteAdapter extends RecyclerView.Adapter<favouriteAdapter.ViewHolder>{

    private Context mContext;
    private List<favourite> mFavourite;
    private FirebaseUser fuser;

    public favouriteAdapter(Context mContext, List<favourite> mFavourite) {
        this.mContext = mContext;
        this.mFavourite = mFavourite;
        fuser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.favourite_list_item,parent,false);
        return new favouriteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final favourite favourite=mFavourite.get(position);

        getFavouriteUserInfo(holder.favourite_userName,holder.favourite_usertitle,holder.favourite_userImage,favourite);
        getFavouriteDishInfo(holder.favourite_userDish,holder,favourite);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetail_post_description_Intent = new Intent(mContext, detailed_post_description.class);
                toDetail_post_description_Intent.putExtra("postId", favourite.getPost_id());
                toDetail_post_description_Intent.putExtra("authorId", favourite.getAuthor_id());
                mContext.startActivity(toDetail_post_description_Intent);
            }
        });

    }

    private void getFavouriteDishInfo(final ImageView favourite_userDish, ViewHolder holder, final favourite favourite) {
        FirebaseDatabase.getInstance().getReference().child("posts").child(favourite.getPost_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    post post=snapshot.getValue(Model.post.class);
                    if(isValidContextForGlide(mContext))
                    {
                        Glide.with(mContext)
                                .load(post.getimage_url())
                                .into(favourite_userDish);
                    }
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("favourites").child(FirebaseAuth.getInstance().
                            getCurrentUser().getUid()).child(favourite.getPost_id()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFavouriteUserInfo(final TextView favourite_userName, TextView favourite_usertitle,
                                  final ImageView favourite_userImage, favourite favourite) {
        FirebaseDatabase.getInstance().getReference().child("users").child(favourite.getAuthor_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    User user=snapshot.getValue(User.class);
                    if(snapshot.child("image_url").exists())
                    {
                        if(isValidContextForGlide(mContext))
                        {
                            Glide.with(mContext)
                                    .load(user.getImage_url())
                                    .into(favourite_userImage);
                        }
                    }
                    favourite_userName.setText(user.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mFavourite.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView favourite_userImage;
        TextView favourite_userName;
        TextView favourite_usertitle;
        ImageView favourite_userDish;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favourite_userDish=itemView.findViewById(R.id.favourite_userDish);
            favourite_userImage=itemView.findViewById(R.id.favourite_profileimage);
            favourite_userName=itemView.findViewById(R.id.favourite_userName);
            favourite_usertitle=itemView.findViewById(R.id.favourite_usertitle);
            relativeLayout=itemView.findViewById(R.id.favourite_recyclerView);
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
