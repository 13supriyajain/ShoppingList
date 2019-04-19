package com.example.supjain.shoppinglist.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;

public class ShoppingListsViewModel extends AndroidViewModel {

    public List<?> list;
    public ObservableField<String> errorMsg;

    public ShoppingListsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = new ObservableField<>(errorMsg);
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
