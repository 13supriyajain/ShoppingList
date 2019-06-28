package com.example.supjain.shoppinglist.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.supjain.shoppinglist.R;
import com.example.supjain.shoppinglist.data.Item;
import com.example.supjain.shoppinglist.data.Store;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.supjain.shoppinglist.util.Constants.CHECK_ITEM_OP;
import static com.example.supjain.shoppinglist.util.Constants.DELETE_ITEM_OP;
import static com.example.supjain.shoppinglist.util.Constants.EDIT_ITEM_OP;
import static com.example.supjain.shoppinglist.util.Constants.UNCHECK_ITEM_OP;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListAdapterViewHolder> {
    private ItemListAdapterOnClickHandler clickHandler;
    private List<Store> storesList;
    private HashMap<Integer, Object> displayIndexMap;

    public ItemListAdapter(ItemListAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ItemListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_list_entry, parent, false);
        return new ItemListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapterViewHolder holder, int position) {
        int viewPosition = holder.getAdapterPosition();
        displayIndexMap = calculateDisplayIndices();
        if (displayIndexMap != null) {
            Object viewObj = displayIndexMap.get(viewPosition);
            if (viewObj instanceof String)
                holder.displayStoreName((String) viewObj);
            else if (viewObj instanceof Item)
                holder.displayItem((Item) viewObj);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (storesList != null && !storesList.isEmpty()) {
            count = storesList.size();
            for (Store store : storesList) {
                List<Item> itemsList = store.getItems();
                if (!itemsList.isEmpty())
                    count += itemsList.size();
            }
        }
        return count;
    }

    /**
     * Returns an index map to help decide whether to store name or item entry for the recyclerview's
     * current position.
     **/
    private HashMap<Integer, Object> calculateDisplayIndices() {
        HashMap<Integer, Object> indexMap = null;
        int count = 0;

        if (!storesList.isEmpty()) {
            indexMap = new HashMap<>();

            for (Store store : storesList) {
                if (store != null && !TextUtils.isEmpty(store.getStoreName())) {
                    indexMap.put(count, store.getStoreName());
                    count++;
                    List<Item> itemsList = store.getItems();
                    for (Item item : itemsList) {
                        indexMap.put(count, item);
                        count++;
                    }
                }
            }
        }
        return indexMap;
    }

    public void setStoresList(List<Store> stores) {
        this.storesList = stores;
        notifyDataSetChanged();
    }

    public interface ItemListAdapterOnClickHandler {
        void mClick(String operation, Item item);
    }

    public class ItemListAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView storeNameTextView;
        LinearLayout itemEntryContainer;
        CheckBox itemNameCheckbox;
        ImageButton itemEditBtn;
        ImageButton itemDeleteBtn;

        ItemListAdapterViewHolder(View view) {
            super(view);
            storeNameTextView = view.findViewById(R.id.store_name_textview);
            itemEntryContainer = view.findViewById(R.id.item_entry_container);
            itemNameCheckbox = view.findViewById(R.id.item_name_checkbox);
            itemEditBtn = view.findViewById(R.id.item_edit_btn);
            itemDeleteBtn = view.findViewById(R.id.item_delete_btn);

            itemNameCheckbox.setOnClickListener(this);
            itemEditBtn.setOnClickListener(this);
            itemDeleteBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int clickIndex = getAdapterPosition();

            switch (id) {
                case R.id.item_edit_btn:
                    clickHandler.mClick(EDIT_ITEM_OP, (Item) displayIndexMap.get(clickIndex));
                    break;
                case R.id.item_delete_btn:
                    clickHandler.mClick(DELETE_ITEM_OP, (Item) displayIndexMap.get(clickIndex));
                    break;
                case R.id.item_name_checkbox:
                    if (itemNameCheckbox.isChecked()) {
                        itemNameCheckbox.setPaintFlags(itemNameCheckbox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        clickHandler.mClick(CHECK_ITEM_OP, (Item) displayIndexMap.get(clickIndex));
                    } else {
                        itemNameCheckbox.setPaintFlags(itemNameCheckbox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        clickHandler.mClick(UNCHECK_ITEM_OP, (Item) displayIndexMap.get(clickIndex));
                    }
                    break;
            }
        }

        void displayStoreName(String storeName) {
            storeNameTextView.setText(storeName);
            storeNameTextView.setVisibility(View.VISIBLE);
            itemEntryContainer.setVisibility(View.GONE);
        }

        void displayItem(Item item) {
            itemNameCheckbox.setText(item.toString());
            if (item.getItemMarkedPurchased() == 1) {
                itemNameCheckbox.setPaintFlags(itemNameCheckbox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                itemNameCheckbox.setChecked(true);
            }
            storeNameTextView.setVisibility(View.GONE);
            itemEntryContainer.setVisibility(View.VISIBLE);
        }
    }
}
