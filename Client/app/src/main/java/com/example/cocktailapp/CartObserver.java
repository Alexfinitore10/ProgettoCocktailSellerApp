package com.example.cocktailapp;

import androidx.lifecycle.*;

import java.util.*;

public class CartObserver extends ViewModel {
    private final MutableLiveData<Queue<CartLayoutClass>> toAddItems = new MutableLiveData<>(new LinkedList<>());
    private final MutableLiveData<Queue<CartLayoutClass>> toUpdateItems = new MutableLiveData<>(new LinkedList<>());
    private final MutableLiveData<Queue<Double>> totalCartValue = new MutableLiveData<>(new LinkedList<>());
    private final MutableLiveData<Boolean> paymentSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

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

    public void setTotalCartValue(double value){
        Queue<Double> currentItems = totalCartValue.getValue();
        assert currentItems != null;
        currentItems.add(value);
        this.totalCartValue.setValue(currentItems);
    }

    public void setIsLoggedIn(boolean loggedIn){
        this.isLoggedIn.setValue(loggedIn);
    }

    public void setPaymentSuccess(boolean success){
        this.paymentSuccess.setValue(success);
    }

    public LiveData<Queue<Double>> getTotalCartValue() {
        return totalCartValue;
    }

    public LiveData<Queue<CartLayoutClass>> getToAddItems() {
        return toAddItems;
    }

    public LiveData<Queue<CartLayoutClass>> getToUpdateItem() {
        return toUpdateItems;
    }

    public LiveData<Boolean> getPaymentSuccess() {
        return paymentSuccess;
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }


}
