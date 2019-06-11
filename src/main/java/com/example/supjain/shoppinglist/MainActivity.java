package com.example.supjain.shoppinglist;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.supjain.shoppinglist.adapters.ShoppingListsAdapter;
import com.example.supjain.shoppinglist.data.ShoppingList;
import com.example.supjain.shoppinglist.data.Store;
import com.example.supjain.shoppinglist.util.ShoppingListUtil;
import com.example.supjain.shoppinglist.viewmodel.ShoppingListsViewModel;
import com.example.supjain.shoppinglist.widget.ShoppingListWidgetProvider;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.supjain.shoppinglist.util.Constants.RC_CREATE_LIST;
import static com.example.supjain.shoppinglist.util.Constants.RC_SIGN_IN;
import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_TYPE_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_LIST_OBJ_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ShoppingListsAdapter.ShoppingListsAdapterOnClickHandler {

    private static FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseDb;
    private FirebaseUser currentUser;
    private ShoppingListsViewModel shoppingListsViewModel;
    private ShoppingListsAdapter shoppingListsAdapter;
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private boolean widgetConfigCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.main_activity_title);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            setResult(RESULT_CANCELED);
            widgetConfigCall = appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID;
        }

        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseFirestore.getInstance();
        checkIfUserIsSignedIn();

        FloatingActionButton createListFab = findViewById(R.id.create_list_fab);
        createListFab.setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.shoppinglists_recyclerview);
        shoppingListsAdapter = new ShoppingListsAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                ShoppingListUtil.calculateNoOfColumns(this));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(shoppingListsAdapter);
        recyclerView.setHasFixedSize(true);

        shoppingListsViewModel = ViewModelProviders.of(this).get(ShoppingListsViewModel.class);
        shoppingListsViewModel.getList().observe(this, new Observer<List<?>>() {
            @Override
            public void onChanged(List<?> list) {
                if (list != null && !list.isEmpty() && list instanceof ShoppingList &&
                        shoppingListsAdapter != null) {
                    shoppingListsAdapter.setShoppingLists((List<ShoppingList>) list);
                } else
                    shoppingListsViewModel.setErrorMsg(getResources().getString(R.string.no_shopping_list_err));
            }
        });
        binding.setVariable(BR.viewModel, shoppingListsViewModel);

        fetchAndSetShoppingLists();
        binding.executePendingBindings();
    }

    private void fetchAndSetShoppingLists() {

        // TODO: Add network connection check and cases to display error msg
        final List<ShoppingList> lists = new ArrayList<>();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            firebaseDb.collection(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ShoppingList shoppingList = document.toObject(ShoppingList.class);
                                    lists.add(shoppingList);
                                }
                                shoppingListsViewModel.setList(lists);
                            } else {
                                Toast.makeText(getApplicationContext(), "Data fetch failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        shoppingListsViewModel.setList(lists);
    }

    private void checkIfUserIsSignedIn() {
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            signIn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                currentUser = firebaseAuth.getCurrentUser();
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
        } else if (requestCode == RC_CREATE_LIST && resultCode == RESULT_OK && data != null) {
            String listName = data.getStringExtra(SHOPPING_LIST_NAME_KEY);
            String listType = data.getStringExtra(SHOPPING_LIST_TYPE_KEY);

            ShoppingList newShoppingList = new ShoppingList(listName,
                    listType, ShoppingListUtil.getShoppingListTypeIcon(listType), null);
            saveShoppingList(newShoppingList);
        }
    }

    private void saveShoppingList(ShoppingList newShoppingList) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            firebaseDb.collection(userId)
                    .add(newShoppingList)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "List has been stored successfully",
                                    Toast.LENGTH_SHORT).show();
                            fetchAndSetShoppingLists();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "List couldn't be stored !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Signed out",
                                Toast.LENGTH_SHORT).show();
                        signIn();
                    }
                });
    }

    private void signIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
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
                startActivityForResult(intent, RC_CREATE_LIST);
        }
    }

    @Override
    public void mRecyclerViewClick(ShoppingList list) {

        // Update all widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ShoppingListWidgetProvider.class));
        ShoppingListWidgetProvider.updateAppWidgets(this, appWidgetManager, list, appWidgetIds);

        if (widgetConfigCall) {
            // Finish configuring widget
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        } else {
            Toast.makeText(this, "List selected: " + list.getShoppingListName(),
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DisplayShoppingListActivity.class);
            intent.putParcelableArrayListExtra(STORE_LIST_OBJ_KEY, (ArrayList<Store>) list.getStores());
            intent.putExtra(SHOPPING_LIST_NAME_KEY, list.getShoppingListName());
            startActivity(intent);
        }
    }
}