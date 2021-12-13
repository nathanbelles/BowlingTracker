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
import java.util.LinkedList;
import java.util.List;

/**
 * The model for a League object, including methods for saving and retrieving leagues from the
 * database.
 */
public class League implements Serializable {

    ///////////////////////////////////////////////////////////////////////
    // Static variables
    ///////////////////////////////////////////////////////////////////////

    /**
     * The name of the table to store League objects in
     */
    public static final String TABLE = "League";
    /**
     * The name of the column for the League's id
     */
    public static final String TABLE_COLUMN_ID = "Id";
    /**
     * The name of the column for the League's name
     */
    public static final String TABLE_COLUMN_NAME = "Name";
    /**
     * The name of the column for the League's player name
     */
    public static final String TABLE_COLUMN_PLAYER_NAME = "PlayerName";
    /**
     * The name of the column for the League's team name
     */
    public static final String TABLE_COLUMN_TEAM_NAME = "TeamName";
    /**
     * The name of the column for the League's team number
     */
    public static final String TABLE_COLUMN_TEAM_NUMBER = "TeamNumber";

    /**
     * The sql statement to create a League table in the database
     */
    public static final String CREATE_TABLE_STRING =
            "CREATE TABLE " + TABLE + " (" +
                    TABLE_COLUMN_ID + " INTEGER PRIMARY KEY," +
                    TABLE_COLUMN_NAME + " TEXT," +
                    TABLE_COLUMN_PLAYER_NAME + " TEXT," +
                    TABLE_COLUMN_TEAM_NAME + " TEXT," +
                    TABLE_COLUMN_TEAM_NUMBER + " INTEGER)";

    /**
     * The sql statement to drop a League table in the database
     */
    public static final String DELETE_TABLE_STRING =
            "DROP TABLE IF EXISTS " + TABLE;

    private static LeaguesDbHelper dbHelper = null;

    ///////////////////////////////////////////////////////////////////////
    // Static methods
    ///////////////////////////////////////////////////////////////////////

    /**
     * Retrieves the League object at the current cursor position
     * @param cursor The database cursor
     * @return The retrieved League object
     */
    private static League getLeagueFromCursor(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_ID));
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_NAME));
        String playerName = cursor.getString(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_PLAYER_NAME));
        String teamName = cursor.getString(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_TEAM_NAME));
        int teamNumber = cursor.getInt(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_TEAM_NUMBER));

        return new League(id, name, playerName, teamName, teamNumber);
    }

    /**
     * Creates a database helper
     * @param context The context of the app
     */
    public static void createDbHelper(Context context) {
        dbHelper = new LeaguesDbHelper(context);
    }

    /**
     * Gets a saved League from the database based on its id
     * @param id
     * @return
     */
    public static League getSavedLeague(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TABLE_COLUMN_ID,
                TABLE_COLUMN_NAME,
                TABLE_COLUMN_PLAYER_NAME,
                TABLE_COLUMN_TEAM_NAME,
                TABLE_COLUMN_TEAM_NUMBER
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
        cursor.moveToNext();

        League league = getLeagueFromCursor(cursor);

        cursor.close();

        return league;
    }

    /**
     * Gets all the saved leagues
     * @return A List of all the League objects
     */
    public static List<League> getSavedLeagues() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TABLE_COLUMN_ID,
                TABLE_COLUMN_NAME,
                TABLE_COLUMN_PLAYER_NAME,
                TABLE_COLUMN_TEAM_NAME,
                TABLE_COLUMN_TEAM_NUMBER
        };


        // Sort leagues by the order they were added
        String sortOrder =
                TABLE_COLUMN_ID + " DESC";

        Cursor cursor = db.query(
                TABLE,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        //add all the leagues from the database to a linked list
        List<League> leagues = new LinkedList<>();
        while (cursor.moveToNext()) {
            leagues.add(getLeagueFromCursor(cursor));
        }
        cursor.close();

        return leagues;
    }

    ///////////////////////////////////////////////////////////////////////
    // private variables
    ///////////////////////////////////////////////////////////////////////

    private long id;
    private String name;
    private String playerName;
    private String teamName;
    private int teamNumber;

    ///////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////
    /**
     * Creates a League object with a specified id
     * @param id The id of the League
     * @param name The name of the League
     * @param playerName The name of the player
     * @param teamName The team name
     * @param teamNumber The team number
     */
    private League(long id, String name, String playerName, String teamName, int teamNumber) {
        this.id = id;
        this.name = name;
        this.playerName = playerName;
        this.teamName = teamName;
        this.teamNumber = teamNumber;
    }

    /**
     * Creates a League with an auto-generated id
     * @param name The name of the League
     * @param playerName The name of the player
     * @param teamName The team name
     * @param teamNumber The team number
     */
    public League(String name, String playerName, String teamName, int teamNumber) {
        this(0, name, playerName, teamName, teamNumber);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_COLUMN_NAME, name);
        values.put(TABLE_COLUMN_PLAYER_NAME, playerName);
        values.put(TABLE_COLUMN_TEAM_NAME, teamName);
        values.put(TABLE_COLUMN_TEAM_NUMBER, teamNumber);

        this.id = db.insert(TABLE, null, values);

    }

    ///////////////////////////////////////////////////////////////////////
    // public methods
    ///////////////////////////////////////////////////////////////////////

    /**
     * Saves the League to the database
     */
    public void save() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_COLUMN_NAME, name);
        values.put(TABLE_COLUMN_PLAYER_NAME, playerName);
        values.put(TABLE_COLUMN_TEAM_NAME, teamName);
        values.put(TABLE_COLUMN_TEAM_NUMBER, teamNumber);

        String selection = TABLE_COLUMN_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        db.update(
                TABLE,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Deletes the League from the database
     */
    public void delete() {
        Series.deleteByLeagueId(id);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = TABLE_COLUMN_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};
        db.delete(TABLE, selection, selectionArgs);


    }

    /**
     * Get's the League's id
     * @return The League's id
     */
    public long getId() {
        return id;
    }

    /**
     * Get the League's name
     * @return The League's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get's the player's name
     * @return The player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the team's name
     * @return The team's name
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Gets the team's number
     * @return The team's number
     */
    public int getTeamNumber() {
        return teamNumber;
    }

    /**
     * Set the league name, player's name, team's name, and team's number
     * @param name The league name
     * @param playerName The player's name
     * @param teamName The team's name
     * @param teamNumber The team's number
     */
    public void setValues(String name, String playerName, String teamName, int teamNumber){
        this.name = name;
        this.playerName = playerName;
        this.teamName = teamName;
        this.teamNumber = teamNumber;
        save();
    }

    /**
     * Get the current average in the league
     * @return The current average
     */
    public short getAverage() {
        Series series = Series.getLatestSavedSeriesInLeague(id);
        if(series == null) {
            return 0;
        }
        return series.getEndingAverage();
    }
}
