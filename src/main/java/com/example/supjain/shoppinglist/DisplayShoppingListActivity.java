package com.example.supjain.shoppinglist;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supjain.shoppinglist.adapters.ItemListAdapter;
import com.example.supjain.shoppinglist.data.Item;
import com.example.supjain.shoppinglist.data.ShoppingList;
import com.example.supjain.shoppinglist.data.Store;
import com.example.supjain.shoppinglist.viewmodel.ItemListViewModel;
import com.example.supjain.shoppinglist.widget.ShoppingListWidgetProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.supjain.shoppinglist.util.Constants.CHECK_ITEM_OP;
import static com.example.supjain.shoppinglist.util.Constants.DELETE_ITEM_OP;
import static com.example.supjain.shoppinglist.util.Constants.EDIT_ITEM_OP;
import static com.example.supjain.shoppinglist.util.Constants.ITEM_OBJ_KEY;
import static com.example.supjain.shoppinglist.util.Constants.ITEM_TO_EDIT;
import static com.example.supjain.shoppinglist.util.Constants.RC_CREATE_ITEM;
import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_OBJ_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_LIST_OBJ_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.UNCHECK_ITEM_OP;

public class DisplayShoppingListActivity extends AppCompatActivity implements
        ItemListAdapter.ItemListAdapterOnClickHandler {

    @BindView(R.id.item_list_network_err_msg)
    TextView errMsgTextView;
    @BindView(R.id.item_list_recyclerview)
    RecyclerView recyclerView;

    private ItemListViewModel itemListViewModel;
    private ShoppingList shoppingList;
    private ArrayList<Store> storeList;
    private ItemListAdapter itemListAdapter;
    private FirebaseFirestore firebaseDb;
    private FirebaseUser currentUser;
    private String shoppingListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            storeList = savedInstanceState.getParcelableArrayList(STORE_LIST_OBJ_KEY);
            shoppingList = savedInstanceState.getParcelable(SHOPPING_LIST_OBJ_KEY);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_display_shopping_list);
        binding.setLifecycleOwner(this);

        if (shoppingList == null) {
            Intent intent = getIntent();
            shoppingList = intent.getParcelableExtra(SHOPPING_LIST_OBJ_KEY);
        }

        if (shoppingList != null) {
            shoppingListName = shoppingList.getShoppingListName();
            setTitle(shoppingListName);

            if (storeList == null)
                storeList = shoppingList.getStores();
        }

        itemListViewModel = ViewModelProviders.of(this).get(ItemListViewModel.class);
        binding.setVariable(BR.viewModel, itemListViewModel);
        itemListViewModel.getList().observe(this, list -> {
            if (list != null && !list.isEmpty() && itemListAdapter != null) {
                itemListAdapter.setStoresList(list);
                itemListViewModel.setErrorMsg(null);
            } else
                itemListViewModel.setErrorMsg(getResources().getString(R.string.no_item_err_msg));
        });
        itemListViewModel.getErrorMsg().observe(this, errMsg -> {
            if (TextUtils.isEmpty(errMsg))
                errMsgTextView.setVisibility(View.GONE);
            else {
                errMsgTextView.setText(errMsg);
                errMsgTextView.setVisibility(View.VISIBLE);
                itemListAdapter.setStoresList(null);
            }
        });

        if (storeList == null || storeList.isEmpty())
            itemListViewModel.setErrorMsg(getResources().getString(R.string.no_item_err_msg));
        else
            itemListViewModel.setList(storeList);

        binding.executePendingBindings();
        ButterKnife.bind(this);

        itemListAdapter = new ItemListAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemListAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @OnClick(R.id.add_item_fab)
    public void onFabClick() {
        Intent intent = new Intent(this, CreateItemActivity.class);
        startActivityForResult(intent, RC_CREATE_ITEM);
    }

    @Override
    public void mClick(String operation, final Item item) {
        switch (operation) {
            case EDIT_ITEM_OP:
                Intent intent = new Intent(this, CreateItemActivity.class);
                intent.putExtra(ITEM_TO_EDIT, item);
                startActivityForResult(intent, RC_CREATE_ITEM);
                break;
            case DELETE_ITEM_OP:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getString(R.string.delete_item_alert_msg));
                dialog.setPositiveButton(getString(R.string.delete_item_alert_confirm_text), (dialog1, which) -> {
                    dialog1.dismiss();
                    deleteItem(item);
                        });
                dialog.setNegativeButton(getString(R.string.alert_dialog_cancel_text), null);
                dialog.show();
                break;
            case CHECK_ITEM_OP:
                item.setItemMarkedPurchased(1);
                updateItemInStoreList(item, EDIT_ITEM_OP);
                break;
            case UNCHECK_ITEM_OP:
                item.setItemMarkedPurchased(0);
                updateItemInStoreList(item, EDIT_ITEM_OP);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CREATE_ITEM && resultCode == RESULT_OK && data != null) {
            Item item = data.getParcelableExtra(ITEM_OBJ_KEY);
            String storeName = data.getStringExtra(STORE_NAME_KEY);

            if (storeName == null) {
                updateItemInStoreList(item, EDIT_ITEM_OP);
            } else if (!TextUtils.isEmpty(storeName)) {
                if (storeList != null && !storeList.isEmpty()) {
                    int storeObjIndex = getIndexIfStoreObjWithNameAlreadyExists(storeName, storeList);
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
            itemListViewModel.setList(storeList);
        }
    }

    private void updateItemInStoreList(Item item, String operation) {
        if (item != null) {
            String storeId = item.getItemStoreId();
            if (!TextUtils.isEmpty(storeId)) {
                int storeObjIndex = getIndexIfStoreObjWithIdAlreadyExists(storeId, storeList);
                if (storeObjIndex != -1) {
                    Store storeObj = storeList.get(storeObjIndex);
                    List<Item> itemList = storeObj.getItems();
                    int itemObjIndex = getIndexIfItemObjAlreadyExists(item.getItemId(), itemList);
                    if (itemObjIndex != -1) {
                        if (operation.equals(EDIT_ITEM_OP))
                            itemList.set(itemObjIndex, item);
                        else {
                            // Delete item
                            itemList.remove(itemObjIndex);
                            // If itemList gets empty, remove store from the storelist
                            if (itemList.isEmpty())
                                storeList.remove(storeObjIndex);
                        }
                    }
                }
            }
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

    private int getIndexIfItemObjAlreadyExists(String itemId, List<Item> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            String id = item.getItemId();
            if (!TextUtils.isEmpty(id) && id.equalsIgnoreCase(itemId))
                return i;
        }
        return -1;
    }

    private int getIndexIfStoreObjWithNameAlreadyExists(String storeName, List<Store> storeList) {
        for (int i = 0; i < storeList.size(); i++) {
            Store store = storeList.get(i);
            String name = store.getStoreName();
            if (!TextUtils.isEmpty(name) && name.equalsIgnoreCase(storeName))
                return i;
        }
        return -1;
    }

    private int getIndexIfStoreObjWithIdAlreadyExists(String storeId, List<Store> storeList) {
        for (int i = 0; i < storeList.size(); i++) {
            Store store = storeList.get(i);
            String id = store.getStoreId();
            if (!TextUtils.isEmpty(id) && id.equalsIgnoreCase(storeId))
                return i;
        }
        return -1;
    }

    private void saveStoreListChangesInDb() {
        if (currentUser != null) {
            final String userId = currentUser.getUid();
            firebaseDb.collection(userId)
                    .document(shoppingListName)
                    .update("stores", storeList)
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                            R.string.Update_list_error_msg, Toast.LENGTH_SHORT).show());
        }
    }

    private void deleteItem(Item item) {
        updateItemInStoreList(item, DELETE_ITEM_OP);
        itemListViewModel.setList(storeList);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STORE_LIST_OBJ_KEY, storeList);
        outState.putParcelable(SHOPPING_LIST_OBJ_KEY, shoppingList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Update all the changes in the store list obj in the DB, in shopping list obj and in widget(s)
        saveStoreListChangesInDb();
        if (shoppingList != null) {
            shoppingList.setStores(storeList);
            updateWidgets(shoppingList);
        }
    }

    private void updateWidgets(ShoppingList list) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ShoppingListWidgetProvider.class));
        ShoppingListWidgetProvider.updateAppWidgets(this, appWidgetManager, list, appWidgetIds);
    }
}