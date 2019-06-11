package com.example.supjain.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import com.example.supjain.shoppinglist.ui.CreateListFragment;

import androidx.appcompat.app.AppCompatActivity;
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
}
