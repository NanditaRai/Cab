package com.example.cab;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

public class Utils {
    public static SortedMap<Currency, Locale> currencyLocaleMap;


    /*Getting the currency symbol based on the device language*/
    static {
        currencyLocaleMap = new TreeMap<Currency, Locale>(new Comparator<Currency>() {
            public int compare(Currency c1, Currency c2) {
                return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
            }
        });
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                currencyLocaleMap.put(currency, locale);
            } catch (Exception e) {
            }
        }
    }


    public static String getCurrencySymbol(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        System.out.println(currencyCode + ":-" + currency.getSymbol(currencyLocaleMap.get(currency)));
        return currency.getSymbol(currencyLocaleMap.get(currency));
    }


    /*
     * Hiding soft keyboard
     */
    public static void hideKeyboard(@NonNull Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Show toast messages
     */
    public static void showToast(Context mContext,String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

}