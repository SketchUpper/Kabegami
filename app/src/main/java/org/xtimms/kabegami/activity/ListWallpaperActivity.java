package org.xtimms.kabegami.activity;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.xtimms.kabegami.Common;
import org.xtimms.kabegami.R;
import org.xtimms.kabegami.holder.ListWallpaperViewHolder;
import org.xtimms.kabegami.interfaces.OnItemClickListener;
import org.xtimms.kabegami.model.WallpaperItem;

public class ListWallpaperActivity extends AppCompatActivity {

    Query query;
    FirebaseRecyclerOptions<WallpaperItem> options;
    FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder> adapter;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wallpaper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Common.STR_CATEGORY_SELECTED);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        loadBackgroundList();
    }

    public void enableTransparentStatusBar(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (color != 0) {
                window.setStatusBarColor(ContextCompat.getColor(this, color));
            }
        }
    }

    private void loadBackgroundList() {
        query = FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER)
                .orderByChild("categoryId")
                .equalTo(Common.STR_CATEGORY_ID_SELECTED);
        options = new FirebaseRecyclerOptions.Builder<WallpaperItem>()
                .setQuery(query, WallpaperItem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ListWallpaperViewHolder listWallpaperViewHolder, int i, @NonNull final WallpaperItem wallpaperItem) {
                Picasso.get()
                        .load(wallpaperItem.getImageLink())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(listWallpaperViewHolder.wallpaper, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(wallpaperItem.getImageLink())
                                        .error(R.drawable.ic_baseline_error_outline_24)
                                        .into(listWallpaperViewHolder.wallpaper, new Callback() {
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

                listWallpaperViewHolder.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(ListWallpaperActivity.this, ViewWallpaperActivity.class);
                        Common.selectBackground = wallpaperItem;
                        Common.selectBackgroundKey = adapter.getRef(position).getKey();
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ListWallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_wallpaper, parent, false);
                return new ListWallpaperViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        if (adapter != null) {
            adapter.stopListening();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}