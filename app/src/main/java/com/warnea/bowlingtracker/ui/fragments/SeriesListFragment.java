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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.models.Game;
import com.warnea.bowlingtracker.models.League;
import com.warnea.bowlingtracker.models.Series;
import com.warnea.bowlingtracker.ui.adapters.SeriesListAdapter;
import com.warnea.bowlingtracker.ui.dialogs.NewSeriesDialog;
import com.warnea.bowlingtracker.ui.runnables.SeriesOkRunnable;

import java.util.List;

public class SeriesListFragment extends Fragment {
    private Context context;
    private League league;
    private SeriesListAdapter seriesListAdapter;
    private List<Series> seriesList;

    public SeriesListFragment() {
        super();
        setRetainInstance(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        league = (League) b.getSerializable("League");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_series_list, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.series_list_recyclerview_series);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        League.createDbHelper(context);
        Series.createDbHelper(context);
        Game.createDbHelper(context);

        seriesList = Series.getSavedSeriesByLeagueId(league.getId());
        seriesListAdapter = new SeriesListAdapter(context, seriesList);

        recyclerView.setAdapter(seriesListAdapter);

        //When a series is clicked, create a new games fragment and replace the current fragment
        seriesListAdapter.setOnItemClickListener(new SeriesListAdapter.onClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                final FragmentActivity activity = (FragmentActivity) context;
                FragmentManager fm = activity.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = new GamesFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Series", seriesList.get(position));
                fragment.setArguments(bundle);
                ft.replace(R.id.main_frame_layout, fragment)
                        .addToBackStack(null)
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                        .commit();
            }
        });


        FloatingActionButton fab = view.findViewById(R.id.series_list_fab_add_series);
        //When the add new series button is clicked, create the new series dialog
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentActivity activity = (FragmentActivity) context;
                FragmentManager manager = activity.getSupportFragmentManager();

                Fragment frag = manager.findFragmentByTag("fragment_add_series");
                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }

                NewSeriesDialog newLeagueDialog = new NewSeriesDialog(
                        new SeriesOkRunnable() {
                            @Override
                            public void run() {
                                Series series = new Series(
                                        this.getDate(),
                                        this.getHdcpPerGame(),
                                        league);
                                seriesList.add(0, series);
                                seriesListAdapter.notifyDataSetChanged();
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {

                            }
                        },
                        league
                );
                newLeagueDialog.show(manager, "fragment_add_series");
            }
        });
        return view;
    }
}
