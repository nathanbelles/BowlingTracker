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
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.warnea.bowlingtracker.R;
import com.warnea.bowlingtracker.models.League;
import com.warnea.bowlingtracker.ui.runnables.LeagueOkRunnable;

public class EditLeagueDialog extends LeagueDialog {

    private League league;

    public EditLeagueDialog(League league, LeagueOkRunnable ok, Runnable cancel) {
        super(ok, cancel);
        this.league = league;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        EditText leagueName = view.findViewById(R.id.add_league_dialog_edittext_league_name);
        EditText playerName = view.findViewById(R.id.add_league_dialog_edittext_player_name);
        EditText teamName = view.findViewById(R.id.add_league_dialog_edittext_team_name);
        EditText teamNumber = view.findViewById(R.id.add_league_dialog_edittext_team_number);

        leagueName.setText(league.getName());
        playerName.setText(league.getPlayerName());
        teamName.setText(league.getTeamName());
        teamNumber.setText(Integer.toString(league.getTeamNumber()));

        return view;
    }
}
