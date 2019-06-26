package com.example.supjain.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.supjain.shoppinglist.ui.CreateListFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_NAME_KEY;
import static com.example.supjain.shoppinglist.util.Constants.SHOPPING_LIST_TYPE_KEY;

public class CreateListActivity extends AppCompatActivity implements CreateListFragment.CreateListReqHandler {

    private static final String CREATE_LIST_FRAGMENT_TAG = "CreateListFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);
        setTitle(R.string.create_list_activity_title);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(CREATE_LIST_FRAGMENT_TAG) == null) {
            CreateListFragment createListFragment = new CreateListFragment();
            fragmentManager.beginTransaction().add(R.id.create_list_fragment_container,
                    createListFragment, CREATE_LIST_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onCreateListBtnClick(String listName, String listType) {
        Intent intent = new Intent();
        intent.putExtra(SHOPPING_LIST_NAME_KEY, listName);
        intent.putExtra(SHOPPING_LIST_TYPE_KEY, listType);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CREATE_LIST_FRAGMENT_TAG);
        if (fragment != null && fragment instanceof CreateListFragment
                && ((CreateListFragment) fragment).isDataChanged())
            showDiscardChangesAlertDialog();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(CREATE_LIST_FRAGMENT_TAG);
            if (fragment != null && fragment instanceof CreateListFragment
                    && ((CreateListFragment) fragment).isDataChanged()) {
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
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.alert_dialog_cancel_text), null);
        alertDialogBuilder.show();
    }
}
