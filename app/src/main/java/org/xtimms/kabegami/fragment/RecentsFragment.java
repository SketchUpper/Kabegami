package org.xtimms.kabegami.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xtimms.kabegami.R;
import org.xtimms.kabegami.adapter.RecentsAdapter;
import org.xtimms.kabegami.db.Recents;
import org.xtimms.kabegami.db.local.LocalDatabase;
import org.xtimms.kabegami.db.local.RecentsDataSource;
import org.xtimms.kabegami.db.source.RecentsRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecentsFragment extends Fragment {

    private static RecentsFragment INSTANCE = null;

    RecyclerView recyclerView;

    Context context;

    List<Recents> recentsList;
    RecentsAdapter adapter;

    CompositeDisposable compositeDisposable;
    RecentsRepository recentsRepository;

    public RecentsFragment(Context context) {
        // Required empty public constructor
        this.context = context;
        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(context);
        recentsRepository = RecentsRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));
    }

    public static RecentsFragment getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new RecentsFragment(context);
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recents, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recentsList = new ArrayList<>();
        adapter = new RecentsAdapter(context, recentsList);
        recyclerView.setAdapter(adapter);
        
        loadRecents();
        
        return view;
    }

    private void loadRecents() {
        Disposable disposable = recentsRepository.getAllRecents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onGetAllRecentsSuccess, throwable -> Log.d("ERROR", throwable.getMessage()));
        compositeDisposable.add(disposable);
    }

    private void onGetAllRecentsSuccess(List<Recents> recents) {
        recentsList.clear();
        recentsList.addAll(recents);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}