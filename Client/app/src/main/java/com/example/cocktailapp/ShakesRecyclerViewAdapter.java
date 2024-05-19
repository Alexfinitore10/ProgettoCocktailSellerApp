package com.example.cocktailapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
        holder.setPosition(position);
        String ingredienti = shakesLayoutClass.getIngredienti().toString();
        String PrezzoShake = String.valueOf(shakesLayoutClass.getPrezzo());
        int imageID = getImageID(shakeslayoutlist.get(position).getNome());

        PrezzoShake = String.format("%.2f", shakesLayoutClass.getPrezzo());
        ingredienti = ingredienti.substring(1,ingredienti.length()-1);

        holder.imageView.setImageResource(imageID);
        holder.shakeName.setText(shakesLayoutClass.getNome().toString());
        holder.shakePrice.setText("Prezzo: " + PrezzoShake +"€");
        holder.shakeIngredients.setText("Ingredienti: "+ingredienti);

        holder.SpinnerInitializer(holder.amountSpinner, position, holder.itemView.getContext());
    }

    @Override
    public int getItemCount() {
        return shakeslayoutlist.size();
    }

    private int getImageID(String nome) {
        int imageResId;
        switch (nome) {
            case "Frullato di frutta":
                imageResId = R.drawable.frullatodifrutta;
                break;
            case "Frullato tropicale":
                imageResId = R.drawable.frullatotropicale;
                break;
            case "Frullato di bacche":
                imageResId = R.drawable.frullatodibacche;
                break;
            case "Frullato proteico":
                imageResId = R.drawable.frullatoproteico;
                break;
            case "Frullato esotico":
                imageResId = R.drawable.frullatoesotico;
                break;
            default:
                imageResId = R.drawable.cocktail_app_icon; // Replace with your default image resource ID
        }
        return imageResId;
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView shakeName, shakePrice, shakeIngredients;
        private Button addButton;
        private Spinner amountSpinner;
        private ImageView imageView;
        private int position;
        private int selectedAmount;

        public void setPosition(int position) {
            this.position = position;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.shake_image);
            addButton = itemView.findViewById(R.id.shakes_addButton);
            amountSpinner = itemView.findViewById(R.id.shakes_amountSpinner);
            shakeName = itemView.findViewById(R.id.shake_name);
            shakePrice = itemView.findViewById(R.id.shake_price);
            shakeIngredients = itemView.findViewById(R.id.shake_ingredients);
            Carrello carrello = Carrello.getInstance();


            addButton.setOnClickListener(v -> {
                Log.d("addButton shake listen", "Entro con Lastsize: "+carrello.getBeverages().size()+ " e size: "+carrello.getBeverages().size());
                Shake shake = shakeslist.get(position);
                selectedAmount = amountSpinner.getSelectedItemPosition();
                shake.setQuantita(selectedAmount);
                carrello.addBeverage(shake);
                carrello.setCartModified(true);
                Toast.makeText(itemView.getContext(), "Frullato aggiunto al carrello", Toast.LENGTH_SHORT).show();
                carrello.viewItems();
                Log.d("addButton shake listen", "Esco con Lastsize: "+carrello.getLastSize()+ " e size: "+carrello.getBeverages().size());
            });
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
