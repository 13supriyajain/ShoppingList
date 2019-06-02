package com.example.supjain.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.supjain.shoppinglist.adapters.ItemListAdapter;
import com.example.supjain.shoppinglist.data.Item;
import com.example.supjain.shoppinglist.data.Store;
import com.example.supjain.shoppinglist.viewmodel.ShoppingListsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.supjain.shoppinglist.util.Constants.CHECK_ITEM_OP;
import static com.example.supjain.shoppinglist.util.Constants.DELETE_ITEM_OP;
import static com.example.supjain.shoppinglist.util.Constants.EDIT_ITEM_OP;
import static com.example.supjain.shoppinglist.util.Constants.ITEM_TO_EDIT;
import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_LIST_OBJ_KEY;
import static com.example.supjain.shoppinglist.util.Constants.UNCHECK_ITEM_OP;

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
                Intent intent = new Intent(this, CreateItemActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void mClick(String operation, Item item) {
        Toast.makeText(this, operation + " on " + item.getItemName() + " clicked",
                Toast.LENGTH_SHORT).show();
        CheckBox checkBox;
        switch (operation) {
            case EDIT_ITEM_OP:
                Intent intent = new Intent(this, CreateItemActivity.class);
                intent.putExtra(ITEM_TO_EDIT, item);
                startActivity(intent);
                break;
            case DELETE_ITEM_OP:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getString(R.string.delete_item_alert_msg));
                dialog.setPositiveButton(getString(R.string.delete_item_alert_confirm_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                dialog.setNegativeButton(getString(R.string.alert_dialog_cancel_text), null);
                dialog.show();
                break;
            case CHECK_ITEM_OP:
                checkBox = findViewById(R.id.item_name_checkbox);
                checkBox.setPaintFlags(checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                break;
            case UNCHECK_ITEM_OP:
                checkBox = findViewById(R.id.item_name_checkbox);
                checkBox.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                break;
        }
    }
}
