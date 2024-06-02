package com.example.cocktailapp;

import androidx.lifecycle.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CartObserver extends ViewModel {
    private final MutableLiveData<Queue<CartLayoutClass>> toAddItems = new MutableLiveData<>(new LinkedList<>());
    private final MutableLiveData<Queue<CartLayoutClass>> toUpdateItems = new MutableLiveData<>(new LinkedList<>());
    private final MutableLiveData<Queue<Double>> totalCartValue = new MutableLiveData<>(new LinkedList<>());
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private final MutableLiveData<String> allCocktails = new MutableLiveData<>();
    private final MutableLiveData<String> allShakes = new MutableLiveData<>();
    private final MutableLiveData<String> recommendedCocktails = new MutableLiveData<>();
    private final MutableLiveData<String> recommendedShakes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resetCocktails = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resetShakes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resetCart = new MutableLiveData<>();

    public void setResetCart(Boolean resetCart) {
        this.resetCart.postValue(resetCart);
    }

    public void setResetCocktails(Boolean resetCocktails) {
        this.resetCocktails.postValue(resetCocktails);
    }

    public void setResetShakes(Boolean resetShakes) {
        this.resetShakes.postValue(resetShakes);
    }


    public void setRecommendedCocktails(String recommendedCocktails) {
        this.recommendedCocktails.postValue(recommendedCocktails);
    }

    public void setRecommendedShakes(String recommendedShakes) {
        this.recommendedShakes.postValue(recommendedShakes);
    }


    public void setAllCocktails(String allCocktails) {
        this.allCocktails.postValue(allCocktails);
    }

    public void setAllShakes(String allShakes) {
        this.allShakes.postValue(allShakes);
    }

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


    public LiveData<Queue<Double>> getTotalCartValue() {
        return totalCartValue;
    }

    public LiveData<Queue<CartLayoutClass>> getToAddItems() {
        return toAddItems;
    }

    public LiveData<Queue<CartLayoutClass>> getToUpdateItem() {
        return toUpdateItems;
    }



    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public String getAllCocktails() {
        return allCocktails.getValue();
    }

    public String getAllShakes() {
        return allShakes.getValue();
    }

    public String getRecommendedCocktails() {
        return recommendedCocktails.getValue();
    }

    public String getRecommendedShakes() {
        return recommendedShakes.getValue();
    }

    public LiveData<Boolean> getResetCocktails() {
        return resetCocktails;
    }

    public LiveData<Boolean> getResetShakes() {
        return resetShakes;
    }

    public LiveData<Boolean> getResetCart() {
        return resetCart;
    }

}
