package com.example.cab;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.cab.data.CabContract;

import org.w3c.dom.Text;

/**
 * This is an adapter for a list or grid view
 * that uses a {@link Cursor} of cab data as its data source. This adapter knows
 * how to create list items for each row of cab data in the cursor.
 */
public class CabCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link CabCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public CabCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dayTextView = (TextView) view.findViewById(R.id.day);
        TextView fareTextView = (TextView) view.findViewById(R.id.fare);
        TextView sourceView = (TextView) view.findViewById(R.id.source);
        TextView destinationView = (TextView) view.findViewById(R.id.destination);

        int dayColumnIndex = cursor.getColumnIndex(CabContract.CabEntry.COLUMN_DAY);
        int timeColumnIndex = cursor.getColumnIndex(CabContract.CabEntry.COLUMN_TIME);
        int fareColumnIndex = cursor.getColumnIndex(CabContract.CabEntry.COLUMN_FARE);
        int sourceColumnIndex = cursor.getColumnIndex(CabContract.CabEntry.COLUMN_SOURCE);
        int destinationColumnIndex = cursor.getColumnIndex(CabContract.CabEntry.COLUMN_DESTINATION);

        String date = cursor.getString(dayColumnIndex);
        String fare = cursor.getString(fareColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String source = cursor.getString(sourceColumnIndex);
        String destination = cursor.getString(destinationColumnIndex);

        dayTextView.setText(date + "\n" + time);
        String symbol = Utils.getCurrencySymbol("INR");
        fareTextView.setText(symbol + fare);
        sourceView.setText(source);
        destinationView.setText(destination);

    }
}