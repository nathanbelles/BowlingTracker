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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.models.Game;
import com.warnea.bowlingtracker.models.League;
import com.warnea.bowlingtracker.models.Series;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class SeriesStatsFragment extends Fragment {
    private League league;

    public SeriesStatsFragment() {
        super();
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        league = (League) b.getSerializable("League");
    }

    private void loadStats(View view) {
        Context context = view.getContext();

        TextView highestAverageTextView = view.findViewById(R.id.series_stats_highest_average);
        TextView highestGameTextView = view.findViewById(R.id.series_stats_highest_game);
        TextView highestSeriesTextView = view.findViewById(R.id.series_stats_highest_series);
        TextView lowestGameTextView = view.findViewById(R.id.series_stats_lowest_game);
        TextView lowestSeriesTextView = view.findViewById(R.id.series_stats_lowest_series);
        TextView highestAverageIncTextView =
                view.findViewById(R.id.series_stats_highest_average_inc);
        TextView highestAverageDropTextView =
                view.findViewById(R.id.series_stats_highest_average_drop);

        Pair<Short, String> highestAverage = new Pair<>((short)0,
                context.getString(R.string.fragment_series_stats_not_applicable));
        Pair<Short, String> highestGame = new Pair<>((short)0,
                context.getString(R.string.fragment_series_stats_not_applicable));
        Pair<Short, String> highestSeries = new Pair<>((short)0,
                context.getString(R.string.fragment_series_stats_not_applicable));
        Pair<Short, String> lowestGame = new Pair<>((short)Short.MAX_VALUE,
                context.getString(R.string.fragment_series_stats_not_applicable));
        Pair<Short, String> lowestSeries = new Pair<>((short)Short.MAX_VALUE,
                context.getString(R.string.fragment_series_stats_not_applicable));
        Pair<Short, String> highestAverageInc = new Pair<>((short)0,
                context.getString(R.string.fragment_series_stats_not_applicable));
        Pair<Short, String> highestAverageDrop = new Pair<>((short)0,
                context.getString(R.string.fragment_series_stats_not_applicable));


        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        //iterate through all the series, looking for the highest average, highest series, lowest
        //series, highest average increase, and highest average decrease
        List<Series> seriesList = Series.getSavedSeriesByLeagueId(league.getId());
        for(int i = seriesList.size() - 1; i > -1; i--) {
            Series series = seriesList.get(i);
            short endingAverage = series.getEndingAverage();
            short seriesScratchTotal = series.getScratchTotal();

            //Check current games & series against average and series stats
            if(endingAverage > highestAverage.first)
                highestAverage =
                        new Pair<> (endingAverage, dateFormatter.format(series.getDate()));
            if(seriesScratchTotal > highestSeries.first)
                highestSeries =
                        new Pair<> (seriesScratchTotal, dateFormatter.format(series.getDate()));
            if(seriesScratchTotal <= lowestSeries.first)
                lowestSeries =
                        new Pair<> (seriesScratchTotal, dateFormatter.format(series.getDate()));

            //check the games for highest and lowest scratch games
            List<Game> games = Game.getSavedGamesBySeriesId(series.getId());
            for(Game game : games) {
                short gameScratchTotal = game.getScratchTotal();
                if(gameScratchTotal > highestGame.first)
                    highestGame =
                            new Pair<> (game.getScratchTotal(),
                                    dateFormatter.format(series.getDate()));
                if(gameScratchTotal <= lowestGame.first)
                    lowestGame = new Pair<> (game.getScratchTotal(),
                            dateFormatter.format(series.getDate()));
            }

            //Check if highest increase or drop in average
            short averageDiff = (short) (endingAverage -  series.getStartingAverage());
            if(i != seriesList.size() - 1) {
                if(averageDiff > highestAverageInc.first)
                    highestAverageInc =
                            new Pair<> (averageDiff, dateFormatter.format(series.getDate()));
                if(-averageDiff > highestAverageDrop.first)
                    highestAverageDrop = new Pair<> ((short) -averageDiff,
                            dateFormatter.format(series.getDate()));
            }


        }

        highestAverageTextView.setText(
                String.format("%s: %d on %s",
                        context.getString(R.string.fragment_series_stats_highest_average),
                        highestAverage.first,
                        highestAverage.second));
        highestGameTextView.setText(
                String.format("%s: %d on %s",
                        context.getString(R.string.fragment_series_stats_highest_game),
                        highestGame.first,
                        highestGame.second));
        highestSeriesTextView.setText(
                String.format("%s: %d on %s",
                        context.getString(R.string.fragment_series_stats_highest_series),
                        highestSeries.first,
                        highestSeries.second));
        lowestGameTextView.setText(
                String.format("%s: %d on %s",
                        context.getString(R.string.fragment_series_stats_lowest_game),
                        lowestGame.second!=
                                context.getString(R.string.fragment_series_stats_not_applicable)?
                                lowestGame.first:
                                0,
                        lowestGame.second));
        lowestSeriesTextView.setText(
                String.format("%s: %d on %s",
                        context.getString(R.string.fragment_series_stats_lowest_series),
                        lowestSeries.second!=
                                context.getString(R.string.fragment_series_stats_not_applicable)?
                                lowestSeries.first:
                                0,
                        lowestSeries.second));
        highestAverageIncTextView.setText(
                highestAverageInc.first!=0?
                        String.format("%s: %d on %s",
                                context.getString(
                                        R.string.fragment_series_stats_highest_average_inc),
                                highestAverageInc.first,
                                highestAverageInc.second):
                        String.format("%s: %s",
                                context.getString(
                                        R.string.fragment_series_stats_highest_average_inc),
                                context.getString(
                                        R.string.fragment_series_stats_not_applicable)));
        highestAverageDropTextView.setText(
                highestAverageDrop.first!=0?
                        String.format("%s: %d on %s",
                                context.getString(
                                        R.string.fragment_series_stats_highest_average_dec),
                                highestAverageDrop.first,
                                highestAverageDrop.second):
                        String.format("%s: %s",
                                context.getString(
                                        R.string.fragment_series_stats_highest_average_dec),
                                context.getString(
                                        R.string.fragment_series_stats_not_applicable)));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_series_stats, container, false);
        loadStats(view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats(getView());
    }
}
