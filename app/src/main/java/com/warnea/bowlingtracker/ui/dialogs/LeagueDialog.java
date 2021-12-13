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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.ui.runnables.LeagueOkRunnable;

public class LeagueDialog extends DialogFragment {

    private LeagueOkRunnable okRunnable;
    private Runnable cancelRunnable;
    private View view;

    public LeagueDialog(LeagueOkRunnable ok, Runnable cancel) {
        this.okRunnable = ok;
        this.cancelRunnable = cancel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_league_dialog, container);
        this.view = view;

        Button okButton = view.findViewById(R.id.add_league_dialog_button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                EditText leagueName =
                        LeagueDialog.this.view.findViewById(
                                R.id.add_league_dialog_edittext_league_name);
                EditText playerName =
                        LeagueDialog.this.view.findViewById(
                                R.id.add_league_dialog_edittext_player_name);
                EditText teamName =
                        LeagueDialog.this.view.findViewById(
                                R.id.add_league_dialog_edittext_team_name);
                EditText teamNumber =
                        LeagueDialog.this.view.findViewById
                                (R.id.add_league_dialog_edittext_team_number);

                //require a league name
                if (leagueName.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder
                            .setMessage(context.getString(
                                    R.string.dialog_league_error_no_league_name))
                            .setTitle(context.getString(R.string.dialog_error_title))
                            .setPositiveButton(
                                    getString(android.R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }
                            )
                            .setIcon(R.drawable.ic_error_outline);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                okRunnable.setData(leagueName.getText().toString(), playerName.getText().toString(),
                        teamName.getText().toString(),
                        Integer.parseInt(
                                !teamNumber.getText().toString().equals("") ?
                                        teamNumber.getText().toString() :
                                        context.getString(
                                                R.string.dialog_league_default_team_number)
                        )
                );
                okRunnable.run();
                dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.add_league_dialog_button_cancel);
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
