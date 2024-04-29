import java.io.*;
import java.net.*;


public class client {
    //creazione socket del client
    Socket clientSocket;
    //buffer di uscita ed entrata
    PrintWriter out;
    BufferedReader in;
    public static void main(String[] args) throws IOException, InterruptedException{
            client c = new client();
            c.createConnection();
            String risposta = c.sendData();
            System.out.println(risposta);

            // Interpretazione della risposta
            if (risposta.equals("OK")) {
                System.out.println("Registrazione avvenuta con successo!");
            } else {
                System.out.println("Errore durante la registrazione: " + risposta);
            }

            c.closeConnection();

            
    }//fine del main

    public void createConnection(){
        try {
            clientSocket = new Socket("127.0.0.1", 5979);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Errore durante la creazione della connessione: " + e.getMessage());
        }
    }

    public String sendData() throws IOException, InterruptedException{
        clientSocket.setSoTimeout(5000);
        try {
            // Invio dei dati di registrazione
            String email = "alexciacciarella@gmail.com";
            String password = "alex";
            String dati = "1`" + email + "`" + password + "`"; // Formato dati personalizzabile
            //OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
   

            out.println(dati);
            Thread.sleep(500);
            String risposta = in.readLine();
            return risposta;
        } catch (IOException e) {
            System.err.println("Errore durante la lettura");
            return "";   
        }
    }

    public void closeConnection(){
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}