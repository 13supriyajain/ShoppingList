package com.example.supjain.shoppinglist;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supjain.shoppinglist.adapters.ShoppingListsAdapter;
import com.example.supjain.shoppinglist.data.ShoppingList;
import com.example.supjain.shoppinglist.data.Store;
import com.example.supjain.shoppinglist.util.ShoppingListUtil;
import com.example.supjain.shoppinglist.viewmodel.ShoppingListsViewModel;
import com.example.supjain.shoppinglist.widget.ShoppingListWidgetProvider;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.supjain.shoppinglist.util.Constants.RC_CREATE_LIST;
import static com.example.supjain.shoppinglist.util.Constants.RC_SIGN_IN;
import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_TYPE_KEY;
import static com.example.supjain.shoppinglist.util.Constants.STORE_LIST_OBJ_KEY;

public class MainActivity extends AppCompatActivity implements
        ShoppingListsAdapter.ShoppingListsAdapterOnClickHandler {

    @BindView(R.id.network_err_msg)
    TextView errMsgTextView;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.shoppinglists_recyclerview)
    RecyclerView recyclerView;

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseFirestore.getInstance();
        checkIfUserIsSignedIn();

        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        shoppingListsViewModel = ViewModelProviders.of(this).get(ShoppingListsViewModel.class);
        shoppingListsViewModel.getList().observe(this, list -> {
            if (list != null && !list.isEmpty() && shoppingListsAdapter != null) {
                shoppingListsAdapter.setShoppingLists(list);
                shoppingListsViewModel.setErrorMsg(null);
            } else
                shoppingListsViewModel.setErrorMsg(getResources().getString(R.string.no_shopping_list_err_msg));
        });
        shoppingListsViewModel.getErrorMsg().observe(this, errMsg -> {
            if (TextUtils.isEmpty(errMsg)) {
                errMsgTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            } else {
                errMsgTextView.setText(errMsg);
                errMsgTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                shoppingListsAdapter.setShoppingLists(null);
            }
        });
        binding.setVariable(BR.viewModel, shoppingListsViewModel);
        binding.executePendingBindings();
        ButterKnife.bind(this);

        shoppingListsAdapter = new ShoppingListsAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                ShoppingListUtil.calculateNoOfColumns(this));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(shoppingListsAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void fetchAndSetShoppingLists() {
        if (!ShoppingListUtil.hasNetworkConnection(this))
            shoppingListsViewModel.setErrorMsg(getResources().getString(R.string.no_connection_err_msg));
        else {
            final List<ShoppingList> lists = new ArrayList<>();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                firebaseDb.collection(userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    ShoppingList shoppingList = document.toObject(ShoppingList.class);
                                    lists.add(shoppingList);
                                }
                                shoppingListsViewModel.setList(lists);
                            } else
                                shoppingListsViewModel.setErrorMsg(getResources().getString(R.string.failure_err_msg));
                        })
                        .addOnFailureListener(e -> shoppingListsViewModel.setErrorMsg(getResources()
                                .getString(R.string.failure_err_msg)));
            }
        }
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
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                currentUser = firebaseAuth.getCurrentUser();
                Toast.makeText(getApplicationContext(), R.string.signin_success_text_msg, Toast.LENGTH_SHORT).show();
                fetchAndSetShoppingLists();
            } else {
                finish();
            }
        } else if (requestCode == RC_CREATE_LIST && resultCode == RESULT_OK && data != null) {
            String listName = data.getStringExtra(SHOPPING_LIST_NAME_KEY);
            String listType = data.getStringExtra(SHOPPING_LIST_TYPE_KEY);

            ShoppingList newShoppingList = new ShoppingList(listName,
                    listType, ShoppingListUtil.getShoppingListTypeIcon(listType), null);
            validateAndSaveShoppingList(newShoppingList);
        }
    }

    private void validateAndSaveShoppingList(ShoppingList newShoppingList) {
        if (!ShoppingListUtil.hasNetworkConnection(this)) {
            showErrorAlertDialog(R.string.failure_err_title, R.string.no_connection_err_msg);
        } else if (currentUser != null) {
            String userId = currentUser.getUid();
            checkIfListAlreadyExists(userId, newShoppingList);
        }
    }

    private void checkIfListAlreadyExists(final String userId, final ShoppingList newShoppingList) {
        firebaseDb.collection(userId)
                .document(newShoppingList.getShoppingListName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document == null || !document.exists()) {
                            storeShoppingList(userId, newShoppingList);
                        } else {
                            showErrorAlertDialog(R.string.list_name_exists_err_title,
                                    R.string.list_name_exists_err_msg);
                        }
                    }
                })
                .addOnFailureListener(e -> showErrorAlertDialog(R.string.failure_err_title,
                        R.string.failure_err_msg));
    }

    private void storeShoppingList(String userId, ShoppingList newShoppingList) {
        firebaseDb.collection(userId)
                .document(newShoppingList.getShoppingListName())
                .set(newShoppingList)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), R.string.list_store_success_msg,
                            Toast.LENGTH_SHORT).show();
                    fetchAndSetShoppingLists();
                })
                .addOnFailureListener(e -> showErrorAlertDialog(R.string.failure_err_title,
                        R.string.failure_err_msg));
    }

    private void showErrorAlertDialog(int errTitleId, int errMsgId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(errTitleId);
        alertDialog.setMessage(errMsgId);
        alertDialog.setNegativeButton(R.string.alert_dialog_ok_text, null);
        alertDialog.show();
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Toast.makeText(getApplicationContext(), R.string.signout_success_text_msg,
                            Toast.LENGTH_SHORT).show();
                    signIn();
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.create_list_fab)
    public void onFabClick() {
        Intent intent = new Intent(this, CreateListActivity.class);
        startActivityForResult(intent, RC_CREATE_LIST);
    }

    @Override
    public void mRecyclerViewClick(ShoppingList list) {
        // Update all widgets
        updateWidgets(list);

        if (widgetConfigCall) {
            // Finish configuring widget
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        } else {
            Intent intent = new Intent(this, DisplayShoppingListActivity.class);
            intent.putParcelableArrayListExtra(STORE_LIST_OBJ_KEY, (ArrayList<Store>) list.getStores());
            intent.putExtra(SHOPPING_LIST_NAME_KEY, list.getShoppingListName());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndSetShoppingLists();
    }

    private void updateWidgets(ShoppingList list) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ShoppingListWidgetProvider.class));
        ShoppingListWidgetProvider.updateAppWidgets(this, appWidgetManager, list, appWidgetIds);
    }
}