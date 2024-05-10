package com.example.cocktailapp;

import android.util.*;


import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Client {
    boolean isLogged = false;
    //Regex
    String regex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    StringBuilder stringBuilder = new StringBuilder();
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

    public int sendData(String dati){
        out.println(dati);
        Log.v("Client","Messaggio inviato");
        try {
            return clientSocket.getSendBufferSize();
        } catch (SocketException e) {
            Log.e("Client","Non riesco a capie quanti byte sono stati inviati: " + e.getMessage());
            return -1;
        }
    }

    public String receiveData() throws IOException, InterruptedException{
        String data = "";
        try{
            clientSocket.setSoTimeout(5000);
            data = input.readLine();
            return data;
        }catch (IOException e){
            if (e instanceof SocketTimeoutException) {
                Log.e("Client","Errore durante la ricezione del messaggio: " + e.getMessage());
            } else {
                Log.e("Client","Errore durante la lettura: " + e.getMessage());
            }
        }
        return data;
    }

    public String bufferedReceive() {
        try {
            CharBuffer charBuffer = CharBuffer.allocate(1024);

            int ric = input.read(charBuffer);

            String rispostadato = new String(charBuffer.array(), 0, ric);

            Log.d("Client", "La stringa letta dal server è : " + rispostadato);
            // elaborazione... però devo vede un attimo
            return rispostadato;
        } catch (Exception e) {
            Log.e("Client","Errore durante la lettura del server: " + e.getMessage());
            return e.getMessage();
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


    public void closeConnection() {
        try {
            out.close();
            input.close();
            clientSocket.close();
            Log.v("Client","Connessione chiusa con il server.");
        } catch (Exception e) {
            Log.e("Client","Loggo un errore nella chiusura del client: " +e.getMessage());
        }
    }
}
