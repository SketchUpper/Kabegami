package org.xtimms.kabegami.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.xtimms.kabegami.R;
import org.xtimms.kabegami.interfaces.OnItemClickListener;

public class ListWallpaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    OnItemClickListener onItemClickListener;

    public ImageView wallpaper;
    public ProgressBar progressBar;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ListWallpaperViewHolder(@NonNull View itemView) {
        super(itemView);
        wallpaper = itemView.findViewById(R.id.image);
        progressBar = itemView.findViewById(R.id.progressBar);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onClick(v, getAdapterPosition());
    }
}
