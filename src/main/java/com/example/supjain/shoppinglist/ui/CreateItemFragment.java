package com.example.supjain.shoppinglist.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.supjain.shoppinglist.R;
import com.example.supjain.shoppinglist.data.Item;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.example.supjain.shoppinglist.util.Constants.ITEM_NAME_MAX_LENGTH;
import static com.example.supjain.shoppinglist.util.Constants.ITEM_TO_EDIT;
import static com.example.supjain.shoppinglist.util.Constants.MEASUREMENT_VALUE_MAX_LENGTH;
import static com.example.supjain.shoppinglist.util.Constants.STORE_NAME_MAX_LENGTH;

public class CreateItemFragment extends Fragment {

    @BindView(R.id.item_name_edittext)
    EditText itemNameEditText;
    @BindView(R.id.item_quantity_edittext)
    EditText itemQuantityEditText;
    @BindView(R.id.item_measure_edittext)
    EditText itemMeasureEditText;
    @BindView(R.id.store_name_edittext)
    EditText storeNameEditText;

    private static final String DATA_CHANGED_FLAG = "DataChangedFlag";
    private boolean dataChanged;
    private boolean editItemMode;
    private Item item;
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
        ButterKnife.bind(this, rootView);

        if (item != null) {
            editItemMode = true;
            itemNameEditText.setText(item.getItemName());
            itemQuantityEditText.setText(String.valueOf(item.getItemQuantity()));
            itemMeasureEditText.setText(item.getItemMeasurement());
            rootView.findViewById(R.id.store_name_container).setVisibility(View.GONE);
        } else
            editItemMode = false;

        return rootView;
    }

    @OnClick(R.id.save_item_btn)
    void onSaveBtnClick() {
        if (dataChanged) {
            retrieveAndSaveValues();
        } else
            Objects.requireNonNull(getActivity()).finish();
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
            if (TextUtils.isEmpty(quantityValueText)) {
                showErrorAlertDialog(R.string.invalid_item_qty_err_msg);
                return;
            }
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
                    showErrorAlertDialog(R.string.invalid_store_name_err_msg);
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

    @OnTextChanged({R.id.item_name_edittext, R.id.item_quantity_edittext, R.id.item_measure_edittext,
            R.id.store_name_edittext})
    void onEditTextChanged() {
        dataChanged = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(DATA_CHANGED_FLAG, dataChanged);
        super.onSaveInstanceState(outState);
    }

    public interface SaveItemReqHandler {
        void onSaveItemBtnClick(Item item, String storeName);
    }

    public boolean isDataChanged() {
        return this.dataChanged;
    }
}