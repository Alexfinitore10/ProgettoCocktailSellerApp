import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.util.regex.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import carrello.java.*;

/* class Cocktail {
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
} */

public class client {
    //il cocktail
    Cocktail recommended;

    // glio carrello
    carrello c = new carrello();

    boolean isLogged = false;

    // Regex
    String regex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    // Socket for client communication
    Socket clientSocket;
    String address = "127.0.0.1";
    int port = 5978;

    // Buffer for input and output streams
    PrintWriter out;
    BufferedReader input;

    Scanner scanner = new Scanner(System.in);
    StringBuilder stringBuilder = new StringBuilder();

    public static void main(String[] args) throws IOException, InterruptedException {
        client c = new client();

        // Establish connection with the server
        boolean connected = false;
        int numTries = 0;
        do {
            connected = c.createConnection();
            numTries++;
            if (!connected) {
                System.out.println("Connessione fallita. Riprovo tra 5 secondi... (" + numTries + "/10)");
                Thread.sleep(5000);
            }
        } while (!connected && numTries < 10);
        if (!connected) {
            System.err.println("Impossibile stabilire la connessione con il server. Termino il programma.");
            System.exit(1);
        }

        String risposta;
        do {
            if (c.isLogged == false) {
                risposta = c.menu();
            } else {
                risposta = c.menuLoggato();
                continue;
            }
            if (!(risposta.isEmpty())) {
                // receive message
                if (risposta.equals("OK")) {
                    c.isLogged = true;
                }
                continue;
            } else {
                System.err.println("La stringa ricevuto è vuota. Termino il programma.");
                break;
            }
        } while (connected);

        c.closeConnection();
    }

    String bufferedReceive() {
        try {
            CharBuffer charBuffer = CharBuffer.allocate(1024);
            int ric = input.read(charBuffer);

            String rispostadato = new String(charBuffer.array(), 0, ric);

            System.out.println("La stringa letta dal server è : " + rispostadato);
            // elaborazione... però devo vede un attimo
            return rispostadato;
        } catch (Exception e) {
            System.err.println("Errore durante la lettura del server: a rigo 130 " + e.getMessage());
            return e.getMessage();
        }
    }

    String receive() {
        try {
            // ovviamente mi aspetto che sia una stringa con fine
            String risposta = input.readLine();
            System.out.println("La risposta è: " + risposta);
            return risposta;
        } catch (Exception e) {
            System.err.println("Errore durante la lettura del server: " + e.getMessage());
            return e.getMessage();
        }
    }

    String menuLoggato() throws SocketException {
        String risposta = "";
        String rispostaServer = "";

        try {
            clientSocket.setSoTimeout(5000);

            while (true) {
                System.out.println("1) Visualizza Drink");
                System.out.println("2) Visualizza Fruit Shakes");
                System.out.println("3) Aggiungi Cocktail");
                System.out.println("4) Aggiungi Shake");
                System.out.println("5) Visualizza Carrello");
                System.out.println("6) Esci");
                System.out.println("10) Effettua acquisto");
                System.out.println("11) Recommend Drinks");
                System.out.println("12) Recommend Shakes");

                Scanner scanner = new Scanner(System.in);
                risposta = scanner.nextLine();

                if (risposta.matches("^(?:[1-9]|1[0-2])$")) {
                    break;
                }

                System.out.println("Inserimento non valido. Riprova");
            }

            switch (Integer.parseInt(risposta)) {
                case 1:
                    risposta = "3";// drinks
                    break;
                case 2:
                    risposta = "4";// shakes
                    break;
                case 3:
                    risposta = "5";// aggiungi cocktail al carrello
                    break;
                case 4:
                    risposta = "9";// aggiungi shake al carrello
                    break;
                case 5:
                    risposta = "7";// visualizza carrello
                    break;
                default:
                    break;
            }
            if (!risposta.equals("5") && !risposta.equals("7") && !risposta.equals("9") && !risposta.equals("10") && !risposta.equals("11") && !risposta.equals("11")) {
                out.println(risposta);
            }

            // rispostaServer = receive();// ricevo il case

            switch (Integer.parseInt(risposta)) {
                case 3:
                    // visualizzaDrink();// manda comando
                    receiveAndParseCocktail();// sta funzione returna
                    break;
                case 4:
                    // visualizzaShakes();
                    receiveAndParseShake();
                    break;
                case 5:
                    // aggiungiCocktailAlCarrello
                    System.out.println("Il cliente vuole aggiungere un cocktail al carrello");
                    chooseCocktail();
                    c.viewItems();
                    break;
                case 6:// Disconnessione
                    isLogged = false;
                    System.out.println("Logout effettuato... Ritorno al menu");
                    break;
                case 7:// Vedi Carrello
                    c.viewItems();
                    break;
                case 9:// Inserisci Shake al carrello
                    chooseShake();
                    c.viewItems();
                    break;
                case 10:// Invio a server di cancella cocktail e shake
                    deleteShakeNCocktails();
                    break;
                case 11:// Recommend Drinks
                    recommend_drinks();
                    break;
                case 12:// Recommended Shakes
                    recommend_shakes();
                    break;
                default:
                    break;
            }

            return rispostaServer;
        } catch (IOException e) {
            System.err.println("IOException durante la lettura: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Errore durante la lettura: " + e.getMessage());
            return null;
        }
    }

    ArrayList<Shake> receiveAndParseShake() {
        String bufferShake = bufferedReceive();
        ArrayList<Shake> shake = new ArrayList<>();
        for (String c : bufferShake.split("\\n")) {
            shake.add(Shake.parseString(c));
        }
        return shake;
    }

    ArrayList<Cocktail> receiveAndParseCocktail() {
        String bufferCock = bufferedReceive();
        ArrayList<Cocktail> drink = new ArrayList<>();
        for (String c : bufferCock.split("\\n")) {
            drink.add(Cocktail.parseString(c));
        }
        System.out.println("Returno il drink");
        return drink;
    }

    void recommend_drinks(){
        out.println("9");
        String buffer = bufferedReceive();
        if (buffer.equals("NOKERR")) {
            System.err.println("Non è stato possibile prendere i recommended dal server");
        } else if(buffer.equals("Ness")) {
            System.err.println("Nessun drink è presente ancora nei recommend, quindi non è possibile effettuare i recommend");
        }else if (buffer.equals("Pochi")){
            System.err.println("Non ci sono abbastanza drinks per effettuare un recommend, acquista qualche drink prima");
        }else{
            List<Cocktail> rec = Cocktail.setRecommendedCocktails(buffer);
            System.out.println("I drink raccomandati sono: ");
            for(Cocktail s : rec){
                System.out.println(s.toString());
            }
        }
    }

    void recommend_shakes(){
        out.println("10");
        String buffer = bufferedReceive();
        if (buffer.equals("NOKERR")) {
            System.err.println("Non è stato possibile prendere i recommended dal server");
        } else if(buffer.equals("Ness")) {
            System.err.println("Nessun drink è presente ancora nei recommend, quindi non è possibile effettuare i recommend");
        }else if (buffer.equals("Pochi")){
            System.err.println("Non ci sono abbastanza drinks per effettuare un recommend, acquista qualche drink prima");
        }else{
            List<Shake> rec = Shake.setRecommendedShakes(buffer);
            System.out.println("Gli shakes raccomandati sono: ");
            for(Shake s : rec){
                System.out.println(s.toString());
            }
        }
    }

    void deleteShakeNCocktails() {
        try {
            List<String> cocktails = new ArrayList<>();
            List<String> shakes = new ArrayList<>();

            if (c.getCocktails().isEmpty() && c.getShakes().isEmpty()) {
                System.out.println("Non ci sono n'è cocktail n'è shake nel carrello, acquisto non possibile");
                return;
            } else {
                if (!c.getCocktails().isEmpty()) {
                    for (Cocktail a : c.getCocktails()) {
                        cocktails.add("1`" + a.getNome() + "`" + a.getQuantita());
                    }
                }

                if (!c.getShakes().isEmpty()) {
                    for (Shake b : c.getShakes()) {
                        shakes.add("2`" + b.getNome() + "`" + b.getQuantita());
                    }
                }
            }

            out.println("8");
            for (String c : cocktails) {
                out.println(c);
                Thread.sleep(500);
            }
            for (String c : shakes) {
                out.println(c);
                Thread.sleep(500);
            }
            Thread.sleep(500);
            out.println("Fine");
            System.out.println("Cancellazione in corso...");
            clientSocket.setSoTimeout(3000);
            String line;
            try {
                line = input.readLine();
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout durante l'attesa della risposta");
                return;
            }
            while (!line.equals("Fine")) {
                try {
                    line = input.readLine();
                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout durante l'attesa della risposta");
                    return;
                }
            }
            System.out.println("Cancellazione completata di :");

            System.out.println("Cocktails: " + cocktails.toString());
            System.out.println("Shakes: " + shakes.toString());
            c.emptyCarrello();
            Thread.sleep(1000);
        } catch (IOException e) {
            System.err.println("IOException durante la lettura: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("InterruptedException durante la sospensione del thread: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore durante la cancellazione dei drink e dei frullati: " + e.getMessage());
        }
    }

    void chooseCocktail() {
        try {
            out.println("3");// 1
            Thread.sleep(500);
            ArrayList<Cocktail> cock = receiveAndParseCocktail();
            System.out.println("Scegli un cocktail:");
            int i = 0;
            for (i = 0; i < cock.size(); i++) {
                System.out.println(i + ")" + cock.get(i).getNome());
            }
            int indiceCocktail;
            do {
                indiceCocktail = scanner.nextInt();
                if (indiceCocktail < 0 || indiceCocktail >= cock.size()) {
                    System.out.println("Indice non valido, reinserisci:");
                    for (i = 0; i < cock.size(); i++) {
                        System.out.println(i + ")" + cock.get(i).getNome());
                    }
                }
            } while (indiceCocktail < 0 || indiceCocktail >= cock.size());

            System.out.println("Il cocktail scelto è " + cock.get(indiceCocktail).getNome());

            boolean valid = false;
            while (!valid) {
                System.out.println("Quanti cocktail vuoi aggiungere?");
                int q = scanner.nextInt();
                if (q < 0) {
                    System.out.println("I cocktails non possono essere minore di 0");
                } else if (q > cock.get(indiceCocktail).getQuantita()) {
                    System.out.println("La quantita' inserita' e' maggiore della quantita' disponibile del cocktail");
                } else {
                    c.addCocktail(cock.get(indiceCocktail), q);
                    valid = true;
                }
            }
        } catch (Exception e) {
            System.err.println("IOException durante la lettura: " + e.getMessage());
        }
    }

    void chooseShake() {
        try {
            out.println("4");
            ArrayList<Shake> shake = receiveAndParseShake();
            System.out.println("Scegli un frullato:");
            int i = 0;
            for (i = 0; i < shake.size(); i++) {
                System.out.println(i + ")" + shake.get(i).getNome());
            }
            int indiceShake;
            do {
                indiceShake = scanner.nextInt();
                if (indiceShake < 0 || indiceShake >= shake.size()) {
                    System.out.println("Indice non valido, reinserisci:");
                    for (i = 0; i < shake.size(); i++) {
                        System.out.println(i + ")" + shake.get(i).getNome());
                    }
                }
            } while (indiceShake < 0 || indiceShake >= shake.size());

            System.out.println("Lo shake scelto è " + shake.get(indiceShake).getNome());

            boolean valid = false;
            while (!valid) {
                System.out.println("Quanti shake vuoi aggiungere?");
                int q = scanner.nextInt();
                if (q < 0) {
                    System.out.println("Gli shakes non possono essere minore di 0");
                } else if (q > shake.get(indiceShake).getQuantita()) {
                    System.out.println("La quantita' inserita' e' maggiore della quantita' disponibile dello shake");
                } else {
                    c.addShake(shake.get(indiceShake), q);
                    valid = true;
                }
            }

        } catch (Exception e) {
            System.err.println("IOException durante la lettura: " + e.getMessage());
        }
    }

    String menu() throws SocketException {
        String risposta = "";
        String rispostaServer = "";

        try {
            clientSocket.setSoTimeout(5000);

            System.out.println("Scegli l'operazione che vuoi eseguire:");
            System.out.println("1)Registrazione");
            System.out.println("2)Login");
            System.out.println("3)Visualizza i Drink");
            System.out.println("4)Log-Out");
            System.out.println("5)Esci dall'applicativo");
            risposta = scanner.nextLine();
            if (!risposta.isEmpty()) {
                // Interpret server response
                switch (Integer.parseInt(risposta)) {
                    case 1:
                        registration();
                        rispostaServer = receive();
                        parseRispostaRegistrazione(rispostaServer);
                        break;
                    case 2:
                        login();
                        rispostaServer = receive();
                        if (rispostaServer.equals("OK")) {
                            return "OK";
                        }
                        break;
                    case 3:
                        visualizzaDrink();
                        String cock = bufferedReceive();
                        ArrayList<Cocktail> drink = new ArrayList<>();
                        for (String c : cock.split("\\n")) {
                            drink.add(Cocktail.parseString(c));
                        }
                        // System.out.println("Arraylist:" + drink);
                        break;
                    case 4:
                        System.out.println("L'utente non è loggato, quindi non può disconnettersi");
                        break;
                    case 5:// Uscita dall'applicativo
                        return "";
                    default:
                        System.out.println("Numero non valido");
                        break;
                }
            } else {
                return risposta;
            }
        } catch (IOException e) {
            System.err.println("Errore durante l'invio del comando: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Comando non valido: " + e.getMessage());
        }
        return risposta;
    }

    void registration() {
        try {
            String email;
            do {
                System.out.print("Inserisci la tua email: ");
                email = scanner.nextLine();
            } while (checkEmailRegex(email) == false);

            System.out.print("Inserisci la tua password: ");
            String password = scanner.nextLine();

            // hashing della password
            password = hash(password);
            if (password.isEmpty()) {
                throw new Exception("Errore durante l'hashing della password");
            }

            String dati = "2`" + email + "`" + password;
            System.out.println("I dati della registrazione che stanno per essere inviati sono: " + dati);
            out.println(dati);
            System.out.println("Invio riuscito");
        } catch (Exception e) {
            System.err.println("Errore durante l'invio dei dati: " + e.getMessage());
        }
    }

    void login() {
        try {
            String email;
            do {
                System.out.print("Inserisci la tua email: ");
                email = scanner.nextLine();
            } while (checkEmailRegex(email) == false);

            System.out.print("Inserisci la tua password: ");
            String password = scanner.nextLine();

            // hashing della password
            password = hash(password);
            if (password.isEmpty()) {
                throw new Exception("Errore durante l'hashing della password");
            }

            System.out.println("Hashed Password : " + password);

            String dati = "1`" + email + "`" + password;
            System.out.println("I dati del login che stanno per essere inviati sono: " + dati);
            out.println(dati);
            System.out.println("Invio riuscito");
        } catch (NoSuchElementException ex) {
            System.err.println("Mancano dei dati per il login");
        } catch (IllegalStateException e) {
            System.err.println("Scanner in stato inconsistente");
        } catch (Exception e) {
            System.err.println("Errore sconosciuto durante l'invio dei dati per il login: " + e.getMessage());
        }

    }

    boolean checkEmailRegex(String email) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            System.out.println("Email valida per la regex");
            return true;
        } else {
            System.out.println("Email non valida per la regex");
            return false;
        }
    }

    void parseRispostaRegistrazione(String risposta) {
        switch (risposta) {
            case "NOK_Already":
                System.out.println("Registrazione fallita, Utente già registrato");
                break;
            case "OK":
                System.out.println("Registrazione riuscita con successo");
                break;
            case "NOK_Registration":
                System.out.println("Errore di registrazione causato dal Server");
                break;
            case "NOK_Unknown":
                System.out.println("Errore Generico");
                break;
            default:
                System.out.println("Risposta dal server non valida");
        }
    }

    void visualizzaShakes() {
        try {
            String dati = "4";
            System.out.println("I dati della visualizzazione che stanno per essere inviati sono: " + dati);
            out.println(dati);
            System.out.println("Invio riuscito");
        } catch (Exception e) {
            System.err.println("Errore nell'invio dei dati per la visualizzazione" + e.getMessage());
        }
    }

    void visualizzaDrink() {
        try {
            String dati = "3";
            System.out.println("I dati della visualizzazione che stanno per essere inviati sono: " + dati);
            out.println(dati);
            System.out.println("Invio riuscito");
        } catch (Exception e) {
            System.err.println("Errore nell'invio dei dati per la visualizzazione" + e.getMessage());
        }
    }

    public boolean createConnection() {
        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(address, port), 5000);
            System.out.println("Connected");
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            return true;
        } catch (Exception e) {
            System.err.println("Errore durante la creazione della connessione: " + e.getMessage());
            return false;
        }
    }

    // funzione non piu usata
    public String sendData() throws IOException, InterruptedException {
        try {
            // Send registration data using clientSocket instance
            String email = "alexciacciarella@gmail.com";
            String password = "alex";
            String dati = "1`" + email + "`" + password + "`";
            out.println(dati);

            clientSocket.setSoTimeout(5000);

            // Read server response
            String risposta = input.readLine();
            System.out.println("La risposta è: " + risposta);
            return risposta;
        } catch (IOException e) {
            // Check for timeout exception specifically
            if (e instanceof SocketTimeoutException) {
                System.err.println("Timeout durante la lettura dalla socket");
            } else {
                System.err.println("Errore durante la lettura: " + e.getMessage());
            }
            return "";
        }
    }

    public void closeConnection() {
        try {
            isLogged = false;
            out.close();
            input.close();
            clientSocket.close();
        } catch (Exception e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }

    public String hash(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        String myHash = sb.toString().toUpperCase();
        System.out.println("La password hashata è: " + myHash);
        return myHash;
    }
}
