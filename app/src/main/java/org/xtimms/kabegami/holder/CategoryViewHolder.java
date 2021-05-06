package org.xtimms.kabegami.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.xtimms.kabegami.R;
import org.xtimms.kabegami.interfaces.OnItemClickListener;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView categoryName;
    public ImageView backgroundImage;
    public ProgressBar progressBar;

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        backgroundImage = itemView.findViewById(R.id.image);
        categoryName = itemView.findViewById(R.id.name);
        progressBar = itemView.findViewById(R.id.progressBar);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onClick(v, getAdapterPosition());
    }
}
