package com.example.supjain.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.supjain.shoppinglist.adapters.ShoppingListsAdapter;
import com.example.supjain.shoppinglist.data.Item;
import com.example.supjain.shoppinglist.data.ShoppingList;
import com.example.supjain.shoppinglist.data.ShoppingListType;
import com.example.supjain.shoppinglist.data.Store;
import com.example.supjain.shoppinglist.util.ShoppingListUtil;
import com.example.supjain.shoppinglist.viewmodel.ShoppingListsViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.supjain.shoppinglist.util.Constants.RC_SIGN_IN;
import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_LIST_OBJ_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ShoppingListsAdapter.ShoppingListsAdapterOnClickHandler {

    private static FirebaseAuth firebaseAuth;
    private ShoppingListsViewModel shoppingListsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.main_activity_title);

        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkIfUserIsSignedIn();

        shoppingListsViewModel = ViewModelProviders.of(this).get(ShoppingListsViewModel.class);
        binding.setVariable(BR.viewModel, shoppingListsViewModel);

        FloatingActionButton createListFab = findViewById(R.id.create_list_fab);
        createListFab.setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.shoppinglists_recyclerview);
        ShoppingListsAdapter shoppingListsAdapter = new ShoppingListsAdapter(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                ShoppingListUtil.calculateNoOfColumns(this));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(shoppingListsAdapter);
        recyclerView.setHasFixedSize(true);

        fetchAndSetShoppingLists();
        binding.executePendingBindings();

        // TODO: Put signin activity code in separate file/activity
    }

    private void fetchAndSetShoppingLists() {

        List<ShoppingList> lists = new ArrayList<>();
        List<Item> itemList = new ArrayList<>();
        List<Store> storeList = new ArrayList<>();

        Item item1 = new Item(123L, "Milk", 1, "gallon");
        itemList.add(item1);

        Item item2 = new Item(456L, "Apples", 5, "kgs");
        itemList.add(item2);

        Item item3 = new Item(789L, "Oranges", 3, "kgs");
        itemList.add(item3);

        Store store1 = new Store(987L, "Safeway", "", itemList);
        storeList.add(store1);

        Store store2 = new Store(654L, "Sprouts", "", itemList);
        storeList.add(store2);

        Store store3 = new Store(321L, "Walgreens", "", itemList);
        storeList.add(store3);

        ShoppingList shoppingList1 = new ShoppingList(111L, "Grocery",
                ShoppingListType.GROCERY.toString(), R.drawable.default_list_img, storeList);
        lists.add(shoppingList1);

        ShoppingList shoppingList2 = new ShoppingList(222L, "Household",
                ShoppingListType.HOUSEHOLD.toString(), R.drawable.default_list_img, storeList);
        lists.add(shoppingList2);

        ShoppingList shoppingList3 = new ShoppingList(333L, "Wedding",
                ShoppingListType.WEDDING.toString(), R.drawable.default_list_img, storeList);
        lists.add(shoppingList3);

        ShoppingList shoppingList4 = new ShoppingList(444L, "Trip",
                ShoppingListType.TRIP.toString(), R.drawable.default_list_img, storeList);
        lists.add(shoppingList4);

        shoppingListsViewModel.setList(lists);
    }

    private void checkIfUserIsSignedIn() {
        if (firebaseAuth.getCurrentUser() != null) {
            // already signed in
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Toast.makeText(getApplicationContext(), "Signin successful", Toast.LENGTH_SHORT).show();
                // ...
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(getApplicationContext(), "Sign-in cancelled", Toast.LENGTH_SHORT)
                            .show();
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_SHORT)
                            .show();
                }

                // Other error
                Toast.makeText(getApplicationContext(), "Sign-in error", Toast.LENGTH_SHORT)
                        .show();

                finish();
            }
        }
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Signed out",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.signout_menu_option:
                signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.create_list_fab:
                Intent intent = new Intent(this, CreateListActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public void mRecyclerViewClick(ShoppingList list) {
        Toast.makeText(this, "List selected: " + list.getShoppingListName(),
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DisplayShoppingListActivity.class);
        intent.putParcelableArrayListExtra(STORE_LIST_OBJ_KEY, (ArrayList<Store>) list.getStores());
        intent.putExtra(SHOPPING_LIST_NAME_KEY, list.getShoppingListName());
        startActivity(intent);
    }
}