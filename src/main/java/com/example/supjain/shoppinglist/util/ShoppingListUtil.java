package com.example.supjain.shoppinglist.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

import com.example.supjain.shoppinglist.R;

/**
 * This is a helper class containing helper methods.
 */
public class ShoppingListUtil {

    // Calculate number of columns to be displayed in GridView
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    // Check if device has network connection
    public static boolean hasNetworkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // Return drawable/image id based on list type
    public static int getShoppingListTypeIcon(String listType) {
        int listTypeIcon = R.drawable.ic_default_list;

        switch (listType) {
            case "Grocery":
                listTypeIcon = R.drawable.ic_grocery_list;
                break;
            case "Household Stuff":
                listTypeIcon = R.drawable.ic_household_stuff_list;
                break;
            case "Party":
                listTypeIcon = R.drawable.ic_party_list;
                break;
            case "Birthday":
                listTypeIcon = R.drawable.ic_birthday_list;
                break;
            case "Road Trip":
                listTypeIcon = R.drawable.ic_road_trip_list;
                break;
            case "Vacation":
                listTypeIcon = R.drawable.ic_vacation_list;
                break;
            case "Wedding":
                listTypeIcon = R.drawable.ic_wedding_list;
                break;
            case "Office Supply":
                listTypeIcon = R.drawable.ic_office_supply_list;
                break;
        }
        return listTypeIcon;
    }
}
