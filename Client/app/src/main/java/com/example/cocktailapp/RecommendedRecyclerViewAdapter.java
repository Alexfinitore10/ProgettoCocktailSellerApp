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

public class RecommendedRecyclerViewAdapter extends RecyclerView.Adapter<RecommendedRecyclerViewAdapter.ViewHolder> {
    private ArrayList<RecommendedLayoutClass> recommendedLayoutArrayList;
    private Context context;
    private Carrello carrello;
    private ArrayList<Cocktail> cocktailList;
    private ArrayList<Shake> shakeList;
    private CartObserver itemTransfer;

    public RecommendedRecyclerViewAdapter(ArrayList<RecommendedLayoutClass> recommendedLayoutArrayList, Context context, ArrayList<Cocktail> cocktailList, ArrayList<Shake> shakeList, CartObserver itemTransfer) {
        this.recommendedLayoutArrayList = recommendedLayoutArrayList;
        this.context = context;
        this.cocktailList = cocktailList;
        this.shakeList = shakeList;
        this.itemTransfer = itemTransfer;
    }

    @NonNull
    @Override
    public RecommendedRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_rec_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedRecyclerViewAdapter.ViewHolder holder, int position) {
        RecommendedLayoutClass recommendedLayoutClass = recommendedLayoutArrayList.get(position);
        int image_id = getImageID(recommendedLayoutArrayList.get(position).getBevanda().getNome());
        holder.setPosition(position);

        String ingredienti = recommendedLayoutClass.getBevanda().getIngredienti().toString();
        String prezzoBevanda = recommendedLayoutClass.getBevanda().getPrezzo().toString();
        if(recommendedLayoutClass.getBevanda() instanceof Cocktail){
            String GradazioneAlcolica = String.format("%.2f", ((Cocktail) recommendedLayoutClass.getBevanda()).getGradazione_alcolica());
            holder.recommendedAlcoholVolume.setText("Gradazione Alcolica: "+GradazioneAlcolica+"%");
        }else{
            holder.recommendedAlcoholVolume.setText("Gradazione Alcolica: Analcolico");
        }
        ingredienti = ingredienti.substring(1,ingredienti.length()-1);

        holder.recommendedName.setText(recommendedLayoutClass.getBevanda().getNome());
        holder.recommendedPrice.setText("Prezzo: "+prezzoBevanda+"€");
        holder.recommendedIngredients.setText("Ingredienti: "+ingredienti);
        holder.RecommendedImageView.setImageResource(image_id);
        holder.SpinnerInitializer(holder.amountSpinner, position, holder.itemView.getContext());

    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
    }


    public void setCocktailList(ArrayList<Cocktail> input) {
        this.cocktailList = input;
        Log.d("RecommendedAdapter","cocktailList: "+cocktailList);
    }

    public void setShakeList(ArrayList<Shake> shakeListInput) {
        this.shakeList = shakeListInput;
        Log.d("RecommendedAdapter","shakeList: "+shakeList);
    }

    @Override
    public int getItemCount() {
        return recommendedLayoutArrayList.size();
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button addButton;
        private ImageView RecommendedImageView;
        private Spinner amountSpinner;
        private int position,selectedAmount;
        private TextView recommendedName, recommendedPrice, recommendedAlcoholVolume, recommendedIngredients;

        public void setPosition(int position) {
            this.position = position;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recommendedName = itemView.findViewById(R.id.recommended_name);
            recommendedPrice = itemView.findViewById(R.id.recommended_price);
            recommendedAlcoholVolume = itemView.findViewById(R.id.recommended_alcohol_volume);
            recommendedIngredients = itemView.findViewById(R.id.recommended_ingredients);
            RecommendedImageView = itemView.findViewById(R.id.recommended_image);
            amountSpinner = itemView.findViewById(R.id.recommendedAmountSpinner);
            addButton = itemView.findViewById(R.id.recommendedAddButton);
            carrello = Carrello.getInstance();


            addButton.setOnClickListener(v -> {
                carrello.viewItems();
                Bevanda bevanda = recommendedLayoutArrayList.get(position).getBevanda();
                if(amountSpinner.getSelectedItem() == null){
                    Toast.makeText(itemView.getContext(), "Bevanda terminata" , Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedAmount = (int) amountSpinner.getSelectedItem();

                if(carrello.isBeverageInCart(bevanda)) {
                        if ((selectedAmount + carrello.getAmountSelectedBeverage(bevanda)) > getRecommendedAmount(bevanda)) {
                            Toast.makeText(context, "Quantità massima superata", Toast.LENGTH_SHORT).show();
                        } else {
                            int amountSelectedBeverage = carrello.getAmountSelectedBeverage(bevanda);
                            carrello.setAmountSelectedBeverage(bevanda, (selectedAmount + amountSelectedBeverage));
                            itemTransfer.setTotalCartValue(carrello.calculateTotal());
                            if(bevanda instanceof Cocktail){
                                Cocktail temp = new Cocktail(bevanda.getNome(),bevanda.getPrezzo(),bevanda.getIngredienti(),(selectedAmount +amountSelectedBeverage),((Cocktail) bevanda).getGradazione_alcolica());
                                itemTransfer.setElementToUpdate(new CartLayoutClass(temp));
                            }else{
                                Shake temp = new Shake(bevanda.getNome(),bevanda.getPrezzo(),bevanda.getIngredienti(),(selectedAmount +amountSelectedBeverage));
                                itemTransfer.setElementToUpdate(new CartLayoutClass(temp));
                            }
                            Toast.makeText(context, "Altri aggiunti al carrello", Toast.LENGTH_SHORT).show();
                        }

                }else{
                    if(bevanda instanceof Cocktail){
                        Cocktail temp = new Cocktail(bevanda.getNome(),bevanda.getPrezzo(),bevanda.getIngredienti(),selectedAmount,((Cocktail) bevanda).getGradazione_alcolica());
                        carrello.addBeverage(temp);
                        carrello.viewItems();
                        itemTransfer.setElementToAdd(new CartLayoutClass(temp));
                    }else{
                        Shake temp = new Shake(bevanda.getNome(),bevanda.getPrezzo(),bevanda.getIngredienti(),selectedAmount);
                        carrello.addBeverage(temp);
                        carrello.viewItems();
                        itemTransfer.setElementToAdd(new CartLayoutClass(temp));
                    }

                    Toast.makeText(context, "Aggiunto al carrello" , Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void SpinnerInitializer(Spinner spinner, int position, Context context) {
            Bevanda bevanda = recommendedLayoutArrayList.get(position).getBevanda();
            int amount = getRecommendedAmount(bevanda);


            List<Integer> amounts_list = new ArrayList<>();
            for (int i = 1; i <= amount; i++) {
                amounts_list.add(i);
            }


            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, amounts_list);


            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);
        }

        private int getRecommendedAmount(Bevanda bevanda){
            if(bevanda instanceof Cocktail){
                for(int i = 0; i < cocktailList.size(); i++){
                    if(cocktailList.get(i).getNome().equals(bevanda.getNome())) {
                        return cocktailList.get(i).getQuantita();
                    }
                }
            }else{
                for(int i = 0; i < shakeList.size(); i++){
                    if(shakeList.get(i).getNome().equals(bevanda.getNome())) {
                        return shakeList.get(i).getQuantita();
                    }
                }
            }
            return 0;
        }
    }


}
