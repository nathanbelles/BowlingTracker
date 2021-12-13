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

package com.warnea.bowlingtracker.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.models.League;
import com.warnea.bowlingtracker.models.Series;
import com.warnea.bowlingtracker.ui.runnables.SeriesOkRunnable;

import java.time.LocalDate;

public class EditSeriesDialog extends SeriesDialog {
    private Series series;

    public EditSeriesDialog(SeriesOkRunnable ok, Runnable cancel, League league, Series series) {
        super(ok, cancel, league);
        this.series = series;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        DatePicker date = view.findViewById(R.id.add_series_dialog_datepicker_date);
        EditText hdcpPerGame = view.findViewById(R.id.add_series_dialog_edittext_hdcp_per_game);

        LocalDate localDate = series.getDate();
        date.updateDate(
                localDate.getYear(),
                localDate.getMonthValue() - 1,
                localDate.getDayOfMonth());
        hdcpPerGame.setText(Short.toString(series.getHdcpPerGame()));

        return view;
    }
}
