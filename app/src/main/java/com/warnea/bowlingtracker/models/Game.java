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

import java.util.LinkedList;
import java.util.List;

/**
 * The model for a Game object, including methods for saving and retrieving games from the database.
 */
public class Game {

    ///////////////////////////////////////////////////////////////////////
    // Static variables
    ///////////////////////////////////////////////////////////////////////

    /**
     * The name of the table to store the Game objects in
     */
    public static final String TABLE = "Game";
    /**
     * The name of the column for the Game's id
     */
    public static final String TABLE_COLUMN_ID = "Id";
    /**
     * The name of the column for the Game's scratch total
     */
    public static final String TABLE_COLUMN_SCRATCH_TOTAL = "ScratchTotal";
    /**
     * The name of the column for the id of the series the game belongs to
     */
    public static final String TABLE_COLUMN_SERIES_ID = "SeriesId";

    /**
     *  The sql statement to create a Game table in the database
     */
    public static final String CREATE_TABLE_STRING =
            "CREATE TABLE " + TABLE + " (" +
                    TABLE_COLUMN_ID + " INTEGER PRIMARY KEY," +
                    TABLE_COLUMN_SCRATCH_TOTAL + " INTEGER," +
                    TABLE_COLUMN_SERIES_ID + " INTEGER)";

    /**
     * The sql statement to drop a Game table in the database
     */
    public static final String DELETE_TABLE_STRING =
            "DROP TABLE IF EXISTS " + TABLE;

    private static LeaguesDbHelper dbHelper = null;

    ///////////////////////////////////////////////////////////////////////
    // Static variables
    ///////////////////////////////////////////////////////////////////////

    /**
     * Retrieves the Game object at the current cursor position
     * @param cursor The database cursor
     * @return The retrieved Game object
     */
    private static Game getGameFromCursor(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_ID));
        short scratchTotal = cursor.getShort(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_SCRATCH_TOTAL));
        long seriesId = cursor.getLong(
                cursor.getColumnIndexOrThrow(TABLE_COLUMN_SERIES_ID)
        );

        Series series = Series.getSavedSeries(seriesId);

        return new Game(id, scratchTotal, series);
    }

    /**
     * Creates a database helper
     * @param context The context of the app
     */
    public static void createDbHelper(Context context) {
        dbHelper = new LeaguesDbHelper(context);
    }

    /**
     * Gets a saved Game from the database based on its id
     * @param id The id of the Game to retrieve
     * @return The saved Game
     */
    public static Game getSavedGame(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TABLE_COLUMN_ID,
                TABLE_COLUMN_SCRATCH_TOTAL,
                TABLE_COLUMN_SERIES_ID
        };

        String selection = TABLE_COLUMN_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        //search the database for the id
        Cursor cursor = db.query(
                TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //No result found
        if(cursor.isAfterLast()) {
            cursor.close();
            return null;
        }

        //Move the cursor to the first result
        cursor.moveToNext();

        //Get the game
        Game game = getGameFromCursor(cursor);

        cursor.close();

        return game;
    }

    /**
     * Gets all the saved Games for a series.
     * @param seriesId The id of the Series to retrieve the Games from
     * @return A List of all Game objects in the series
     */
    public static List<Game> getSavedGamesBySeriesId(long seriesId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TABLE_COLUMN_ID,
                TABLE_COLUMN_SCRATCH_TOTAL,
                TABLE_COLUMN_SERIES_ID
        };

        //We want to sort the games by the order in which they were added
        String sortOrder =
                TABLE_COLUMN_ID + " ASC";

        String selection = TABLE_COLUMN_SERIES_ID + " = ?";
        String[] selectionArgs = {Long.toString(seriesId)};

        Cursor cursor = db.query(
                TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        //get each game and add it to the list
        List<Game> games = new LinkedList<>();
        while (cursor.moveToNext()) {
            games.add(getGameFromCursor(cursor));
        }
        cursor.close();

        return games;
    }

    /**
     * Deletes all Games belonging to a Series
     * @param seriesId The id of the Series to delete the Games from
     */
    public static void deleteBySeriesId(long seriesId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = TABLE_COLUMN_SERIES_ID + " = ?";
        String[] selectionArgs = {Long.toString(seriesId)};

        //delete all games belonging to the series
        db.delete(TABLE, selection, selectionArgs);
    }

    ///////////////////////////////////////////////////////////////////////
    // private variables
    ///////////////////////////////////////////////////////////////////////

    private long id;
    private short scratchTotal;
    private Series series;

    ///////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////

    /**
     * Creates a Game with a specified id
     * @param id The id of the Game
     * @param scratchTotal The scratch total of the game
     * @param series The Series the Game belongs to
     */
    private Game(long id, short scratchTotal, Series series) {
        this.id = id;
        this.scratchTotal = scratchTotal;
        this.series = series;
    }

    /**
     * Creates a Game with an auto-generated id
     * @param scratchTotal The scratch total of the Game
     * @param series The Series the Game belongs to
     */
    public Game(short scratchTotal, Series series) {
        this(0, scratchTotal, series);

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TABLE_COLUMN_SCRATCH_TOTAL, scratchTotal);
        values.put(TABLE_COLUMN_SERIES_ID, series.getId());

        // Insert the new row, returning the primary key value of the new row
        this.id = db.insert(TABLE, null, values);

    }

    ///////////////////////////////////////////////////////////////////////
    // public methods
    ///////////////////////////////////////////////////////////////////////

    /**
     * Saves the Game to the database
     */
    public void save() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_COLUMN_SCRATCH_TOTAL, scratchTotal);
        values.put(TABLE_COLUMN_SERIES_ID, series.getId());

        String selection = TABLE_COLUMN_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        db.update(
                TABLE,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Deletes the Game from the database
     */
    public void delete() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = TABLE_COLUMN_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        db.delete(TABLE, selection, selectionArgs);
    }

    /**
     * Gets the Game's scratch total
     * @return The Game's scratch total
     */
    public short getScratchTotal() {
        return scratchTotal;
    }

    /**
     * Sets the Game's scratch total
     * @param scratchTotal The Game's scratch total
     */
    public void setValues(short scratchTotal) {
        this.scratchTotal = scratchTotal;
        save();
    }

    /**
     * Gets the Series the game belongs to
     * @return The Series the game belongs to
     */
    public Series getSeries() {
        return series;
    }

    /**
     * Gets the total with handicap for the Game
     * @return The total with handicap for the Game
     */
    public short getHdcpTotal() {
        return (short) (scratchTotal + series.getHdcpPerGame());
    }
}
