package org.xtimms.kabegami.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.xtimms.kabegami.Common;
import org.xtimms.kabegami.R;
import org.xtimms.kabegami.db.Recents;
import org.xtimms.kabegami.db.local.LocalDatabase;
import org.xtimms.kabegami.db.local.RecentsDataSource;
import org.xtimms.kabegami.db.source.RecentsRepository;
import org.xtimms.kabegami.helper.SaveHelper;
import org.xtimms.kabegami.model.WallpaperItem;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ViewWallpaperActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    BottomAppBar bottomAppBar;
    ImageView imageView;
    CoordinatorLayout rootLayout;

    CompositeDisposable compositeDisposable;
    RecentsRepository recentsRepository;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
            try {
                manager.setBitmap(bitmap);
                Snackbar.make(rootLayout, "Wallpaper was set", Snackbar.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Exception e,Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bottomAppBar = findViewById(R.id.bottom_navigation);
        bottomAppBar.setOnMenuItemClickListener(this);

        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(this);
        recentsRepository = RecentsRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));

        rootLayout = findViewById(R.id.root);

        imageView = findViewById(R.id.image);
        Picasso.get()
                .load(Common.selectBackground.getImageLink())
                .into(imageView);

        addToRecents();

        increaseViewCount();
    }

    private void increaseViewCount() {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .child(Common.selectBackgroundKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("viewCount")) {
                            WallpaperItem wallpaperItem = snapshot.getValue(WallpaperItem.class);
                            long count = wallpaperItem.getViewCount() + 1;
                            Map<String, Object> updateView = new HashMap<>();
                            updateView.put("viewCount", count);

                            FirebaseDatabase.getInstance()
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.selectBackgroundKey)
                                    .updateChildren(updateView)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ViewWallpaperActivity.this, "Can't update view count", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Map<String, Object> updateView = new HashMap<>();
                            updateView.put("viewCount", 1L);

                            FirebaseDatabase.getInstance()
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.selectBackgroundKey)
                                    .updateChildren(updateView)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ViewWallpaperActivity.this, "Can't set default view count", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addToRecents() {
        Disposable disposable = Observable.create((e) -> {
            Recents recents = new Recents(
                    Common.selectBackground.getImageLink(),
                    Common.selectBackground.getCategoryId(),
                    String.valueOf(System.currentTimeMillis()),
                    Common.selectBackgroundKey
            );
            recentsRepository.insertRecents(recents);
            e.onComplete();
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {

                }, throwable -> Log.e("ERROR", throwable.getMessage()), () -> {

                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Common.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ViewWallpaperActivity.this);

                    String fileName = UUID.randomUUID().toString() + ".png";
                    Picasso.get()
                            .load(Common.selectBackground.getImageLink())
                            .into(new SaveHelper(getBaseContext(),
                                    dialog.create(),
                                    getApplicationContext().getContentResolver(),
                                    fileName,
                                    "image"));
                } else {
                    Toast.makeText(this, "You need to accept this permission to download wallpapers", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_download) {
            if (ActivityCompat.checkSelfPermission(ViewWallpaperActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.PERMISSION_REQUEST_CODE);
                }
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ViewWallpaperActivity.this);

                String fileName = UUID.randomUUID().toString() + ".png";
                Picasso.get()
                        .load(Common.selectBackground.getImageLink())
                        .into(new SaveHelper(getBaseContext(),
                                dialog.create(),
                                getApplicationContext().getContentResolver(),
                                fileName,
                                "image"));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Picasso.get().cancelRequest(target);
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.download) {
            if (ActivityCompat.checkSelfPermission(ViewWallpaperActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.PERMISSION_REQUEST_CODE);
                }
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ViewWallpaperActivity.this);

                String fileName = UUID.randomUUID().toString() + ".png";
                Picasso.get()
                        .load(Common.selectBackground.getImageLink())
                        .into(new SaveHelper(getBaseContext(),
                                dialog.create(),
                                getApplicationContext().getContentResolver(),
                                fileName,
                                "image"));
            }
        } else if (item.getItemId() == R.id.apply) {
            Picasso.get()
                    .load(Common.selectBackground.getImageLink())
                    .into(target);
        }
        return true;
    }
}