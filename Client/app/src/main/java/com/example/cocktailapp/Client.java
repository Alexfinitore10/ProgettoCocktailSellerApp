package com.example.cocktailapp;

import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;


public class Client {
    private static Client istanza;
    // Socket for client communication
    private Socket clientSocket;
    private String address = "192.168.1.179";
    private int port = 5978;

    // Buffer for input and output streams
    private PrintWriter out;
    private BufferedReader input;
    private boolean connected = false;
    private int numTries = 0;

    private Client() throws InterruptedException {
        do {
            connected = this.createConnection();
            numTries++;
            if(!connected){
                Log.e("Client","Impossibile stabilire la connessione con il server. Prossimo tentativo tra 5 secondi... (" + numTries + "/10)");
                TimeUnit.MILLISECONDS.sleep(5000);
            }
        } while(!connected && numTries < 10);
        if(!connected){
            Log.e("Client","Impossibile stabilire la connessione con il server. Chiusura programma...");
            this.closeConnection();
        }else{
            Log.v("Client","Connessione stabilita con il server.");
        }
    }

    public static Client getIstanza() {
        if(istanza == null){
            try {
                istanza = new Client();
            } catch (InterruptedException e) {
                Log.e("Client","Errore durante la creazione dell'istanza del client: " + e.getMessage());
            }
        }
        return istanza;
    }





    private boolean createConnection() {
        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(address,port), 5000);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            return true;
        } catch (Exception e) {
            Log.e("Client","Errore durante la creazione della connessione: " + e.getMessage());
            return false;
        }
    }

    public int sendData(String dati) throws IOException, InterruptedException{
        try {
            out.println(dati);
            Log.v("Client","Messaggio inviato");
            clientSocket.setSoTimeout(5000);
            return clientSocket.getSendBufferSize();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                Log.e("Client","Timeout durante la lettura dalla socket");
            } else {
                Log.e("Client","Errore durante la lettura: " + e.getMessage());
            }
        }
        return clientSocket.getSendBufferSize();
    }

    public int receiveData(String receiver) throws IOException, InterruptedException{
        try{
            input.readLine();
            return clientSocket.getReceiveBufferSize();
        }catch (IOException e){
            if (e instanceof SocketTimeoutException) {
                Log.e("Client","Timeout durante la lettura dalla socket");
            } else {
                Log.e("Client","Errore durante la lettura: " + e.getMessage());
            }
        }
        return clientSocket.getReceiveBufferSize();
    }


    public void closeConnection() {
        try {
            out.close();
            input.close();
            clientSocket.close();
        } catch (Exception e) {
            Log.e("Client","Loggo un errore nella chiusura del client: " +e.getMessage());
        }
    }
}
