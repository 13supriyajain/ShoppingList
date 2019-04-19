package com.example.supjain.shoppinglist.util;

import android.annotation.SuppressLint;
import android.widget.ImageView;

import com.example.supjain.shoppinglist.R;
import com.example.supjain.shoppinglist.adapters.ItemListAdapter;
import com.example.supjain.shoppinglist.adapters.ShoppingListsAdapter;
import com.example.supjain.shoppinglist.data.ShoppingList;
import com.example.supjain.shoppinglist.data.Store;

import java.util.List;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class BindingUtil {

    @SuppressLint("CrossDomainBindingAdapterIssue")
    @BindingAdapter({"src"})
    public static void loadImage(ImageView view, int imageId) {
        if (imageId > 0)
            view.setImageResource(imageId);
        else
            view.setImageResource(R.drawable.default_list_img);
    }

    @SuppressLint("CrossDomainBindingAdapterIssue")
    @BindingAdapter({"listdata"})
    public static void setList(RecyclerView view, List<?> data) {
        if (!data.isEmpty()) {
            if (data.get(0) instanceof ShoppingList) {
                ShoppingListsAdapter adapter = (ShoppingListsAdapter) view.getAdapter();
                if (adapter != null)
                    adapter.setShoppingLists((List<ShoppingList>) data);
            } else if (data.get(0) instanceof Store) {
                ItemListAdapter adapter = (ItemListAdapter) view.getAdapter();
                if (adapter != null)
                    adapter.setStoresList((List<Store>) data);
            }
        }

    }
}
