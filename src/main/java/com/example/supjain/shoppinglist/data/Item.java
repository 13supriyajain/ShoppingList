package com.example.supjain.shoppinglist.data;

import android.os.Parcel;
import android.os.Parcelable;

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
    private long itemId;
    private String itemName;
    private float itemQuantity;
    private String itemMeasurement;

    public Item() {
    }

    public Item(long itemId, String itemName, float itemQuantity, String itemMeasurement) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemMeasurement = itemMeasurement;
    }

    protected Item(Parcel in) {
        itemId = in.readLong();
        itemName = in.readString();
        itemQuantity = in.readFloat();
        itemMeasurement = in.readString();
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
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
        dest.writeLong(itemId);
        dest.writeString(itemName);
        dest.writeFloat(itemQuantity);
        dest.writeString(itemMeasurement);
    }
}
