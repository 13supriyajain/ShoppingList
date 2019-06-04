package com.example.supjain.shoppinglist.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.supjain.shoppinglist.DisplayShoppingListActivity;
import com.example.supjain.shoppinglist.R;
import com.example.supjain.shoppinglist.data.Item;
import com.example.supjain.shoppinglist.data.ShoppingList;
import com.example.supjain.shoppinglist.data.Store;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.TaskStackBuilder;

import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_LIST_OBJ_KEY;

public class ShoppingListWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                ShoppingList list, int appWidgetId) {
        // Create an Intent to launch DisplayShoppingListActivity when clicked
        Intent intent = new Intent(context, DisplayShoppingListActivity.class);
        intent.putParcelableArrayListExtra(STORE_LIST_OBJ_KEY, (ArrayList<Store>) list.getStores());
        intent.putExtra(SHOPPING_LIST_NAME_KEY, list.getShoppingListName());

        // Use TaskStackBuilder to build the back stack and get the PendingIntent
        PendingIntent pendingIntent =
                TaskStackBuilder.create(context)
                        // add all of DisplayShoppingListActivity's parents to the stack (MainActivity),
                        // followed by DisplayShoppingListActivity itself
                        .addNextIntentWithParentStack(intent)
                        .getPendingIntent(appWidgetId, PendingIntent.FLAG_UPDATE_CURRENT);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shopping_list_widget);
        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_shopping_list_items, pendingIntent);
        views.setTextViewText(R.id.widget_shopping_list_items, getShoppingListData(list));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        ShoppingList list, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds)
            updateAppWidget(context, appWidgetManager, list, appWidgetId);
    }

    // This method takes a shopping list and return a String containing information
    // about all the stores and items in the list, to be displayed in the widget.
    private static String getShoppingListData(ShoppingList shoppingList) {
        StringBuilder shoppingListData = new StringBuilder();
        if (shoppingList != null) {
            shoppingListData.append("<<").append(shoppingList.getShoppingListName())
                    .append(">>\n------------\n");
            List<Store> storeList = shoppingList.getStores();
            if (storeList != null && !storeList.isEmpty()) {
                for (Store store : storeList) {
                    shoppingListData.append(store.getStoreName()).append(":\n");
                    List<Item> itemList = store.getItems();
                    if (itemList != null && !itemList.isEmpty()) {
                        for (Item item : itemList) {
                            shoppingListData.append(item.toString()).append("\n");
                        }
                    }
                    shoppingListData.append("------------\n");
                }
            }
        }
        return shoppingListData.toString();
    }
}
