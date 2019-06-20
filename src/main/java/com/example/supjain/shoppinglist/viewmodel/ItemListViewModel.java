package com.example.supjain.shoppinglist.viewmodel;

import android.app.Application;

import com.example.supjain.shoppinglist.data.Store;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ItemListViewModel extends AndroidViewModel {

    public MutableLiveData<List<Store>> list = new MutableLiveData<>();
    public MutableLiveData<String> errorMsg = new MutableLiveData<>();

    public ItemListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg.setValue(errorMsg);
    }

    public MutableLiveData<List<Store>> getList() {
        return this.list;
    }

    public void setList(List<Store> list) {
        this.list.setValue(list);
    }
}
