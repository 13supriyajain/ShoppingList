package com.example.supjain.shoppinglist;

import android.os.Bundle;

import com.example.supjain.shoppinglist.ui.CreateListFragment;

import androidx.appcompat.app.AppCompatActivity;

public class CreateListActivity extends AppCompatActivity {

    private static final String CREATE_LIST_FRAGMENT_TAG = "CreateListFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);
        setTitle(R.string.create_list_activity_title);

        CreateListFragment createListFragment = new CreateListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.create_list_fragment_container, createListFragment, CREATE_LIST_FRAGMENT_TAG)
                .commit();
    }
}
