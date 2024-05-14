package com.example.cocktailapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShakesRecyclerViewAdapter extends RecyclerView.Adapter<ShakesRecyclerViewAdapter.ViewHolder>{
    private ArrayList<ShakesLayoutClass> shakeslayoutlist;
    private Context context;

    private ArrayList<Shake> shakeslist;

    public ShakesRecyclerViewAdapter(ArrayList<ShakesLayoutClass> shakeslayoutlist, Context context, ArrayList<Shake> shakeslist) {
        this.shakeslayoutlist = shakeslayoutlist;
        this.context = context;
        this.shakeslist = shakeslist;
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
        String ingredienti = shakesLayoutClass.getIngredienti().toString();
        String PrezzoShake = String.valueOf(shakesLayoutClass.getPrezzo());

        PrezzoShake = String.format("%.2f", shakesLayoutClass.getPrezzo());
        ingredienti = ingredienti.substring(1,ingredienti.length()-1);

        holder.shakeName.setText(shakesLayoutClass.getNome().toString());
        holder.shakePrice.setText("Prezzo: " + PrezzoShake +"€");
        holder.shakeIngredients.setText("Ingredienti: "+ingredienti);

        holder.SpinnerInitializer(holder.amountSpinner, position, holder.itemView.getContext());
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

        private void SpinnerInitializer(Spinner spinner,int position, Context context) {
            int amount = shakeslist.get(position).getQuantita();
            List<Integer> amounts_list = new ArrayList<>();

            for (int i = 1; i <= amount; i++) {
                amounts_list.add(i);
            }

            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_spinner_item,
                    amounts_list
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            spinner.setAdapter(adapter);
        }
    }
}
