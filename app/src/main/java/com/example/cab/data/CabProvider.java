package com.example.cab.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.cab.data.CabContract.CabEntry;

/**
 * {@link ContentProvider} for Cab app.
 */
public class CabProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = CabProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int CABS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int CAB_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(CabContract.CONTENT_AUTHORITY, CabContract.PATH_CABS, CABS);
        sUriMatcher.addURI(CabContract.CONTENT_AUTHORITY, CabContract.PATH_CABS + "/#", CAB_ID);
    }

    /** Database helper object */
    private CabDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new CabDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch(match){
            case CABS:
                cursor = database.query(CabEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case(CAB_ID):
                selection = CabEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(CabEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri" + uri);
        }

        //If the data at this URI changes, then we know we need to update he cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
         switch (match){
             case CABS:
                 return insertCab(uri, contentValues);
             default:
                 throw new IllegalArgumentException("Unknown Uri" + uri);
         }
    }

    private Uri insertCab(Uri uri, ContentValues values) {
        // Check that the name is not null
        String day = values.getAsString(CabEntry.COLUMN_DAY);
        if (day == null) {
            throw new IllegalArgumentException("Day is required");
        }

        // If the FARE is provided, check that it's greater than or equal to 0
        Integer fare = values.getAsInteger(CabEntry.COLUMN_FARE);
        if (fare != null && fare < 0) {
            throw new IllegalArgumentException("Requires valid fare");
        }

        // Check the source is not null
        String source = values.getAsString(CabEntry.COLUMN_SOURCE);
        if(source == null){
            throw new IllegalArgumentException("Source is required");
        }

        // Check the destination is not null
        String destination = values.getAsString(CabEntry.COLUMN_DESTINATION);
        if(destination == null){
            throw new IllegalArgumentException("Destination is required");
        }

        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(CabEntry.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all the listeners that data has changed for the pet content uri
        getContext().getContentResolver().notifyChange(uri , null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CABS:
                return updateCab(uri, contentValues, selection, selectionArgs);
            case CAB_ID:
                // For the CAB_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = CabEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateCab(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateCab(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_DAY} key is present,
        // check that the date value is not null.
        if (values.containsKey(CabEntry.COLUMN_DAY)) {
            String name = values.getAsString(CabEntry.COLUMN_DAY);
            if (name == null) {
                throw new IllegalArgumentException("Requires a day name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(CabEntry.COLUMN_FARE)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer fare = values.getAsInteger(CabEntry.COLUMN_FARE);
            if (fare != null && fare < 0) {
                throw new IllegalArgumentException("Requires valid fare");
            }
        }

        //Check that source value is not null
        if (values.containsKey(CabEntry.COLUMN_SOURCE)) {
            String source = values.getAsString(CabEntry.COLUMN_SOURCE);
            if (source == null) {
                throw new IllegalArgumentException("Requires a source name");
            }
        }

        //Check that destination value is not null
        if (values.containsKey(CabEntry.COLUMN_DESTINATION)) {
            String destination = values.getAsString(CabEntry.COLUMN_DESTINATION);
            if (destination == null) {
                throw new IllegalArgumentException("Requires a destination name");
            }
        }

        //cHECK THE TIME VALUE
        if (values.containsKey(CabEntry.COLUMN_TIME)) {
            String time = values.getAsString(CabEntry.COLUMN_TIME);
            if (time == null) {
                throw new IllegalArgumentException("Requires a valid time");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated =  database.update(CabEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CABS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CabEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CAB_ID:
                // Delete a single row given by the ID in the URI
                selection = CabEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(CabEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CABS:
                return CabEntry.CONTENT_LIST_TYPE;
            case CAB_ID:
                return CabEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}