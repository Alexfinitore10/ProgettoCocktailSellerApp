package com.example.cocktailapp;

import androidx.lifecycle.*;

import java.util.*;

public class CartLayoutItemTransfer extends ViewModel {
    private final MutableLiveData<Queue<CartLayoutClass>> toAddItems = new MutableLiveData<>(new LinkedList<>());
    private final MutableLiveData<Queue<CartLayoutClass>> toUpdateItems = new MutableLiveData<>(new LinkedList<>());

    public void setElementToAdd(CartLayoutClass item) {
        Queue<CartLayoutClass> currentItems = toAddItems.getValue();
        assert currentItems != null;
        currentItems.add(item);
        this.toAddItems.setValue(currentItems);
    }

    public void setElementToUpdate(CartLayoutClass item){
        Queue<CartLayoutClass> currentItems = toUpdateItems.getValue();
        assert currentItems != null;
        currentItems.add(item);
        this.toUpdateItems.setValue(currentItems);
    }

    public LiveData<Queue<CartLayoutClass>> getToAddItems() {
        return toAddItems;
    }

    public LiveData<Queue<CartLayoutClass>> getToUpdateItem() {
        return toUpdateItems;
    }
}
