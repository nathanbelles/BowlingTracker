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

package com.warnea.bowlingtracker.ui.runnables;

public abstract class LeagueOkRunnable implements Runnable {
    private String name;
    private String playerName;
    private String teamName;
    private int teamNumber;

    public void setData(String name, String playerName, String teamName, int teamNumber) {
        this.name = name;
        this.playerName = playerName;
        this.teamName = teamName;
        this.teamNumber = teamNumber;
    }

    public String getName() {
        return name;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getTeamNumber() {
        return teamNumber;
    }
}
