package com.example.supjain.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.supjain.shoppinglist.adapters.ItemListAdapter;
import com.example.supjain.shoppinglist.data.Item;
import com.example.supjain.shoppinglist.data.Store;
import com.example.supjain.shoppinglist.viewmodel.ShoppingListsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_LIST_OBJ_KEY;

public class DisplayShoppingListActivity extends AppCompatActivity implements View.OnClickListener,
        ItemListAdapter.ItemListAdapterOnClickHandler {

    private ShoppingListsViewModel shoppingListsViewModel;
    private List<Store> storeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_display_shopping_list);
        binding.setLifecycleOwner(this);

        Intent intent = getIntent();
        String listName = intent.getStringExtra(SHOPPING_LIST_NAME_KEY);
        setTitle(listName);

        storeList = intent.getParcelableArrayListExtra(STORE_LIST_OBJ_KEY);
        shoppingListsViewModel = ViewModelProviders.of(this).get(ShoppingListsViewModel.class);
        shoppingListsViewModel.setList(storeList);
        binding.setVariable(BR.viewModel, shoppingListsViewModel);

        FloatingActionButton addItemFab = findViewById(R.id.add_item_fab);
        addItemFab.setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.item_list_recyclerview);
        ItemListAdapter itemListAdapter = new ItemListAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemListAdapter);
        recyclerView.setHasFixedSize(true);

        binding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add_item_fab:
                Toast.makeText(this, "FAB clicked", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, CreateListActivity.class);
//                startActivity(intent);
        }
    }

    @Override
    public void mClick(String operation, Item item) {
        Toast.makeText(this, operation + " on " + item.getItemName() + " clicked",
                Toast.LENGTH_SHORT).show();
    }
}
