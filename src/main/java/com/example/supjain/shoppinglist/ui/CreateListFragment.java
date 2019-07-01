package com.example.supjain.shoppinglist.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.supjain.shoppinglist.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

import static com.example.supjain.shoppinglist.util.Constants.LIST_NAME_MAX_LENGTH;

public class CreateListFragment extends Fragment {

    @BindView(R.id.list_name_edittext)
    EditText listnameEditText;
    @BindView(R.id.list_type_spinner)
    Spinner listTypesSpinner;

    private static final String DATA_CHANGED_FLAG = "DataChangedFlag";
    private boolean dataChanged;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private String listTypeSelected;
    private CreateListReqHandler createListReqHandler;

    public CreateListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_list_fragment, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null)
            dataChanged = savedInstanceState.getBoolean(DATA_CHANGED_FLAG);

        spinnerAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.list_types_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listTypesSpinner.setAdapter(spinnerAdapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createListReqHandler = (CreateListReqHandler) getActivity();
    }

    @OnItemSelected(R.id.list_type_spinner)
    void onItemSelected(int position) {
        listTypeSelected = Objects.requireNonNull(spinnerAdapter.getItem(position)).toString();
    }

    @OnClick(R.id.create_list_btn)
    void onCreateListBtnClick() {
        String listName = null;
        if (listnameEditText != null)
            listName = listnameEditText.getText().toString();
        if (!isInvalidListName(listName))
            createListReqHandler.onCreateListBtnClick(listName, listTypeSelected);
        else
            showErrorAlertDialog();
    }

    // Checks if Shopping list name is invalid
    private boolean isInvalidListName(String listName) {
        return TextUtils.isEmpty(listName) || listName.length() > LIST_NAME_MAX_LENGTH;
    }

    // Show error/alert dialog if shopping list name is invalid
    private void showErrorAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(R.string.failure_err_title);
        alertDialog.setMessage(R.string.create_list_err_invalid_name);
        alertDialog.setNegativeButton(R.string.alert_dialog_ok_text, null);
        alertDialog.show();
    }

    @OnTextChanged(R.id.list_name_edittext)
    void onEditTextChanged() {
        dataChanged = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(DATA_CHANGED_FLAG, dataChanged);
        super.onSaveInstanceState(outState);
    }

    // Any class which wants to handle create list button click, should implement this interface.
    public interface CreateListReqHandler {
        void onCreateListBtnClick(String listName, String listType);
    }

    public boolean isDataChanged() {
        return this.dataChanged;
    }
}
