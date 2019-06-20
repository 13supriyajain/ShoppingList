package com.example.supjain.shoppinglist.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.supjain.shoppinglist.R;
import com.example.supjain.shoppinglist.data.Item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.supjain.shoppinglist.util.Constants.ITEM_NAME_MAX_LENGTH;
import static com.example.supjain.shoppinglist.util.Constants.ITEM_TO_EDIT;
import static com.example.supjain.shoppinglist.util.Constants.MEASUREMENT_VALUE_MAX_LENGTH;
import static com.example.supjain.shoppinglist.util.Constants.STORE_NAME_MAX_LENGTH;

public class CreateItemFragment extends Fragment implements View.OnClickListener, TextWatcher {
    //CreateItemActivity.IOnBackPressed {

    private static final String DATA_CHANGED_FLAG = "DataChangedFlag";
    private boolean dataChanged;
    private boolean editItemMode;
    private Item item;

    private EditText itemNameEditText;
    private EditText itemQuantityEditText;
    private EditText itemMeasureEditText;
    private EditText storeNameEditText;

    private SaveItemReqHandler saveItemReqHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        saveItemReqHandler = (SaveItemReqHandler) getActivity();
    }

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
                    retrieveAndSaveValues();
                } else
                    getActivity().finish();
                break;
        }
    }

    private void retrieveAndSaveValues() {

        String itemName = "";
        if (itemNameEditText != null) {
            itemName = itemNameEditText.getText().toString();
            if (isInvalidItemNameValue(itemName)) {
                showErrorAlertDialog(R.string.invalid_item_name_err_msg);
                return;
            }
        }

        float quantityValue = 0;
        if (itemQuantityEditText != null) {
            String quantityValueText = itemQuantityEditText.getText().toString();
            quantityValue = Float.valueOf(quantityValueText);
            if (quantityValue <= 0) {
                showErrorAlertDialog(R.string.invalid_item_qty_err_msg);
                return;
            }
        }

        String measurementValue = "";
        if (itemMeasureEditText != null) {
            measurementValue = itemMeasureEditText.getText().toString();
            if (isInvalidMeasurementValue(measurementValue)) {
                showErrorAlertDialog(R.string.invalid_item_msrmnt_err_msg);
                return;
            }
        }

        if (editItemMode && item != null) {
            item.setItemName(itemName);
            item.setItemQuantity(quantityValue);
            item.setItemMeasurement(measurementValue);
            saveItemReqHandler.onSaveItemBtnClick(item, null);
        } else if (!editItemMode) {
            String storeName = "";
            if (storeNameEditText != null) {
                storeName = storeNameEditText.getText().toString();
                if (isInvalidStoreNameValue(storeName)) {
                    showErrorAlertDialog(R.string.invalid_item_msrmnt_err_msg);
                    return;
                }
            }
            Item newItem = new Item(itemName, quantityValue, measurementValue);
            saveItemReqHandler.onSaveItemBtnClick(newItem, storeName);
        }
    }

    private boolean isInvalidItemNameValue(String itemName) {
        return TextUtils.isEmpty(itemName) || itemName.length() > ITEM_NAME_MAX_LENGTH;
    }

    private boolean isInvalidStoreNameValue(String storeName) {
        return TextUtils.isEmpty(storeName) || storeName.length() > STORE_NAME_MAX_LENGTH;
    }

    private boolean isInvalidMeasurementValue(String measurementValue) {
        return TextUtils.isEmpty(measurementValue) || measurementValue.length() > MEASUREMENT_VALUE_MAX_LENGTH;
    }

    private void showErrorAlertDialog(int errMsgId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(R.string.failure_err_title);
        alertDialog.setMessage(errMsgId);
        alertDialog.setNegativeButton(R.string.alert_dialog_ok_text, null);
        alertDialog.show();
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

//    @Override
//    public boolean onBackPressed() {
//        return dataChanged;
//    }

    public interface SaveItemReqHandler {
        void onSaveItemBtnClick(Item item, String storeName);
    }
}