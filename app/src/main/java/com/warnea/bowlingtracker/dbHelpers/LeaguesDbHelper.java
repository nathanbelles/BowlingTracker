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

package com.warnea.bowlingtracker.dbHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.warnea.bowlingtracker.models.Game;
import com.warnea.bowlingtracker.models.League;
import com.warnea.bowlingtracker.models.Series;

public class LeaguesDbHelper extends SQLiteOpenHelper {
    // NOTE: When updating schema, increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "leagues.db";

    public LeaguesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(League.CREATE_TABLE_STRING);
        db.execSQL(Series.CREATE_TABLE_STRING);
        db.execSQL(Game.CREATE_TABLE_STRING);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 1st version, so just delete all the data
        db.execSQL(League.DELETE_TABLE_STRING);
        db.execSQL(Series.DELETE_TABLE_STRING);
        db.execSQL(Game.DELETE_TABLE_STRING);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
