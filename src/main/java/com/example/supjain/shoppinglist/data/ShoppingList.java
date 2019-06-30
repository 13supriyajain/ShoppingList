package com.example.supjain.shoppinglist.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ShoppingList implements Parcelable {

    private String shoppingListName;
    private String shoppingListType;
    private int shoppingListImageId = -1;
    public static final Creator<ShoppingList> CREATOR = new Creator<ShoppingList>() {
        @Override
        public ShoppingList createFromParcel(Parcel in) {
            return new ShoppingList(in);
        }

        @Override
        public ShoppingList[] newArray(int size) {
            return new ShoppingList[size];
        }
    };

    public ShoppingList() {
    }

    private ArrayList<Store> stores;

    public ShoppingList(String shoppingListName, String shoppingListType,
                        int shoppingListImageId, ArrayList<Store> stores) {
        this.shoppingListName = shoppingListName;
        this.shoppingListType = shoppingListType;
        this.shoppingListImageId = shoppingListImageId;
        this.stores = stores;
    }

    private ShoppingList(Parcel in) {
        shoppingListName = in.readString();
        shoppingListType = in.readString();
        shoppingListImageId = in.readInt();
        stores = in.createTypedArrayList(Store.CREATOR);
    }

    public String getShoppingListName() {
        return shoppingListName;
    }

    public int getShoppingListImageId() {
        return shoppingListImageId;
    }

    public ArrayList<Store> getStores() {
        return stores;
    }

    public void setStores(ArrayList<Store> stores) {
        this.stores = stores;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shoppingListName);
        dest.writeString(shoppingListType);
        dest.writeInt(shoppingListImageId);
        dest.writeTypedList(stores);
    }
}
