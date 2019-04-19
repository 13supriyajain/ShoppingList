package com.example.supjain.shoppinglist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.supjain.shoppinglist.BR;
import com.example.supjain.shoppinglist.data.ShoppingList;
import com.example.supjain.shoppinglist.databinding.ShoppinglistsListItemBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ShoppingListsAdapterViewHolder> {

    private List<ShoppingList> shoppingLists;
    private ShoppingListsAdapterOnClickHandler clickHandler;

    public ShoppingListsAdapter(ShoppingListsAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void setShoppingLists(List<ShoppingList> list) {
        this.shoppingLists = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShoppingListsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        ViewDataBinding binding =
                ShoppinglistsListItemBinding.inflate(layoutInflater, viewGroup, false);
        return new ShoppingListsAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListsAdapterViewHolder holder, int i) {
        int viewPosition = holder.getAdapterPosition();
        ShoppingList shoppingList = shoppingLists.get(viewPosition);
        holder.bind(shoppingList);
    }

    @Override
    public int getItemCount() {
        if (shoppingLists == null || shoppingLists.isEmpty())
            return 0;
        return shoppingLists.size();
    }

    public interface ShoppingListsAdapterOnClickHandler {
        void mRecyclerViewClick(ShoppingList list);
    }

    public class ShoppingListsAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ViewDataBinding binding;

        ShoppingListsAdapterViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        void bind(ShoppingList listData) {
            binding.setVariable(BR.list, listData);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            clickHandler.mRecyclerViewClick(shoppingLists.get(clickedPosition));
        }
    }
}
