package com.example.cab;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.cab.data.CabContract.CabEntry;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;


/**
 * Allows user to create a new ride or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,TimeDateSelector {

    private EditText mFareEditText;
    private EditText mSourceEditText;
    private EditText mDestinationEditText;
    private String mTimeOfRide;
    private String mDateOfRide;
    private Button mDateSetButton;
    private Button mTimeSetButton;
    private TextView mAddNote;
    private EditText mNote;

    private static final int EXISTING_PET_LOADER = 1;
    private int PLACE_PICKER_REQUEST = 1;
    int isSource = 1;

    private boolean mDataHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDataHasChanged = true;
            return false;
        }
    };

    /** Content URI for the existing pet (null if it's a new pet) */
    private Uri mCurrentCabUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mAddNote = (TextView)findViewById(R.id.add_note);
        mNote  = (EditText) findViewById(R.id.note);

        mCurrentCabUri = getIntent().getData();

        if (mCurrentCabUri == null) {
            setTitle("Add");
            invalidateOptionsMenu();
            mAddNote.setText(getString(R.string.want_to_add));
            mNote.setVisibility(View.GONE);
        } else {
            setTitle("Edit");
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
            mAddNote.setText(getString(R.string.want_to_edit));
        }

        //Setting the background of the page and making it translucent
        View pageView = (View) findViewById(R.id.page);
        pageView.getBackground().setAlpha(50);

        // Find all relevant views that we will need to read user input from
        mFareEditText = (EditText) findViewById(R.id.edit_fare);
        mSourceEditText = (EditText) findViewById(R.id.edit_source);
        mDestinationEditText = (EditText) findViewById(R.id.edit_destination);
        mTimeSetButton = (Button) findViewById(R.id.set_time);
        mDateSetButton = (Button) findViewById(R.id.set_date);

        mFareEditText.setOnTouchListener(mTouchListener);
        mFareEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mFareEditText.getWindowToken(), 0);
                }
                return false;
            }
        });
        mSourceEditText.setOnTouchListener(mTouchListener);
        mDestinationEditText.setOnTouchListener(mTouchListener);
        mNote.setOnTouchListener(mTouchListener);
        mNote.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mNote.getWindowToken(), 0);
                }
                return false;
            }
        });

        mTimeSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mDataHasChanged = true;
              showTimePickerDialog(EditorActivity.this);
            }
        });

        mDateSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mDataHasChanged = true;
              showDatePickerDialog(EditorActivity.this);
            }
        });

        mSourceEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    isSource = 1;
                    openPlacePicker();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }
        });

        mDestinationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    isSource = 0;
                    openPlacePicker();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }
        });

        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNote.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * Setting the time of ride chosen by the user
     */

    public void showTimePickerDialog( TimeDateSelector listener) {
        DialogUtils.TimePickerFragment newFragment = new DialogUtils.TimePickerFragment();
        newFragment.setListener(listener);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void timeSelected(String time) {
        mTimeOfRide = time;
    }


    /**
     * Setting the date of ride chosen by the user
     */

    public void showDatePickerDialog(TimeDateSelector listener) {
        DialogUtils.DatePickerFragment newFragment = new DialogUtils.DatePickerFragment();
        newFragment.setListener(listener);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void dateSelected(String date){
        mDateOfRide = date;
    }


    /**
     * Get user input from editor and save new pet into database.
     */
    private void savePet() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space

        String fareString = mFareEditText.getText().toString().trim();
        String sourceString = mSourceEditText.getText().toString().trim();
        String destinationString = mDestinationEditText.getText().toString().trim();
        String noteString = mNote.getText().toString().trim();

        if (mCurrentCabUri == null && TextUtils.isEmpty(mDateOfRide) && TextUtils.isEmpty(fareString)
                && TextUtils.isEmpty(mTimeOfRide) && TextUtils.isEmpty(sourceString)
                && TextUtils.isEmpty(destinationString)) {
            return;
        } else {
            ContentValues values = new ContentValues();
            values.put(CabEntry.COLUMN_DAY, mDateOfRide);

            // integer value. Use 0 by default.
            int fare = 0;
            if (!TextUtils.isEmpty(fareString)) {
                fare = Integer.parseInt(fareString);
            }

            values.put(CabEntry.COLUMN_FARE, fare);
            values.put(CabEntry.COLUMN_SOURCE, sourceString);
            values.put(CabEntry.COLUMN_DESTINATION, destinationString);
            values.put(CabEntry.COLUMN_TIME, mTimeOfRide);
            values.put(CabEntry.COLUMN_NOTE, noteString);

            if (mCurrentCabUri == null) {
                // Insert a new pet into the provider, returning the content URI for the new pet.
                Uri newUri = getContentResolver().insert(CabEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Utils.showToast(this, getString(R.string.editor_insert_failed));
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Utils.showToast(this, getString(R.string.editor_insert_successful));
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentCabUri, values, null, null);
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Utils.showToast(this, getString(R.string.editor_update_failed));
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Utils.showToast(this, getString(R.string.editor_update_successful));
                }
            }
        }
    }


    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        if (mCurrentCabUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentCabUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Utils.showToast(this, getString(R.string.editor_delete_failed));
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Utils.showToast(this, getString(R.string.editor_delete_successful));
            }
        }
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                CabEntry._ID,
                CabEntry.COLUMN_DAY,
                CabEntry.COLUMN_FARE,
                CabEntry.COLUMN_SOURCE,
                CabEntry.COLUMN_DESTINATION,
                CabEntry.COLUMN_TIME,
                CabEntry.COLUMN_NOTE};

        return new CursorLoader(this,   // Parent activity context
                mCurrentCabUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int dayColumnIndex = cursor.getColumnIndex(CabEntry.COLUMN_DAY);
            int fareColumnIndex = cursor.getColumnIndex(CabEntry.COLUMN_FARE);
            int sourceColumnIndex = cursor.getColumnIndex(CabEntry.COLUMN_SOURCE);
            int destinationColumnIndex = cursor.getColumnIndex(CabEntry.COLUMN_DESTINATION);
            int timeColumnIndex = cursor.getColumnIndex(CabEntry.COLUMN_TIME);
            int noteColumnIndex = cursor.getColumnIndex(CabEntry.COLUMN_NOTE);

            // Extract out the value from the Cursor for the given column index
            String day = cursor.getString(dayColumnIndex);
            int fare = cursor.getInt(fareColumnIndex);
            String source = cursor.getString(sourceColumnIndex);
            String destination = cursor.getString(destinationColumnIndex);
            String time = cursor.getString(timeColumnIndex);
            String note = cursor.getString(noteColumnIndex);

            mDateSetButton.setText(day);
            mDateOfRide = day;
            mFareEditText.setText(Integer.toString(fare));
            mSourceEditText.setText(source);
            mDestinationEditText.setText(destination);
            mTimeSetButton.setText(time);
            mTimeOfRide = time;
            mNote.setText(note);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mDataHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentCabUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                savePet();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mDataHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openPlacePicker() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                if(isSource == 0)
                    mDestinationEditText.setText(place.getName() + "," + place.getAddress());
                else
                    mSourceEditText.setText(place.getName() + "," + place.getAddress());
            }
        }
    }



}