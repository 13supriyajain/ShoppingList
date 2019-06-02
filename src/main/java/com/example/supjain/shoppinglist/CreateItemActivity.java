package com.example.supjain.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.supjain.shoppinglist.ui.CreateItemFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.example.supjain.shoppinglist.util.Constants.ITEM_TO_EDIT;

public class CreateItemActivity extends AppCompatActivity {

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
        if ((fragment instanceof IOnBackPressed) && ((IOnBackPressed) fragment).onBackPressed()) {
            showDiscardChangesAlertDialog();
        } else
            super.onBackPressed();
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

    public interface IOnBackPressed {
        /**
         * @return true if fragment's implementation of onBackPressed() should be given priority,
         * if not then return false and activity's onBackPressed should be given preference
         */
        boolean onBackPressed();
    }
}