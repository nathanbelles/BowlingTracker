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

package com.warnea.bowlingtracker.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.warnea.bowlingtracker.dbHelpers.LeaguesDbHelper;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * The model for a Series object, including methods for saving and retrieving series from the
 * database.
 */
public class Series implements Serializable {

    ///////////////////////////////////////////////////////////////////////
    // Static variables
    ///////////////////////////////////////////////////////////////////////

    /**
     * The name of the table to store the Series objects in
     */
    public static final String TABLE = "Series";

    /**
     * The name of the column for the Series' id
     */
    public static final String TABLE_COLUMN_ID = "Id";
    /**
     *The name of the column for the series' date
     */
    public static final String TABLE_COLUMN_DATE = "Date";
    /**
     * The name of the column for the series' handicap per game
     */
    public static final String TABLE_COLUMN_HDCP_PER_GAME = "HdcpPerGame";
    /**
     * The name of the column for the id of the league the series belongs to
     */
    public static final String TABLE_COLUMN_LEAGUE_ID = "LeagueId";

    /**
     * The sql statement to create a Series table in the database
     */
    public static final String CREATE_TABLE_STRING =
            "CREATE TABLE " + TABLE + " (" +
                    TABLE_COLUMN_ID + " INTEGER PRIMARY KEY," +
                    TABLE_COLUMN_DATE + " INTEGER," +
                    TABLE_COLUMN_HDCP_PER_GAME + " INTEGER," +
                    TABLE_COLUMN_LEAGUE_ID + " INTEGER)";

    /**
     * The sql statement to drop a Series table in the database
     */
    public static final String DELETE_TABLE_STRING =
            "DROP TABLE IF EXISTS " + TABLE;

    private static LeaguesDbHelper dbHelper = null;

    ///////////////////////////////////////////////////////////////////////
    // Static variables
    ///////////////////////////////////////////////////////////////////////

    /**
     * Retrieves a Series object at the current cursor position
     * @param cursor The database cursor
     * @return The retrieved Series object
     */
    private static Series getSeriesFromCursor(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_ID));
        short hdcpPerGame = cursor.getShort(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_HDCP_PER_GAME));
        long leagueId = cursor.getLong(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_LEAGUE_ID)
        );
        LocalDate date = LocalDate.ofEpochDay(cursor.getLong(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_DATE)));

        League league = League.getSavedLeague(leagueId);

        return new Series(id, date, hdcpPerGame, league);
    }

    /**
     * Creates a database helper
     * @param context The context of the app
     */
    public static void createDbHelper(Context context) {
        dbHelper = new LeaguesDbHelper(context);
    }

    /**
     * Get a saved Series from the database based on its id
     * @param id The id of the Series to retrieve
     * @return The saved Series
     */
    public static Series getSavedSeries(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TABLE_COLUMN_ID,
                TABLE_COLUMN_DATE,
                TABLE_COLUMN_HDCP_PER_GAME,
                TABLE_COLUMN_LEAGUE_ID
        };

        String selection = TABLE_COLUMN_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        Cursor cursor = db.query(
                TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //if there is no series with the id, return a null result
        if(cursor.isAfterLast())
        {
            cursor.close();
            return null;
        }

        //move the cursor to the result
        cursor.moveToNext();

        //get the found series
        Series series = getSeriesFromCursor(cursor);

        cursor.close();

        return series;
    }

    /**
     * Get the most recent saved Series in a League
     * @param leagueId The id of the League to retrieve teh Series from
     * @return The most recent Series in the League
     */
    public static Series getLatestSavedSeriesInLeague(long leagueId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TABLE_COLUMN_ID,
                TABLE_COLUMN_DATE,
                TABLE_COLUMN_HDCP_PER_GAME,
                TABLE_COLUMN_LEAGUE_ID
        };

        String selection = TABLE_COLUMN_LEAGUE_ID + " = ?";
        String[] selectionArgs = {Long.toString(leagueId)};

        //sort by date, then by the order added
        String sortOrder =
                TABLE_COLUMN_DATE + " DESC," +
                TABLE_COLUMN_ID + " DESC";

        Cursor cursor = db.query(
                TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        //if there is no series with the id, return a null result
        if(cursor.isAfterLast()) {
            cursor.close();
            return null;
        }

        //get the found series (should be the latest one based on the sort)
        cursor.moveToNext();
        Series series = getSeriesFromCursor(cursor);
        cursor.close();

        return series;
    }

    /**
     * Gets all the saved Series in a League
     * @param leagueId The id of the League to retrieve the Series from
     * @return A list of all Series objects in the League
     */
    public static List<Series> getSavedSeriesByLeagueId(long leagueId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TABLE_COLUMN_ID,
                TABLE_COLUMN_DATE,
                TABLE_COLUMN_HDCP_PER_GAME,
                TABLE_COLUMN_LEAGUE_ID
        };

        //sort by date, then by the order added
        String sortOrder =
                TABLE_COLUMN_DATE + " DESC," +
                TABLE_COLUMN_ID + " DESC";

        String selection = TABLE_COLUMN_LEAGUE_ID + " = ?";
        String[] selectionArgs = {Long.toString(leagueId)};

        Cursor cursor = db.query(
                TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );


        //Create a linked list of all series
        List<Series> series = new LinkedList<>();
        while (cursor.moveToNext()) {
            series.add(getSeriesFromCursor(cursor));
        }
        cursor.close();

        return series;
    }

    /**
     * Deletes all Series belonging to a League
     * @param leagueId The id of the League to delete the Series from
     */
    public static void deleteByLeagueId(long leagueId) {
        List<Series> seriesList = getSavedSeriesByLeagueId(leagueId);
        seriesList.forEach((series) -> Game.deleteBySeriesId(series.id));

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = TABLE_COLUMN_LEAGUE_ID + " = ?";
        String[] selectionArgs = {Long.toString(leagueId)};
        db.delete(TABLE, selection, selectionArgs);
    }

    ///////////////////////////////////////////////////////////////////////
    // private variables
    ///////////////////////////////////////////////////////////////////////

    private long id;
    private short hdcpPerGame;
    private League league;
    private LocalDate date;

    ///////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////

    /**
     * Creates a Series with a specified id
     * @param id The id of the Series
     * @param date The date of the Series
     * @param hdcpPerGame The handicap per game of the Series
     * @param league The League the Series belongs to
     */
    private Series(long id, LocalDate date, short hdcpPerGame, League league) {
        this.id = id;
        this.date = date;
        this.hdcpPerGame = hdcpPerGame;
        this.league = league;
    }

    /**
     * Creates a Series with auto-generated id
     * @param date The date of the Series
     * @param hdcpPerGame The handicap per game of the Series
     * @param league The League the Series belongs to
     */
    public Series(LocalDate date, short hdcpPerGame, League league) {
        this(0, date, hdcpPerGame, league);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_COLUMN_HDCP_PER_GAME, hdcpPerGame);
        values.put(TABLE_COLUMN_LEAGUE_ID, league.getId());
        values.put(TABLE_COLUMN_DATE, date.toEpochDay());

        this.id = db.insert(TABLE, null, values);

    }

    ///////////////////////////////////////////////////////////////////////
    // private methods
    ///////////////////////////////////////////////////////////////////////

    /**
     * Get all the saved Series in a League that come before this one
     * @return A list of all Series object in the League that come before this one
     */
    private List<Series> getPrevSavedSeriesByLeagueId() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TABLE_COLUMN_ID,
                TABLE_COLUMN_DATE,
                TABLE_COLUMN_HDCP_PER_GAME,
                TABLE_COLUMN_LEAGUE_ID
        };


        //Gets series in the league and before the current day, or on the current day but before
        //this series
        //LeagueId = {leagueId} AND (Date  < {date} OR ( Date  = {date} AND Id  < {id}))
        String selection = TABLE_COLUMN_LEAGUE_ID +
                " = ? AND (" +
                TABLE_COLUMN_DATE +
                " < ? OR ( " +
                TABLE_COLUMN_DATE +
                " = ? AND " +
                TABLE_COLUMN_ID +
                "  < ?))";
        String[] selectionArgs = {
                Long.toString(league.getId()),
                Long.toString(date.toEpochDay()),
                Long.toString(date.toEpochDay()),
                Long.toString(id)
        };

        Cursor cursor = db.query(
                TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //Create a linked list of all series w/ the league id
        List<Series> series = new LinkedList<>();
        while (cursor.moveToNext()) {
            series.add(getSeriesFromCursor(cursor));
        }
        cursor.close();

        return series;
    }

    ///////////////////////////////////////////////////////////////////////
    // public methods
    ///////////////////////////////////////////////////////////////////////

    /**
     * Saves the Series to the database
     */
    public void save() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_COLUMN_HDCP_PER_GAME, hdcpPerGame);
        values.put(TABLE_COLUMN_LEAGUE_ID, league.getId());
        values.put(TABLE_COLUMN_DATE, date.toEpochDay());

        String selection = TABLE_COLUMN_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        db.update(
                TABLE,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Deletes the Series from the database
     */
    public void delete() {
        Game.deleteBySeriesId(id);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = TABLE_COLUMN_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};
        db.delete(TABLE, selection, selectionArgs);


    }

    /**
     * Gets the Series' id
     * @return The Series' id
     */
    public long getId() {
        return id;
    }

    /**
     * Get the date the Series' took place
     * @return The date the Series' took place
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Get the Series' starting average
     * @return The Series' starting average
     */
    public short getStartingAverage() {
        int total = 0;
        int count = 0;

        for(Series series : getPrevSavedSeriesByLeagueId()) {
            total += series.getScratchTotal();
            count += series.getNumberOfGames();
        }

        if(count == 0) {
            return 0;
        }
        return (short) (total/count);
    }

    /**
     * Get the Series' handicap per game
     * @return The Series' handicap per game
     */
    public short getHdcpPerGame() {
        return hdcpPerGame;
    }

    /**
     * Get the League the Series belongs to
     * @return The League the Series belongs to
     */
    public League getLeague() {
        return league;
    }

    /**
     * Sets the Series' date and handicap per game
     * @param date The date the Series took place
     * @param hdcpPerGame The handicap per game
     */
    public void setValues(LocalDate date, short hdcpPerGame) {
        this.date = date;
        this.hdcpPerGame = hdcpPerGame;

        save();
    }

    /**
     * Get the amount of saved Games that belong to the Series
     * @return The amount of saved Games that belong to the Series
     */
    public int getNumberOfGames() {
        return Game.getSavedGamesBySeriesId(id).size();
    }

    /**
     * Get the scratch total of all the saved Games in the Series
     * @return The scratch total of all the saved Games in the Series
     */
    public short getScratchTotal() {
        short total = 0;
        for(Game game : Game.getSavedGamesBySeriesId(id)) {
            total += game.getScratchTotal();
        }
        return total;
    }

    /**
     * Get the handicap total of all the saved Games in the Series
     * @return The handicap total of all the saved Games in the Series
     */
    public short getHdcpTotal() {
        short total = 0;
        for(Game game : Game.getSavedGamesBySeriesId(id)) {
            total += game.getHdcpTotal();
        }
        return total;
    }

    /**
     * Get the average after all the saved Games in the Series
     * @return The average after all the saved Games in the Series
     */
    public short getEndingAverage() {
        //set the total to the scratch total of all the games in the series
        int total = getScratchTotal();
        //set the count to the number of games in the series
        int count = getNumberOfGames();

        //add the scratch totals of all previous series
        //add the amount of games in each previous series
        for(Series series : getPrevSavedSeriesByLeagueId()) {
            total += series.getScratchTotal();
            count += series.getNumberOfGames();
        }

        //prevents a divide by 0 error, if no games played
        if(count == 0) {
            return 0;
        }

        //return the average after this series
        //(scratch total of all games / # of games)
        return (short) (total/count);
    }


}
