package com.example.supjain.shoppinglist.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.supjain.shoppinglist.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CreateListFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    private ArrayAdapter<CharSequence> spinnerAdapter;

    public CreateListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.create_list_fragment, container, false);

        Spinner listTypesSpinner = rootView.findViewById(R.id.list_type_spinner);
        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.list_types_array,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listTypesSpinner.setAdapter(spinnerAdapter);
        listTypesSpinner.setOnItemSelectedListener(this);

        Button createListBtn = rootView.findViewById(R.id.create_list_btn);
        createListBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String listTypeSelected = spinnerAdapter.getItem(position).toString();
        Toast.makeText(getContext(), listTypeSelected + " selected", Toast.LENGTH_SHORT)
                .show();
        ;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.create_list_btn:
                Toast.makeText(getContext(), "List created", Toast.LENGTH_SHORT).show();
        }
    }
}
