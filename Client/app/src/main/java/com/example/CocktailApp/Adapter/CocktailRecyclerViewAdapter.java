package com.example.CocktailApp.Adapter;


import android.content.Context;
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


import com.example.CocktailApp.R;
import com.example.CocktailApp.Model.Carrello;
import com.example.CocktailApp.Model.Cocktail;


import java.util.ArrayList;
import java.util.List;

public class CocktailRecyclerViewAdapter extends RecyclerView.Adapter <CocktailRecyclerViewAdapter.ViewHolder> {
    private ArrayList<CocktailLayoutClass> cocktailLayoutArrayList;
    private Context context;
    private ArrayList<Cocktail> cocktailList;
    private Carrello carrello;


    public CocktailRecyclerViewAdapter(ArrayList<CocktailLayoutClass> cocktailLayoutArrayList, Context context, ArrayList<Cocktail> cocktailList) {
        this.cocktailLayoutArrayList = cocktailLayoutArrayList;
        this.context = context;
        this.cocktailList = cocktailList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cocktail_rec_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CocktailLayoutClass cocktailLayoutClass = cocktailLayoutArrayList.get(position);
        int image_id = getImageID(cocktailLayoutArrayList.get(position).getNome());
        holder.setPosition(position);


        String ingredienti = cocktailLayoutClass.getIngredienti().toString();
        String PrezzoCocktail = cocktailLayoutClass.getPrezzo().toString();
        String GradazioneAlcolica = cocktailLayoutClass.getGradazione_alcolica().toString();
        ingredienti = ingredienti.substring(1,ingredienti.length()-1);

        holder.cocktailName.setText(cocktailLayoutClass.getNome());
        holder.cocktailPrice.setText("Prezzo: "+ PrezzoCocktail+"€");
        holder.alcoholVolume.setText("Gradazione Alcolica: "+GradazioneAlcolica+"%");
        holder.cocktailIngredients.setText("Ingredienti: "+ingredienti);
        holder.cocktailImage.setImageResource(image_id);

        holder.SpinnerInitializer(holder.amountSpinner, holder.position, holder.itemView.getContext());


    }

    @Override
    public int getItemCount() {
        return cocktailLayoutArrayList.size();
    }



    private int getImageID(String nome) {
        int imageResId;
        switch (nome) {
            case "Mojito":
                imageResId = R.drawable.mojito;
                break;
            case "Bloody Mary":
                imageResId = R.drawable.bloodymary;
                break;
            case "Daquiri":
                imageResId = R.drawable.daquiri;
                break;
            case "White Russian":
                imageResId = R.drawable.whiterussian;
                break;
            case "Negroni":
                imageResId = R.drawable.negroni;
                break;
            case "Dry Martini":
                imageResId = R.drawable.drymartini;
                break;
            case "Margarita":
                imageResId = R.drawable.margarita;
                break;
            case "Manhattan":
                imageResId = R.drawable.manhattan;
                break;
            case "Whiskey Sour":
                imageResId = R.drawable.whiskeysour;
                break;
            case "Moscow Mule":
                imageResId = R.drawable.moscowmule;
                break;
            default:
                imageResId = R.drawable.cocktail_app_icon;
        }
        return imageResId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button addButton;
        private Spinner amountSpinner;
        private ImageView cocktailImage;
        private int position;
        private int selectedAmount;

        public void setPosition(int position) {
            this.position = position;
        }

        private TextView cocktailName, cocktailPrice, alcoholVolume, cocktailIngredients;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cocktailImage = itemView.findViewById(R.id.cocktail_image);
            addButton = itemView.findViewById(R.id.addButton);
            cocktailName = itemView.findViewById(R.id.cocktail_name);
            cocktailPrice = itemView.findViewById(R.id.cocktail_price);
            alcoholVolume = itemView.findViewById(R.id.alcohol_volume);
            cocktailIngredients = itemView.findViewById(R.id.cocktail_ingredients);
            amountSpinner = itemView.findViewById(R.id.cocktailAmountSpinner);
            carrello = Carrello.getInstance();


            addButton.setOnClickListener(v -> {
                carrello.viewItems();
                Cocktail cocktail = cocktailList.get(position);
                if(amountSpinner.getSelectedItem() == null){
                    Toast.makeText(itemView.getContext(), "Bevanda terminata" , Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedAmount = (int) amountSpinner.getSelectedItem();


                if(carrello.isBeverageInCart(cocktail)) {
                    if ((selectedAmount + carrello.getAmountSelectedBeverage(cocktail)) > cocktailList.get(position).getQuantita()) {
                        Toast.makeText(context, "Quantità massima superata", Toast.LENGTH_SHORT).show();
                    } else {
                        int amountSelectedBeverage = carrello.getAmountSelectedBeverage(cocktail);
                        carrello.setAmountSelectedBeverage(cocktail, (selectedAmount + amountSelectedBeverage));
                        Toast.makeText(context, "Altri aggiunti al carrello", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Cocktail temp = new Cocktail(cocktail.getNome(),cocktail.getPrezzo(),cocktail.getIngredienti(),selectedAmount,cocktail.getGradazione_alcolica());
                    carrello.addBeverage(temp);
                    carrello.viewItems();
                    Toast.makeText(context, "Aggiunto al carrello" , Toast.LENGTH_SHORT).show();
                }
            });

        }



        private void SpinnerInitializer(Spinner spinner,int position,Context context) {
            int amount = cocktailList.get(position).getQuantita();



            List<Integer> amounts_list = new ArrayList<>();
            for (int i = 1; i <= amount; i++) {
                amounts_list.add(i);
            }


            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, amounts_list);


            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

        }

    }


}