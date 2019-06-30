package com.example.supjain.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.supjain.shoppinglist.data.Item;
import com.example.supjain.shoppinglist.ui.CreateItemFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.example.supjain.shoppinglist.util.Constants.ITEM_OBJ_KEY;
import static com.example.supjain.shoppinglist.util.Constants.ITEM_TO_EDIT;
import static com.example.supjain.shoppinglist.util.Constants.STORE_NAME_KEY;

public class CreateItemActivity extends AppCompatActivity implements CreateItemFragment.SaveItemReqHandler {

    private static final String CREATE_ITEM_FRAGMENT_TAG = "CreateItemFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(CREATE_ITEM_FRAGMENT_TAG) == null) {
            CreateItemFragment createItemFragment = new CreateItemFragment();
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(ITEM_TO_EDIT)) {
                setTitle(R.string.edit_item_activity_title);
                createItemFragment.setArguments(intent.getExtras());
            } else
                setTitle(R.string.create_item_activity_title);

            fragmentManager.beginTransaction().add(R.id.create_item_fragment_container,
                    createItemFragment, CREATE_ITEM_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CREATE_ITEM_FRAGMENT_TAG);
        if (fragment instanceof CreateItemFragment && ((CreateItemFragment) fragment).isDataChanged())
            showDiscardChangesAlertDialog();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(CREATE_ITEM_FRAGMENT_TAG);
            if (fragment instanceof CreateItemFragment && ((CreateItemFragment) fragment).isDataChanged()) {
                showDiscardChangesAlertDialog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDiscardChangesAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.discard_changes_alert_dialog_title));
        alertDialogBuilder.setMessage(R.string.discard_changes_alert_dialog_msg);
        alertDialogBuilder.setPositiveButton(getString(R.string.discard_changes_alert_dialog_confirm_text),
                (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.alert_dialog_cancel_text), null);
        alertDialogBuilder.show();
    }

    @Override
    public void onSaveItemBtnClick(Item item, String storeName) {
        Intent intent = new Intent();
        intent.putExtra(ITEM_OBJ_KEY, item);
        intent.putExtra(STORE_NAME_KEY, storeName);
        setResult(RESULT_OK, intent);
        finish();
    }
}