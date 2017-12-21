/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cab.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cab.data.CabContract.CabEntry;

/**
 * Database helper for Cab app. Manages database creation and version management.
 */
public class CabDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = CabDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "cabService.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 5;

    public CabDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_CABS_TABLE =  "CREATE TABLE " + CabEntry.TABLE_NAME + " ("
                + CabEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CabEntry.COLUMN_DAY + " TEXT NOT NULL, "
                + CabEntry.COLUMN_FARE + " INTEGER NOT NULL DEFAULT 0, "
                + CabEntry.COLUMN_SOURCE + " TEXT NOT NULL, "
                + CabEntry.COLUMN_DESTINATION + " TEXT NOT NULL, "
                + CabEntry.COLUMN_TIME + " TEXT, "
                + CabEntry.COLUMN_NOTE + " TEXT );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CABS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
        db.execSQL("DROP TABLE IF EXISTS" + CabEntry.TABLE_NAME);
        onCreate(db);
    }
}