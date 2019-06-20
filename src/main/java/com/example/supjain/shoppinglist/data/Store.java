package com.example.supjain.shoppinglist.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Store implements Parcelable {

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    private static AtomicInteger uniqueId = new AtomicInteger();
    private int storeId;
    private String storeName;
    private List<Item> items;

    public Store() {
    }

    public Store(String storeName, List<Item> items) {
        this.storeId = uniqueId.getAndIncrement();
        this.storeName = storeName;
        this.items = items;
    }

    protected Store(Parcel in) {
        storeId = in.readInt();
        storeName = in.readString();
        items = in.createTypedArrayList(Item.CREATOR);
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(storeId);
        dest.writeString(storeName);
        dest.writeTypedList(items);
    }
}
