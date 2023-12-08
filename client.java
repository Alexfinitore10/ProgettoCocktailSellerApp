import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5978);

            // Ottiene un'istanza dell'input stream per ricevere dati dal server
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Ottiene un'istanza dell'output stream per inviare dati al server
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            
            // Invia un messaggio al server
            writer.println("0`elvino`Elvino");

            // Legge la risposta dal server
            String response = reader.readLine();
            System.out.println("Risposta dal server: " + response);

            // Chiude la connessione con il server
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}