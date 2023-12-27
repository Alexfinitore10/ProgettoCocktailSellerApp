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
            writer.println("3`");

            // Legge la risposta dal server
            //String response = reader.readLine();
            //System.out.println("Risposta dal server: " + response);

            String response = receiveAll(input);
            response = response.substring(0, response.length()-1);
            System.out.println(response);

            // Chiude la connessione con il server
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String receiveAll(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder response = new StringBuilder();
        char[] buffer = new char[512];
        int bytesRead,totalBytesRead = 0;

        while ((bytesRead = reader.read(buffer)) != -1) {
            totalBytesRead += bytesRead;
            response.append(buffer, 0, bytesRead);
            buffer = new char[512];
            if(response.toString().charAt(totalBytesRead-1) == '`'){
                break;
            }
        }

        return response.toString();
    }
}