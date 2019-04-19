package com.example.supjain.shoppinglist.data;

import java.util.List;

public class ShoppingList {

    private long shoppingListId;
    private String shoppingListName;
    private String shoppingListType;
    private int shoppingListImageId = -1;
    private List<Store> stores;

    public ShoppingList() {
    }

    public ShoppingList(long shoppingListId, String shoppingListName, String shoppingListType,
                        int shoppingListImageId, List<Store> stores) {
        this.shoppingListId = shoppingListId;
        this.shoppingListName = shoppingListName;
        this.shoppingListType = shoppingListType;
        this.shoppingListImageId = shoppingListImageId;
        this.stores = stores;
    }

    public long getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(long shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public String getShoppingListName() {
        return shoppingListName;
    }

    public void setShoppingListName(String shoppingListName) {
        this.shoppingListName = shoppingListName;
    }

    public String getShoppingListType() {
        return shoppingListType;
    }

    public void setShoppingListType(String shoppingListType) {
        this.shoppingListType = shoppingListType;
    }

    public int getShoppingListImageId() {
        return shoppingListImageId;
    }

    public void setShoppingListImageId(int shoppingListImageId) {
        this.shoppingListImageId = shoppingListImageId;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }
}