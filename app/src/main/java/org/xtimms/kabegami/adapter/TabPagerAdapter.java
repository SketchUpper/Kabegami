package org.xtimms.kabegami.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.xtimms.kabegami.fragment.CategoryFragment;
import org.xtimms.kabegami.fragment.TrendingFragment;
import org.xtimms.kabegami.fragment.RecentsFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    public TabPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return CategoryFragment.getInstance();
        } else if (position == 1) {
            return TrendingFragment.getInstance();
        } else if (position == 2) {
            return RecentsFragment.getInstance(context);
        }
        return CategoryFragment.getInstance();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Categories";
            case 1:
                return "Trending";
            case 2:
                return "Recents";
            default:
                return "";
        }
    }
}
