package com.example.kshitijverma.beat_my_recipie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


import java.util.List;

public class explore_recycler_adapter extends FirebaseRecyclerAdapter<explore_class, explore_recycler_adapter.explore_recycler_viewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
   // Explore_Activity obj=new Explore_Activity();
    public explore_recycler_adapter(@NonNull FirebaseRecyclerOptions<explore_class> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull explore_recycler_viewHolder holder, int position, @NonNull explore_class explore_class) {
        holder.dish_name.setText(explore_class.getDish());
        holder.p.setVisibility(View.GONE);
        holder.explore_country.setText(explore_class.getCountry());
        Glide.with(holder.itemView.getContext()).load(explore_class.getImage()).into(holder.explore_dish_image);
    }
    @NonNull
    @Override
    public explore_recycler_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.explore_recycler_list_item, parent, false);
        return new explore_recycler_viewHolder(view);
    }
    class explore_recycler_viewHolder extends RecyclerView.ViewHolder{
        TextView dish_name;
        ProgressBar p;
        ImageView explore_dish_image;
        TextView explore_country;

        public explore_recycler_viewHolder(@NonNull View itemView) {
            super(itemView);
            dish_name=itemView.findViewById(R.id.dish_name_xml);
            explore_dish_image=(ImageView)itemView.findViewById(R.id.explore_dish_image_xml);
            p=itemView.findViewById(R.id.spin_kit2);
            explore_country=itemView.findViewById(R.id.explore_country_xml);
        }
    }
}
