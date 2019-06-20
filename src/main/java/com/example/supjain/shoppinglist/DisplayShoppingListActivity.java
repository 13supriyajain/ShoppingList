package com.example.supjain.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supjain.shoppinglist.adapters.ItemListAdapter;
import com.example.supjain.shoppinglist.data.Item;
import com.example.supjain.shoppinglist.data.Store;
import com.example.supjain.shoppinglist.viewmodel.ItemListViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
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
import static com.example.supjain.shoppinglist.util.Constants.ITEM_OBJ_KEY;
import static com.example.supjain.shoppinglist.util.Constants.ITEM_TO_EDIT;
import static com.example.supjain.shoppinglist.util.Constants.RC_CREATE_ITEM;
import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_LIST_OBJ_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.UNCHECK_ITEM_OP;

public class DisplayShoppingListActivity extends AppCompatActivity implements View.OnClickListener,
        ItemListAdapter.ItemListAdapterOnClickHandler {

    private ItemListViewModel itemListViewModel;
    private List<Store> storeList;
    private ItemListAdapter itemListAdapter;
    private FirebaseFirestore firebaseDb;
    private FirebaseUser currentUser;
    private String shoppingListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_display_shopping_list);
        binding.setLifecycleOwner(this);

        Intent intent = getIntent();
        shoppingListName = intent.getStringExtra(SHOPPING_LIST_NAME_KEY);
        setTitle(shoppingListName);

        FloatingActionButton addItemFab = findViewById(R.id.add_item_fab);
        addItemFab.setOnClickListener(this);

        final TextView errMsgTextView = findViewById(R.id.item_list_network_err_msg);
        final ProgressBar progressBar = findViewById(R.id.item_list_progressbar);

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
                    errMsgTextView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                } else
                    itemListViewModel.setErrorMsg(getResources().getString(R.string.no_item_err_msg));
            }
        });
        itemListViewModel.getErrorMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errMsg) {
                errMsgTextView.setText(errMsg);
                errMsgTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                itemListAdapter.setStoresList(null);
            }
        });

        storeList = intent.getParcelableArrayListExtra(STORE_LIST_OBJ_KEY);
        if (storeList == null || storeList.isEmpty())
            itemListViewModel.setErrorMsg(getResources().getString(R.string.no_item_err_msg));
        else
            itemListViewModel.setList(storeList);

        binding.setVariable(BR.viewModel, itemListViewModel);
        binding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add_item_fab:
                Intent intent = new Intent(this, CreateItemActivity.class);
                startActivityForResult(intent, RC_CREATE_ITEM);
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
                startActivityForResult(intent, RC_CREATE_ITEM);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CREATE_ITEM && resultCode == RESULT_OK && data != null) {
            Item item = data.getParcelableExtra(ITEM_OBJ_KEY);
            String storeName = data.getStringExtra(STORE_NAME_KEY);

            if (storeName == null && item != null) {
                int storeId = item.getItemStoreId();
                if (storeId != -1) {
                    int storeObjIndex = getIndexIfStoreObjAlreadyExists(storeId, storeList);
                    if (storeObjIndex != -1) {
                        Store storeObj = storeList.get(storeObjIndex);
                        List<Item> itemList = storeObj.getItems();
                        int itemObjIndex = getIndexIfItemObjAlreadyExists(item.getItemId(), itemList);
                        if (itemObjIndex != -1) {
                            itemList.set(itemObjIndex, item);
                        }
                    }
                }
            } else if (!TextUtils.isEmpty(storeName)) {
                if (storeList != null && !storeList.isEmpty()) {
                    int storeObjIndex = getIndexIfStoreObjAlreadyExists(storeName, storeList);
                    if (storeObjIndex != -1) {
                        Store storeObj = storeList.get(storeObjIndex);
                        item.setItemStoreId(storeObj.getStoreId());
                        storeObj.getItems().add(item);
                    } else
                        createNewStore(storeName, item);
                } else {
                    storeList = new ArrayList<>();
                    createNewStore(storeName, item);
                }
            }
            saveStoreListChanges();
        }
    }

    private void createNewStore(String storeName, Item item) {
        Store storeObj = new Store(storeName, null);
        item.setItemStoreId(storeObj.getStoreId());
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        storeObj.setItems(itemList);
        storeList.add(storeObj);
    }

    private int getIndexIfItemObjAlreadyExists(int itemId, List<Item> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            int id = item.getItemId();
            if (id == itemId)
                return i;
        }
        return -1;
    }

    private int getIndexIfStoreObjAlreadyExists(String storeName, List<Store> storeList) {
        for (int i = 0; i < storeList.size(); i++) {
            Store store = storeList.get(i);
            String name = store.getStoreName();
            if (!TextUtils.isEmpty(name) && name.equalsIgnoreCase(storeName))
                return i;
        }
        return -1;
    }

    private int getIndexIfStoreObjAlreadyExists(int storeId, List<Store> storeList) {
        for (int i = 0; i < storeList.size(); i++) {
            Store store = storeList.get(i);
            int id = store.getStoreId();
            if (id == storeId)
                return i;
        }
        return -1;
    }

    private void saveStoreListChanges() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            firebaseDb.collection(userId).document(shoppingListName)
                    .update("stores", storeList)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "List has been stored successfully",
                                    Toast.LENGTH_SHORT).show();
                            itemListViewModel.setList(storeList);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showErrorAlertDialog(R.string.failure_err_title, R.string.failure_err_msg);
                        }
                    });
        }
    }

    private void showErrorAlertDialog(int errTitleId, int errMsgId) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle(errTitleId);
        alertDialog.setMessage(errMsgId);
        alertDialog.setNegativeButton(R.string.alert_dialog_ok_text, null);
        alertDialog.show();
    }
}