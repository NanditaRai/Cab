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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Cab app.
 */
public final class CabContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private CabContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.cab";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CABS = "cabs";

    /**
     * Inner class  that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class CabEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CABS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CABS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CABS;

        /** Name of database table for pets */
        public final static String TABLE_NAME = "cabs";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_DAY ="day";
        public final static String COLUMN_FARE = "fare";
        public final static String COLUMN_SOURCE = "source";
        public final static String COLUMN_DESTINATION = "destination";
        public final static String COLUMN_TIME = "timeOfRide";
        public final static String COLUMN_NOTE = "note";

    }

}

