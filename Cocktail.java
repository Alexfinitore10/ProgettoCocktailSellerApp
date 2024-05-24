import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cocktail {
    private String nome;
    private List<String> ingredienti;
    private float gradazioneAlcolica;
    private float prezzo;
    private int quantita;

    // Costruttore
    public Cocktail(String nome, List<String> ingredienti, float gradazioneAlcolica, float prezzo, int quantita) {
        this.nome = nome;
        this.ingredienti = ingredienti;
        this.gradazioneAlcolica = gradazioneAlcolica;
        this.prezzo = prezzo;
        this.quantita = quantita;
    }

    // Getter e setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<String> ingredienti) {
        this.ingredienti = ingredienti;
    }

    public float getGradazioneAlcolica() {
        return gradazioneAlcolica;
    }

    public void setGradazioneAlcolica(float gradazioneAlcolica) {
        this.gradazioneAlcolica = gradazioneAlcolica;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    // Metodo per parsare la stringa e creare un oggetto Cocktail
    public static Cocktail parseString(String input) {
        String[] parts = input.split(", ");

        String nome = parts[0].trim();

        // Rimuovi le parentesi quadre dagli ingredienti
        String ingredientiString = parts[1].substring(1, parts[1].length() - 1).trim();
        String[] ingredientiArray = ingredientiString.split(";");

        float gradazioneAlcolica = Float.parseFloat(parts[2].trim());
        float prezzo = Float.parseFloat(parts[3].trim());
        int quantita = Integer.parseInt(parts[4].trim());

        return new Cocktail(nome, Arrays.asList(ingredientiArray), gradazioneAlcolica, prezzo, quantita);
    }

    // Metodo per settare i cocktail raccomandati e restituire una lista di Cocktail
    public static List<Cocktail> setRecommendedCocktails(String buffer) {
        List<Cocktail> recommendedCocktails = new ArrayList<Cocktail>();
        String[] cocktails = buffer.split("\n");

        for (String cocktail : cocktails) {
            Cocktail recommendedCocktail = parseString(cocktail);
            recommendedCocktails.add(recommendedCocktail);
        }
        return recommendedCocktails;
    }

    // Metodo toString per rappresentazione testuale dell'oggetto
    @Override
    public String toString() {
        return "Cocktail{" +
                "nome='" + nome + '\'' +
                ", ingredienti=" + ingredienti +
                ", gradazioneAlcolica=" + gradazioneAlcolica +
                ", prezzo=" + prezzo +
                ", quantita=" + quantita +
                '}';
    }

    // Metodo main per testare la classe
    public static void main(String[] args) {
        // Stringa ricevuta dal server
        String buffer = "Mojito, [Rum;Lime;Zucchero;Menta], 18, 6, 10\nWhite Russian, [Vodka;Liquore al caffè;Ghiaccio;Panna fresca], 25, 7, 16\nDaquiri, [Rum;Succo di lime;Zucchero;Ghiaccio;Gocce di maraschino], 18.9, 6.44, 10";

        List<Cocktail> topCocktails = setRecommendedCocktails(buffer);

        for (Cocktail cocktail : topCocktails) {
            System.out.println(cocktail);
        }
    }
}
