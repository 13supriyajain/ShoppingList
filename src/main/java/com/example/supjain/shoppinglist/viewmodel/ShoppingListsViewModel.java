package com.example.supjain.shoppinglist.viewmodel;

import android.app.Application;

import com.example.supjain.shoppinglist.data.ShoppingList;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ShoppingListsViewModel extends AndroidViewModel {

    public MutableLiveData<List<ShoppingList>> list = new MutableLiveData<>();
    public ObservableField<String> errorMsg;

    public ShoppingListsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = new ObservableField<>(errorMsg);
    }

    public MutableLiveData<List<ShoppingList>> getList() {
        return this.list;
    }

    public void setList(List<ShoppingList> list) {
        this.list.setValue(list);
    }
}
