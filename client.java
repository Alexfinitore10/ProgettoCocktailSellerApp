// import java.io.*;
// import java.net.*;
// import java.util.Scanner;

// public class client {
//     //creazione socket del client
//     Socket clientSocket;
//     String address = "127.0.0.1";
//     int port = 5978;
//     Socket socket;
//     //buffer di uscita ed entrata
//     PrintWriter out;
//     BufferedReader input;
//     public static void main(String[] args) throws IOException, InterruptedException{
//             client c = new client();
//             c.createConnection();
//             String risposta = c.sendData();
//             System.out.println(risposta);

//             // Interpretazione della risposta
//             if (risposta.equals("OK")) {
//                 System.out.println("Registrazione avvenuta con successo!");
//             } else {
//                 System.out.println("Errore durante la registrazione: " + risposta);
//             }

//             c.closeConnection();

//     }//fine del main

//     public void createConnection(){
//         try {
//             clientSocket = new Socket("127.0.0.1", 5978);
//             out = new PrintWriter(clientSocket.getOutputStream(), true);
//             input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//         } catch (Exception e) {
//             // TODO: handle exception
//             System.err.println("Errore durante la creazione della connessione: " + e.getMessage());
//         }
//     }

//     public String sendData() throws IOException, InterruptedException{
//         clientSocket.setSoTimeout(5000);
//         try {
//             // Invio dei dati di registrazione
//             String email = "alexciacciarella@gmail.com";
//             String password = "alex";
//             String dati = "1`" + email + "`" + password + "`"; // Formato dati personalizzabile
//             //OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());

//             out.println(dati);
//             //Thread.sleep(5000);
//             String risposta = input.readLine();
//             System.out.println(" la risposta è : " + risposta);
//             return risposta;
//         } catch (IOException e) {
//             System.err.println("Errore durante la lettura");
//             return "";
//         }
//     }

//     public void closeConnection(){
//         try {
//             out.close();
//             input.close();
//             clientSocket.close();
//         } catch (Exception e) {
//             // TODO: handle exception
//             System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
//         }
//     }

//     public void newCreateConnection(){
//             try {
//                 socket = new Socket();
//                 socket.connect(new InetSocketAddress(address, port), 5000); //Timeout 5 sec for to avoid stuck
//                 System.out.println("Connected");

//                 Scanner scanner = new Scanner(System.in);

//                 input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                 out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//             } catch (IOException e) {
//                 // TODO Auto-generated catch block
//                 e.printStackTrace();
//             }

//     }
// }

import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.util.Scanner;

public class client {

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
            risposta = c.menu();
            if (!(risposta.isEmpty())) {
                // receive message

            } else {
                System.err.println("La stringa ricevuto è vuota. Termino il programma.");
                break;
            }
        } while (true);

        c.closeConnection();
    }

    String bufferedReceive() {
        try {
            // devo ottenere prima la lunghezza del buffer
            int bufferLength = Integer.parseInt(receive());

            // invio al server un messaggio di ack che mi ha fatto ricevere la lunghezza del
            // buffer
            out.println("ACK");

            System.out.println("[DEBUG]Lunghezza del buffer da leggere è: " + bufferLength);

            CharBuffer charBuffer = CharBuffer.allocate(bufferLength);
            // mi aspetto che il server invii il buffer di lunghezza concordata
            int ric = input.read(charBuffer);

            String risposta = new String(charBuffer.array(), 0, ric);

            System.out.println("La stringa letta dal server è : " + risposta);
            // elaborazione... però devo vede un attimo
            return risposta;
        } catch (Exception e) {
            System.err.println("Errore durante la lettura del server: " + e.getMessage());
            return e.getMessage();
        }
    }

    String receive() {
        try {
            String risposta = input.readLine();
            System.out.println("La risposta è: " + risposta);
            return risposta;
        } catch (Exception e) {
            System.err.println("Errore durante la lettura del server: " + e.getMessage());
            return e.getMessage();
        }
    }

    String menu() throws SocketException {
        String risposta;

        clientSocket.setSoTimeout(5000);

        System.out.println("Scegli l'operazione che vuoi eseguire:");
        System.out.println("1) Registrazione");
        System.out.println("2)Login");
        System.out.println("3)Visualizza i Drink");
        System.out.println("4)Disconnettiti");
        risposta = scanner.nextLine();
        if (!risposta.isEmpty()) {
            // System.out.println(risposta);
            // Interpret server response
            switch (Integer.parseInt(risposta)) {
                case 1:
                    registration();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    visualizzaDrink();
                    bufferedReceive();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Comando non riconosciuto");
            }
        } else {
            return risposta;
        }
        return risposta;

    }

    void registration() {
        try {
            System.out.print("Inserisci la tua email: ");
            String email = scanner.nextLine();
            System.out.print("Inserisci la tua password: ");
            String password = scanner.nextLine();
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
            System.out.print("Inserisci la tua email: ");
            String email = scanner.nextLine();
            System.out.print("Inserisci la tua password: ");
            String password = scanner.nextLine();
            String dati = "1`" + email + "`" + password;
            System.out.println("I dati del login che stanno per essere inviati sono: " + dati);
            out.println(dati);
            System.out.println("Invio riuscito");
        } catch (Exception e) {
            System.err.println("Errore nell'invio dei dati per il login" + e.getMessage());
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
            out.close();
            input.close();
            clientSocket.close();
        } catch (Exception e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}
