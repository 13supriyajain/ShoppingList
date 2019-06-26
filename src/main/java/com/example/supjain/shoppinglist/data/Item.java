package com.example.supjain.shoppinglist.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

public class Item implements Parcelable {

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    Random random = new Random();
    private String itemId;
    private String itemName;
    private float itemQuantity;
    private String itemMeasurement;
    private int itemMarkedPurchased;
    private String itemStoreId;

    public Item() {
    }

    public Item(String itemName, float itemQuantity, String itemMeasurement) {
        this.itemId = itemName + random.nextLong();
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemMeasurement = itemMeasurement;
        this.itemStoreId = null; // Default value upon item creation
        this.itemMarkedPurchased = 0; // Default value upon item creation
    }

    protected Item(Parcel in) {
        itemId = in.readString();
        itemName = in.readString();
        itemQuantity = in.readFloat();
        itemMeasurement = in.readString();
        itemMarkedPurchased = in.readInt();
        itemStoreId = in.readString();
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(float itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemMeasurement() {
        return itemMeasurement;
    }

    public void setItemMeasurement(String itemMeasurement) {
        this.itemMeasurement = itemMeasurement;
    }

    public int getItemMarkedPurchased() {
        return itemMarkedPurchased;
    }

    public void setItemMarkedPurchased(int itemMarkedPurchased) {
        this.itemMarkedPurchased = itemMarkedPurchased;
    }

    public String getItemStoreId() {
        return itemStoreId;
    }

    public void setItemStoreId(String itemStoreId) {
        this.itemStoreId = itemStoreId;
    }

    @Override
    public String toString() {
        return this.itemName + " -- " + this.itemQuantity + " " + this.itemMeasurement;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(itemName);
        dest.writeFloat(itemQuantity);
        dest.writeString(itemMeasurement);
        dest.writeInt(itemMarkedPurchased);
        dest.writeString(itemStoreId);
    }
}