package org.xtimms.kabegami.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.xtimms.kabegami.Common;
import org.xtimms.kabegami.R;
import org.xtimms.kabegami.activity.ViewWallpaperActivity;
import org.xtimms.kabegami.holder.ListWallpaperViewHolder;
import org.xtimms.kabegami.interfaces.OnItemClickListener;
import org.xtimms.kabegami.model.WallpaperItem;

public class TrendingFragment extends Fragment {

    RecyclerView recyclerView;

    FirebaseDatabase database;
    DatabaseReference categoryBackground;

    FirebaseRecyclerOptions<WallpaperItem> options;
    FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder> adapter;

    private static TrendingFragment INSTANCE = null;

    public TrendingFragment() {
        database = FirebaseDatabase.getInstance();
        categoryBackground = database.getReference(Common.STR_WALLPAPER);

        Query query = categoryBackground.orderByChild("viewCount")
                .limitToLast(10);

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
                        Intent intent = new Intent(getActivity(), ViewWallpaperActivity.class);
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
    }

    public static TrendingFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TrendingFragment();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadTrending();

        return view;
    }

    private void loadTrending() {
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

}