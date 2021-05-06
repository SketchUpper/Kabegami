package org.xtimms.kabegami.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.xtimms.kabegami.Common;
import org.xtimms.kabegami.R;
import org.xtimms.kabegami.activity.ViewWallpaperActivity;
import org.xtimms.kabegami.db.Recents;
import org.xtimms.kabegami.holder.ListWallpaperViewHolder;
import org.xtimms.kabegami.interfaces.OnItemClickListener;
import org.xtimms.kabegami.model.WallpaperItem;

import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<ListWallpaperViewHolder> {

    private final Context context;
    private final List<Recents> recents;

    public RecentsAdapter(Context context, List<Recents> recents) {
        this.context = context;
        this.recents = recents;
    }

    @NonNull
    @Override
    public ListWallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallpaper, parent, false);
        return new ListWallpaperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListWallpaperViewHolder holder, int position) {
        Picasso.get()
                .load(recents.get(position).getImageLink())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.wallpaper, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        holder.progressBar.setVisibility(View.GONE);
                        Picasso.get()
                                .load(recents.get(position).getImageLink())
                                .error(R.drawable.ic_error)
                                .into(holder.wallpaper, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("PICASSO", "Couldn't fetch image");
                                    }
                                });
                    }
                });

        holder.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(context, ViewWallpaperActivity.class);
                WallpaperItem wallpaperItem = new WallpaperItem();
                wallpaperItem.setCategoryId(recents.get(position).getCategoryId());
                wallpaperItem.setImageLink(recents.get(position).getImageLink());
                Common.selectBackground = wallpaperItem;
                Common.selectBackgroundKey = recents.get(position).getKey();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }

}
