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

import com.warnea.bowlingtracker.models.League;

import java.time.LocalDate;

public abstract class SeriesOkRunnable implements Runnable {
    private short hdcpPerGame;
    private League league;
    private LocalDate date;

    public void setData(LocalDate date, short hdcpPerGame, League league) {
        this.date = date;
        this.hdcpPerGame = hdcpPerGame;
        this.league = league;
    }

    public LocalDate getDate() {
        return date;
    }

    public League getLeague() {
        return league;
    }

    public short getHdcpPerGame() {
        return hdcpPerGame;
    }

}
