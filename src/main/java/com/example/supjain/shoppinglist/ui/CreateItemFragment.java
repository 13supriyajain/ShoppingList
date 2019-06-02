package com.example.supjain.shoppinglist.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.supjain.shoppinglist.CreateItemActivity;
import com.example.supjain.shoppinglist.R;
import com.example.supjain.shoppinglist.data.Item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.supjain.shoppinglist.util.Constants.ITEM_TO_EDIT;

public class CreateItemFragment extends Fragment implements View.OnClickListener, TextWatcher, CreateItemActivity.IOnBackPressed {

    private static final String DATA_CHANGED_FLAG = "DataChangedFlag";
    private boolean dataChanged;
    private boolean editItemMode;
    private Item item;

    private EditText itemNameEditText;
    private EditText itemQuantityEditText;
    private EditText itemMeasureEditText;
    private EditText storeNameEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null)
            item = args.getParcelable(ITEM_TO_EDIT);

        if (savedInstanceState != null)
            dataChanged = savedInstanceState.getBoolean(DATA_CHANGED_FLAG);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_item_fragment, container, false);

        itemNameEditText = rootView.findViewById(R.id.item_name_edittext);
        itemQuantityEditText = rootView.findViewById(R.id.item_quantity_edittext);
        itemMeasureEditText = rootView.findViewById(R.id.item_measure_edittext);
        storeNameEditText = rootView.findViewById(R.id.store_name_edittext);
        itemNameEditText.addTextChangedListener(this);
        itemQuantityEditText.addTextChangedListener(this);
        itemMeasureEditText.addTextChangedListener(this);
        storeNameEditText.addTextChangedListener(this);

        if (item != null) {
            editItemMode = true;
            itemNameEditText.setText(item.getItemName());
            itemQuantityEditText.setText("" + item.getItemQuantity());
            itemMeasureEditText.setText(item.getItemMeasurement());
            rootView.findViewById(R.id.store_name_container).setVisibility(View.GONE);
        } else
            editItemMode = false;

        Button saveItemBtn = rootView.findViewById(R.id.save_item_btn);
        saveItemBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.save_item_btn:
                if (dataChanged) {
                    if (editItemMode && item != null)
                        Toast.makeText(getContext(), "Item edited: " + item.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), "New item created", Toast.LENGTH_SHORT).show();
                }
                getActivity().finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        dataChanged = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(DATA_CHANGED_FLAG, dataChanged);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onBackPressed() {
        return dataChanged;
    }
}