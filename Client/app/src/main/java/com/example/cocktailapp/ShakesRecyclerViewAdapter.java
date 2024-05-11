package com.example.cocktailapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShakesRecyclerViewAdapter extends RecyclerView.Adapter<ShakesRecyclerViewAdapter.ViewHolder>{
    private ArrayList<ShakesLayoutClass> shakeslayoutlist;
    private Context context;

    public ShakesRecyclerViewAdapter(ArrayList<ShakesLayoutClass> shakeslayoutlist, Context context) {
        this.shakeslayoutlist = shakeslayoutlist;
        this.context = context;
    }

    @NonNull
    @Override
    public ShakesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shakes_rec_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShakesRecyclerViewAdapter.ViewHolder holder, int position) {
        ShakesLayoutClass shakesLayoutClass = shakeslayoutlist.get(position);
        holder.shakeName.setText(shakesLayoutClass.getNome().toString());
        holder.shakePrice.setText(String.valueOf(shakesLayoutClass.getPrezzo()));
        holder.shakeIngredients.setText(shakesLayoutClass.getIngredienti().toString());
    }

    @Override
    public int getItemCount() {
        return shakeslayoutlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView shakeName, shakePrice, shakeIngredients;
        private Button addButton;
        private Spinner amountSpinner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            addButton = itemView.findViewById(R.id.shakes_addButton);
            amountSpinner = itemView.findViewById(R.id.shakes_amountSpinner);
            shakeName = itemView.findViewById(R.id.shake_name);
            shakePrice = itemView.findViewById(R.id.shake_price);
            shakeIngredients = itemView.findViewById(R.id.shake_ingredients);
        }
    }
}
