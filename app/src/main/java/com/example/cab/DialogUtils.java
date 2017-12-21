package com.example.cab;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nandita Rai on 2/21/2017.
 */

public class DialogUtils {

    /*Dialog to take the time input by the user*/
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        TimeDateSelector listener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String am_pm = getAMPM(hourOfDay);
            int hour = getHour(hourOfDay);
            String timeOfRide = String.format("%s:%s %s", String.valueOf(hour), String.valueOf(minute), am_pm);
            Button button = (Button) getActivity().findViewById(R.id.set_time);
            button.setText(timeOfRide);
            listener.timeSelected(timeOfRide);
        }

        private int getHour(int hourOfDay ) {
            if(hourOfDay > 12) hourOfDay -= 12 ;
            return hourOfDay;
        }

        private String getAMPM(int hourOfDay) {
            if(hourOfDay >= 12 ) return "PM";
            else  return "AM" ;
        }

        public void setListener(TimeDateSelector listener) {
            this.listener = listener;
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        TimeDateSelector listener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            String dayOfTheWeek = getDayFromDate(year,month,day-1);
            String dateOfRide = dayOfTheWeek + ","
                    + getResources().getStringArray(R.array.Name_Of_Month)[month]
                    + " " + String.valueOf(day) + ","
                    + (String.valueOf(year)).substring(2,4);

            Button button = (Button) getActivity().findViewById(R.id.set_date);
            button.setText(dateOfRide);
            listener.dateSelected(dateOfRide);
        }

        public void setListener(TimeDateSelector listener) {
            this.listener = listener;
        }
    }

    public static String getDayFromDate(int year, int month, int day){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date(year, month, day);
        String week = sdf.format(d);
        return (week.substring(0,3));
    }
}