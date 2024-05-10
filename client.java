import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Cocktail {
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
}

public class client {

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
        } while (true);

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
            System.err.println("Errore durante la lettura del server: " + e.getMessage());
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

            System.out.println("1) Visualizza Drink");
            System.out.println("2) Visualizza Fruit Shakes");
            System.out.println("3) Aggiungi al carrello");
            System.out.println("4) Vedi carrello");
            System.out.println("5) Conferma Acquisto");
            System.out.println("6) Esci");

            Scanner scanner = new Scanner(System.in);
            risposta = scanner.nextLine();

            out.println(risposta);

            rispostaServer = receive();

            if (!risposta.isEmpty()) {
                switch (Integer.parseInt(rispostaServer)) {
                    case 1:
                        visualizzaDrink();
                        String bufferCock = bufferedReceive();
                        ArrayList<Cocktail> drink = new ArrayList<>();
                        for (String c : bufferCock.split("\\n")) {
                            drink.add(Cocktail.parseString(c));
                        }
                        break;
                    case 2:
                        visualizzaShakes();
                        String bufferShake = bufferedReceive();
                        ArrayList<Cocktail> shake = new ArrayList<>();
                        for (String c : bufferShake.split("\\n")) {
                            shake.add(Cocktail.parseString(c));
                        }
                        break;
                    case 3:
                        break;
                    case 6:
                        out.println("9");
                        closeConnection();
                        break;
                    default:
                        break;
                }
            }

            return rispostaServer;
        } catch (IOException e) {
            System.err.println("Errore durante la lettura: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Errore durante la lettura: " + e.getMessage());
            return null;
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
            System.out.println("5)Disconnettiti");
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
                        // decrementaDrink();
                    case 5:
                        closeConnection();
                        return "";
                    default:
                        System.out.println("Numero non valido");
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

    // String menuLoggato() {
    // System.out.println("Scegli l'operazione che vuoi eseguire:");
    // System.out.println("2) Login");
    // System.out.println("3)Visualizza i Drink");
    // System.out.println("4)Decrementa i Drink");
    // System.out.println("5)Logout");

    // try {
    // String risposta = receive();
    // switch (Integer.parseInt(risposta)) {
    // case 2:
    // risposta = receive();
    // if (risposta.equals("OK")) {
    // return "OK";
    // }
    // break;
    // case 3:
    // bufferedReceive();
    // break;
    // case 4:
    // // decrementaDrink();
    // case 5:
    // closeConnection();
    // break;
    // default:
    // System.out.println("Numero non valido");
    // }
    // } catch (IOException e) {
    // System.err.println("Errore durante l'invio del comando: " + e.getMessage());
    // } catch (NumberFormatException e) {
    // System.err.println("Comando non valido: " + e.getMessage());
    // }
    // }

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
