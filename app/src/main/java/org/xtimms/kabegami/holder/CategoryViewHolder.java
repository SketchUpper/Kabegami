package org.xtimms.kabegami.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.xtimms.kabegami.R;
import org.xtimms.kabegami.interfaces.OnItemClickListener;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView category_name;
    public ImageView background_image;

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        background_image = itemView.findViewById(R.id.image);
        category_name = itemView.findViewById(R.id.name);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onClick(v, getAdapterPosition());
    }
}
