package com.example.supjain.shoppinglist.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

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
    private long storeId;
    private String storeName;
    private String storeLocation;
    private List<Item> items;

    public Store() {
    }

    public Store(long storeId, String storeName, String storeLocation, List<Item> items) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
        this.items = items;
    }

    protected Store(Parcel in) {
        storeId = in.readLong();
        storeName = in.readString();
        storeLocation = in.readString();
        items = in.createTypedArrayList(Item.CREATOR);
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
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
        dest.writeLong(storeId);
        dest.writeString(storeName);
        dest.writeString(storeLocation);
        dest.writeTypedList(items);
    }
}
