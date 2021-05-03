package org.xtimms.kabegami.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.xtimms.kabegami.Common;
import org.xtimms.kabegami.R;
import org.xtimms.kabegami.activity.ListWallpaperActivity;
import org.xtimms.kabegami.holder.CategoryViewHolder;
import org.xtimms.kabegami.interfaces.OnItemClickListener;
import org.xtimms.kabegami.model.CategoryItem;

public class CategoryFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference categoryBackground;
    FirebaseRecyclerOptions<CategoryItem> options;
    FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder> adapter;
    RecyclerView recyclerView;

    private static CategoryFragment INSTANCE = null;

    public CategoryFragment() {
        database = FirebaseDatabase.getInstance();
        categoryBackground = database.getReference(Common.STR_CATEGORY_BACKGROUND);

        options = new FirebaseRecyclerOptions.Builder<CategoryItem>()
                .setQuery(categoryBackground, CategoryItem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i, @NonNull final CategoryItem categoryItem) {
                Picasso.get()
                        .load(categoryItem.getImageLink())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(categoryViewHolder.background_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(categoryItem.getImageLink())
                                        .error(R.drawable.ic_baseline_error_outline_24)
                                        .into(categoryViewHolder.background_image, new Callback() {
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

                categoryViewHolder.category_name.setText(categoryItem.getName());

                categoryViewHolder.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Common.STR_CATEGORY_ID_SELECTED = adapter.getRef(position).getKey();
                        Common.STR_CATEGORY_SELECTED = categoryItem.getName();
                        Intent intent = new Intent(getActivity(), ListWallpaperActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_category, parent, false);
                return new CategoryViewHolder(itemView);
            }
        };
    }

    public static CategoryFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CategoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        setCategory();

        return view;
    }

    private void setCategory() {
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