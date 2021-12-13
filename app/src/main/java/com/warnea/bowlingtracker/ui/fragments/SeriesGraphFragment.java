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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.models.League;
import com.warnea.bowlingtracker.models.Series;

import java.util.ArrayList;
import java.util.List;

public class SeriesGraphFragment extends Fragment {
    private League league;

    public SeriesGraphFragment() {
        super();
        setRetainInstance(true);
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
        View view = inflater.inflate(R.layout.fragment_series_graph, container, false);

        //create data points for the average after each series
        List<Series> seriesList = Series.getSavedSeriesByLeagueId(league.getId());
        List<DataPoint> dataPoints = new ArrayList<>();
        for(int i = seriesList.size() - 1, j = 1; i > -1; i--, j++) {
            Series series = seriesList.get(i);
            dataPoints.add(new DataPoint(j, series.getEndingAverage()));
        }

        //Create a graph
        GraphView graph = (GraphView) view.findViewById(
                R.id.series_graph_graphview_average_history);

        //Add the data points to the graph
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(
                dataPoints.toArray(new DataPoint[dataPoints.size()]));
        graph.addSeries(series);


        //Set the x axis to go from 1 to the amount of series
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(dataPoints.size());
        //set the amount of labels to the amount of series.  This prevents the graph from not
        //labeling some series or labeling half series
        graph.getGridLabelRenderer().setNumHorizontalLabels(dataPoints.size());

        return view;
    }
}
