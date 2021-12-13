/*
 * Bowling Tracker Android app
 * Copyright the Bowling Tracker Contributors.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.warnea.bowlingtracker.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.warnea.bowlingtracker.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SeriesMainFragment extends Fragment {
    final static List<Pair<Integer, Class<? extends Fragment>>> TABS = new ArrayList<>();

    static{
        TABS.add(new Pair<>(
                    R.string.fragment_series_main_tab_series_list,
                    SeriesListFragment.class));
        TABS.add(new Pair<>(
                R.string.fragment_series_main_tab_stats,
                SeriesStatsFragment.class));
        TABS.add(new Pair<>(
                R.string.fragment_series_main_tab_chart,
                SeriesGraphFragment.class));
    }

    SeriesMainFragment() {
        super();
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_series_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 viewPager = view.findViewById(R.id.series_main_pager);
        viewPager.setAdapter(new SeriesMainPagerAdapter(this));

        TabLayout tabLayout = view.findViewById(R.id.series_main_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(view.getContext().getText(TABS.get(position).first))
        ).attach();
    }

    public class SeriesMainPagerAdapter extends FragmentStateAdapter {

        public SeriesMainPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Class<? extends Fragment> myClass = TABS.get(position).second;
            Constructor<?> ctor = null;
            try {
                ctor = myClass.getConstructor();
                Fragment fragment = (Fragment) ctor.newInstance();
                fragment.setArguments(getArguments());
                return fragment;
            } catch (NoSuchMethodException e) {
                Log.e("createFragment", e.getStackTrace().toString());
            } catch (IllegalAccessException e) {
                Log.e("createFragment", e.getStackTrace().toString());
            } catch (java.lang.InstantiationException e) {
                Log.e("createFragment", e.getStackTrace().toString());
            } catch (InvocationTargetException e) {
                Log.e("createFragment", e.getStackTrace().toString());
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return TABS.size();
        }

    }


}
