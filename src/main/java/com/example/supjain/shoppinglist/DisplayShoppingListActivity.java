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
import com.example.supjain.shoppinglist.viewmodel.ItemListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
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

    private ItemListViewModel itemListViewModel;
    private List<Store> storeList;
    private static FirebaseAuth firebaseAuth;
    private ItemListAdapter itemListAdapter;
    private FirebaseFirestore firebaseDb;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseFirestore.getInstance();

        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_display_shopping_list);
        binding.setLifecycleOwner(this);

        Intent intent = getIntent();
        String listName = intent.getStringExtra(SHOPPING_LIST_NAME_KEY);
        setTitle(listName);

        FloatingActionButton addItemFab = findViewById(R.id.add_item_fab);
        addItemFab.setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.item_list_recyclerview);
        itemListAdapter = new ItemListAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemListAdapter);
        recyclerView.setHasFixedSize(true);

        itemListViewModel = ViewModelProviders.of(this).get(ItemListViewModel.class);
        itemListViewModel.getList().observe(this, new Observer<List<Store>>() {
            @Override
            public void onChanged(List<Store> list) {
                if (list != null && !list.isEmpty() && itemListAdapter != null) {
                    itemListAdapter.setStoresList(list);
                } else {
                    itemListViewModel.setErrorMsg(getResources().getString(R.string.no_item_err_msg));
                    itemListViewModel.setList(null);
                }
            }
        });
        storeList = intent.getParcelableArrayListExtra(STORE_LIST_OBJ_KEY);
        if (storeList == null || storeList.isEmpty()) {
            itemListViewModel.setErrorMsg(getResources().getString(R.string.no_item_err_msg));
            itemListViewModel.setList(null);
        } else
            itemListViewModel.setList(storeList);
        binding.setVariable(BR.viewModel, itemListViewModel);
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