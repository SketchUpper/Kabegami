package org.xtimms.kabegami.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import org.xtimms.kabegami.Common;
import org.xtimms.kabegami.R;
import org.xtimms.kabegami.adapter.TabPagerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.PERMISSION_REQUEST_CODE);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        viewPager = findViewById(R.id.viewPager);
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tabPagerAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
                    Common.SIGN_IN_REQUEST_CODE);
        } else {
            Snackbar.make(drawerLayout, new StringBuilder("Welcome ").append(FirebaseAuth.getInstance().getCurrentUser().getEmail()), Snackbar.LENGTH_SHORT).show();
        }
        
        loadUserInformation();

    }

    private void loadUserInformation() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            View headerLayout = navigationView.getHeaderView(0);
            TextView email = headerLayout.findViewById(R.id.txt_email);
            email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(drawerLayout, new StringBuilder("Welcome ").append(FirebaseAuth.getInstance().getCurrentUser().getEmail()), Snackbar.LENGTH_SHORT).show();

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.PERMISSION_REQUEST_CODE);
                    }
                }

                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                drawerLayout = findViewById(R.id.drawer_layout);
                toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
                NavigationView navigationView = findViewById(R.id.nav_view);

                toggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
                drawerLayout.addDrawerListener(toggle);
                toggle.syncState();

                viewPager = findViewById(R.id.viewPager);
                TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), this);
                viewPager.setAdapter(tabPagerAdapter);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);

                loadUserInformation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Common.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permisson granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You need to accept this permission to download wallpapers", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_upload) {
            startActivity(new Intent(MainActivity.this, UploadActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_view_uploads) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}