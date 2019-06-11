package com.example.supjain.shoppinglist.viewmodel;

import android.app.Application;

import com.example.supjain.shoppinglist.data.Store;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ItemListViewModel extends AndroidViewModel {

    public MutableLiveData<List<Store>> list = new MutableLiveData<>();
    public ObservableField<String> errorMsg;

    public ItemListViewModel(@NonNull Application application) {
        super(application);
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = new ObservableField<>(errorMsg);
    }

    public MutableLiveData<List<Store>> getList() {
        return this.list;
    }

    public void setList(List<Store> list) {
        this.list.setValue(list);
    }
}
