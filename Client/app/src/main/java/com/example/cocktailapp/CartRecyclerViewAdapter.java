package com.example.cocktailapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public class CartRecyclerViewAdapter extends RecyclerView.Adapter <CartRecyclerViewAdapter.ViewHolder> {
    private ArrayList<CartLayoutClass> cartLayoutClassArrayList;
    private Context context;
    private Carrello carrello;
    private ArrayList<Cocktail> cocktailList;
    private ArrayList<Shake> shakeList;
    private CartObserver itemTransfer;


    public CartRecyclerViewAdapter(ArrayList<CartLayoutClass> cartLayoutClassArrayList, Context context, ArrayList<Cocktail> cocktailList, ArrayList<Shake> shakeList, CartObserver itemTransfer) {
        this.cartLayoutClassArrayList = cartLayoutClassArrayList;
        this.context = context;
        this.cocktailList = cocktailList;
        this.shakeList = shakeList;
        this.itemTransfer = itemTransfer;
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
                int image_id = getImageID(cartLayoutClassArrayList.get(position).getBevanda().getNome());
                holder.setPosition(position);

                String ingredienti = cartLayoutClass.getBevanda().getIngredienti().toString();
                String prezzoBevanda = String.valueOf(cartLayoutClass.getBevanda().getPrezzo()*cartLayoutClass.getBevanda().getQuantita());
                if(cartLayoutClass.getBevanda() instanceof Cocktail){
                    String GradazioneAlcolica = String.valueOf(((Cocktail) cartLayoutClass.getBevanda()).getGradazione_alcolica());
                    GradazioneAlcolica = String.format("%.2f", ((Cocktail) cartLayoutClass.getBevanda()).getGradazione_alcolica());
                    holder.beverageAlcoholVolume.setText("Gradazione Alcolica: "+GradazioneAlcolica+"%");
                }else{
                    holder.beverageAlcoholVolume.setText("Gradazione Alcolica: Analcolico");
                }
                prezzoBevanda = String.format("%.2f",cartLayoutClass.getBevanda().getPrezzo()*cartLayoutClass.getBevanda().getQuantita());
                ingredienti = ingredienti.substring(1,ingredienti.length()-1);

                holder.beverageName.setText(cartLayoutClass.getBevanda().getNome());
                holder.beveragePrice.setText("Prezzo: "+prezzoBevanda+"€");
                holder.beverageIngredients.setText("Ingredienti: "+ingredienti);
                holder.beverageQuantity.setText("Quantità: "+String.valueOf(cartLayoutClass.getBevanda().getQuantita()));
                holder.CartImageView.setImageResource(image_id);
                holder.spinnerInitializer(holder.addAnotherSpinner,holder.getAvailableAmount(cartLayoutClass)-cartLayoutClass.getBevanda().getQuantita(),context);
                holder.spinnerInitializer(holder.removeAnotherSpinner,cartLayoutClass.getBevanda().getQuantita(),context);
    }

    @Override
    public int getItemCount() {
        return cartLayoutClassArrayList.size();
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




    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView CartImageView;
        private TextView beverageName,beveragePrice,beverageAlcoholVolume,beverageIngredients,beverageQuantity;
        private int position;
        private Spinner addAnotherSpinner,removeAnotherSpinner;
        private Button addAnotherButton,removeAnotherButton;

        public void setPosition(int position) {
            this.position = position;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            beverageName = itemView.findViewById(R.id.beverage_name);
            beveragePrice = itemView.findViewById(R.id.beverage_price);
            beverageAlcoholVolume = itemView.findViewById(R.id.alcohol_volume_opt);
            beverageIngredients = itemView.findViewById(R.id.beverage_ingredients);
            beverageQuantity = itemView.findViewById(R.id.beverage_quantity);
            CartImageView = itemView.findViewById(R.id.beverage_image);
            addAnotherSpinner = itemView.findViewById(R.id.addAmountSpinner);
            removeAnotherSpinner = itemView.findViewById(R.id.removeAmountSpinner);
            addAnotherButton = itemView.findViewById(R.id.addAnotherButton);
            removeAnotherButton = itemView.findViewById(R.id.removeAnotherButton);
            carrello = Carrello.getInstance();

            addAnotherButton.setOnClickListener(view -> {
                Bevanda bevanda = cartLayoutClassArrayList.get(position).getBevanda();
                int selectedAmount = (int) addAnotherSpinner.getSelectedItem();
                Log.d("addAnotherButton","La bevanda selezionata è: " + bevanda.toString());

                int amountSelectedBeverage = carrello.getAmountSelectedBeverage(bevanda);
                carrello.setAmountSelectedBeverage(bevanda, selectedAmount+amountSelectedBeverage);
                itemTransfer.setTotalCartValue(carrello.calculateTotal());
                bevanda.setQuantita(selectedAmount+amountSelectedBeverage);
                cartLayoutClassArrayList.set(position,new CartLayoutClass(bevanda));
                notifyItemChanged(position);
                Toast.makeText(itemView.getContext(), "Altri aggiunti al carrello" , Toast.LENGTH_SHORT).show();


            });

            removeAnotherButton.setOnClickListener(view -> {
                int positionToRemove = getBindingAdapterPosition();
                if(positionToRemove == RecyclerView.NO_POSITION){
                    Log.e("removeAnotherButton","Errore durante la ricerca della bevanda selezionata");
                    Toast.makeText(itemView.getContext(), "Errore durante la ricerca della bevanda selezionata" , Toast.LENGTH_SHORT).show();
                    return;
                }
                Bevanda bevanda = cartLayoutClassArrayList.get(positionToRemove).getBevanda();
                int selectedAmount = (int) removeAnotherSpinner.getSelectedItem();

                int amountSelectedBeverage = carrello.getAmountSelectedBeverage(bevanda);
                if((amountSelectedBeverage-selectedAmount) == 0){

                    showRemovalDialog(result -> {
                        if (result) {
                            if(positionToRemove >= 0 && positionToRemove < cartLayoutClassArrayList.size()){
                                try{
                                    cartLayoutClassArrayList.remove(positionToRemove);
                                    carrello.removeBeverage(bevanda);
                                    notifyItemRemoved(positionToRemove);
                                    carrello.viewItems();
                                }catch(Exception e){
                                    Log.e("removeAnotherButton","Errore nella rimozione dal carrello: "+e.getMessage());
                                }
                            }
                        }
                    });

                }else{
                    carrello.setAmountSelectedBeverage(bevanda, amountSelectedBeverage-selectedAmount);
                    bevanda.setQuantita(amountSelectedBeverage-selectedAmount);
                    itemTransfer.setTotalCartValue(carrello.calculateTotal());
                    cartLayoutClassArrayList.set(positionToRemove,new CartLayoutClass(bevanda));
                    notifyItemChanged(positionToRemove);
                    carrello.viewItems();
                    Toast.makeText(itemView.getContext(), "Altri rimossi dal carrello" , Toast.LENGTH_SHORT).show();
                }

                carrello.viewItems();
            });


        }

        private void spinnerInitializer(Spinner spinner, int maxAmount, Context context){

            List<Integer> amounts_list = new ArrayList<>();
            for (int i = 1; i <= maxAmount; i++) {
                amounts_list.add(i);
            }


            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, amounts_list);


            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);
        }

        private int getAvailableAmount(CartLayoutClass input){
            Bevanda temp = input.getBevanda();
            if(temp instanceof Cocktail){
                for(int i = 0; i < cocktailList.size(); i++){
                    if(cocktailList.get(i).getNome().equals(temp.getNome())){
                        return cocktailList.get(i).getQuantita();
                    }
                }
            }else{
                for(int i = 0; i < shakeList.size(); i++){
                    if(shakeList.get(i).getNome().equals(temp.getNome())){
                        return shakeList.get(i).getQuantita();
                    }
                }
            }
            return 0;
        }


        private void showRemovalDialog(Consumer<Boolean> resultHandler) {
            
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Rimozione dal carrello")
                    .setMessage("Vuoi davvero rimuovere questo elemento dal carrello?")
                    .setPositiveButton("Si", (dialog, which) -> {
                        resultHandler.accept(true);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        resultHandler.accept(false);
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

            // Change the color of the positive button
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#F98500"));

            // Change the color of the negative button
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F98500"));


        }


    }


}
