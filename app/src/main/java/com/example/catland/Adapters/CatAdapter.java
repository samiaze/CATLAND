package com.example.catland.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catland.Categories.CatDetailActivity;
import com.example.catland.Models.Cat;
import com.example.catland.R;

import java.util.List;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CatViewHolder> {
    private final List<Cat> breeds;
    private final Context context;

    public CatAdapter(Context context, List<Cat> breeds) {
        this.context = context;
        this.breeds = breeds;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cat, parent, false);
        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        Cat breed = breeds.get(position);
        holder.name.setText(breed.getName());
        holder.origin.setText(breed.getOrigin());
        holder.image.setImageResource(breed.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CatDetailActivity.class);
            intent.putExtra("name", breed.getName());
            intent.putExtra("origin", breed.getOrigin());
            intent.putExtra("description", breed.getDescription());
            intent.putExtra("image", breed.getImageResId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return breeds.size();
    }

    static class CatViewHolder extends RecyclerView.ViewHolder {
        TextView name, origin;
        ImageView image;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.breedName);
            origin = itemView.findViewById(R.id.breedOrigin);
            image = itemView.findViewById(R.id.breedImage);
        }
    }
}
