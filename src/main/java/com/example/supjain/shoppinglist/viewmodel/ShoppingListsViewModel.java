package com.example.supjain.shoppinglist.viewmodel;

import android.app.Application;

import com.example.supjain.shoppinglist.data.ShoppingList;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ShoppingListsViewModel extends AndroidViewModel {

    public MutableLiveData<List<ShoppingList>> list = new MutableLiveData<>();
    public MutableLiveData<String> errorMsg = new MutableLiveData<>();

    public ShoppingListsViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg.setValue(errorMsg);
    }

    public MutableLiveData<List<ShoppingList>> getList() {
        return this.list;
    }

    public void setList(List<ShoppingList> list) {
        this.list.setValue(list);
    }
}
