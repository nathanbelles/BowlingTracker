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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.models.League;
import com.warnea.bowlingtracker.ui.runnables.SeriesOkRunnable;

import java.time.LocalDate;

public class SeriesDialog extends DialogFragment {

    private SeriesOkRunnable okRunnable;
    private Runnable cancelRunnable;
    private View view;
    private League league;

    public SeriesDialog(SeriesOkRunnable ok, Runnable cancel, League league) {
        this.okRunnable = ok;
        this.cancelRunnable = cancel;
        this.league = league;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_series_dialog, container);
        this.view = view;

        Button okButton = view.findViewById(R.id.add_series_dialog_button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePicker seriesDate = SeriesDialog.this.view.findViewById(
                        R.id.add_series_dialog_datepicker_date);
                EditText hdcpPerGame = SeriesDialog.this.view.findViewById(
                        R.id.add_series_dialog_edittext_hdcp_per_game);

                LocalDate date = LocalDate.of(
                        seriesDate.getYear(),
                        seriesDate.getMonth() + 1,
                        seriesDate.getDayOfMonth());

                //if no handicap per game entered, assume it's 0
                short hdcp;
                try {
                    hdcp = Short.parseShort(hdcpPerGame.getText().toString());
                } catch (NumberFormatException ex) {
                    hdcp = 0;
                }

                okRunnable.setData(date, hdcp,
                        league);

                okRunnable.run();
                dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.add_series_dialog_button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRunnable.run();
                dismiss();
            }
        });
        return view;
    }
}
