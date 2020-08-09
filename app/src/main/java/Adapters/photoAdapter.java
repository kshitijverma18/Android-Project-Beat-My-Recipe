package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.List;

import Model.comment;
import Model.post;

public class photoAdapter extends RecyclerView.Adapter<photoAdapter.ViewHolder> {
    private Context mContext;
    private List<post> mPosts;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.photo,parent,false);
        return new photoAdapter.ViewHolder(view);
    }

    public photoAdapter(Context mContext, List<post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final post post=mPosts.get(position);
        if(isValidContextForGlide(mContext))
        {
            Glide.with(mContext).load(post.getimage_url()).into(holder.postImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDetail_post_description_Intent=new Intent(mContext, detailed_post_description.class);
                toDetail_post_description_Intent.putExtra("postId", post.getPost_id());
                toDetail_post_description_Intent.putExtra("authorId", post.getUser_id());
                mContext.startActivity(toDetail_post_description_Intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView postImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage=itemView.findViewById(R.id.user_post_image_xml);

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
