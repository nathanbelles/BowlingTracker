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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.models.Game;
import com.warnea.bowlingtracker.models.League;
import com.warnea.bowlingtracker.models.Series;
import com.warnea.bowlingtracker.ui.adapters.GamesListAdapter;

import java.util.List;

public class GamesFragment extends Fragment {
    private Context context;
    private Series series;
    private GamesListAdapter gamesListAdapter;
    private List<Game> gamesList;
    private View view;


    public GamesFragment() {
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
        series = (Series) b.getSerializable("Series");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);
        this.view = view;


        final RecyclerView recyclerView = view.findViewById(R.id.games_recyclerview_games);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        League.createDbHelper(context);
        Series.createDbHelper(context);
        Game.createDbHelper(context);

        gamesList = Game.getSavedGamesBySeriesId(series.getId());
        gamesListAdapter = new GamesListAdapter(gamesList);

        recyclerView.setAdapter(gamesListAdapter);

        updateTotalsText(view, series);

        FloatingActionButton fab = view.findViewById(R.id.games_fab_add_game);
        //when clicking on add game, create a new game, then update the list and the totals
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Game game = new Game((short) 0, series);
                gamesList.add(game);
                gamesListAdapter.notifyItemInserted(gamesList.size() - 1);
                gamesListAdapter.notifyItemRangeChanged(
                        gamesList.size() - 1,
                        1);
                updateTotalsText(GamesFragment.this.view, series);
            }
        });
        return view;
    }

    public static void updateTotalsText(View view, Series series) {
        Context context = view.getContext();
        TextView finalAverageTextView = view.findViewById(R.id.games_textview_final_average);
        TextView seriesTotalTextView = view.findViewById(R.id.games_textview_series_total);
        short endingAverage = series.getEndingAverage();
        short averageDiff = (short) (endingAverage - series.getStartingAverage());

        String fontTagParam = "";

        //Set the color of the average increase or decrease text (default color if no change)
        if(averageDiff > 0) {
            fontTagParam = "color=#" +
                    Integer.toHexString(
                            context.getColor(R.color.colorAverageIncrease)
                    ).substring(2);
        } else if (averageDiff < 0) {
            fontTagParam = "color=#" +
                    Integer.toHexString(
                            context.getColor(R.color.colorAverageDecrease)
                    ).substring(2);
        }

        finalAverageTextView.setText(
                Html.fromHtml(
                    String.format("%s: %d (<font %s>%+d</font>)",
                        context.getString(R.string.fragment_games_final_average),
                        series.getEndingAverage(),
                        fontTagParam,
                        series.getEndingAverage() - series.getStartingAverage()),
                Html.FROM_HTML_MODE_LEGACY));
        seriesTotalTextView.setText(
                String.format("%s: %d",
                        context.getString(R.string.fragment_games_series_total),
                        series.getScratchTotal()));

    }
}
