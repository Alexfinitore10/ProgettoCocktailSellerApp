package com.example.cocktailapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


import androidx.core.app.NotificationCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientService extends Service {
    private Client client;
    private final IBinder binder = new LocalBinder();
    private ExecutorService executor;

    public class LocalBinder extends Binder {
        ClientService getService() {
            return ClientService.this;
        }
    }


    public ClientService() {
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            client = Client.getIstance();
        });

        CharSequence name = "Canale del Servizio Client";
        String description = "Canale per il Servizio del Client";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("Client CocktailSellerApp", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        // Crea una notifica
        Notification notification = new NotificationCompat.Builder(this, "Client CocktailSellerApp")
                .setContentTitle("Client Service")
                .setContentText("Il servizio Client è in esecuzione.")
                .setSmallIcon(R.drawable.cocktail_app_icon)
                .build();
    
        // Avvia il servizio in primo piano
        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}