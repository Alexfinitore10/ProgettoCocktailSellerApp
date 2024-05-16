package com.example.cocktailapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CartRecyclerViewAdapter extends RecyclerView.Adapter <CartRecyclerViewAdapter.ViewHolder>{
    private ArrayList<CartLayoutClass> cartLayoutClassArrayList;
    private Context context;

    public CartRecyclerViewAdapter(ArrayList<CartLayoutClass> cartLayoutClassArrayList, Context context) {
        this.cartLayoutClassArrayList = cartLayoutClassArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_rec_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerViewAdapter.ViewHolder holder, int position) {
        CartLayoutClass cartLayoutClass = cartLayoutClassArrayList.get(position);

    }

    @Override
    public int getItemCount() {
        return cartLayoutClassArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView beverageName,beveragePrice,beverageAlcoholVolume,beverageIngredients,beverageQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            beverageName = itemView.findViewById(R.id.beverage_name);
            beveragePrice = itemView.findViewById(R.id.beverage_price);
            beverageAlcoholVolume = itemView.findViewById(R.id.alcohol_volume_opt);
            beverageIngredients = itemView.findViewById(R.id.beverage_ingredients);
            beverageQuantity = itemView.findViewById(R.id.beverage_quantity);
        }
    }
}
