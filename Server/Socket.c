#include "Socket.h"

struct sockaddr_in server_addr, client_addr;
const char* ip = "127.0.0.1";
char buffer[1024];
socklen_t addr_size;
pthread_t tid;

void startSocket(){

    int socket_fd, client_fd;

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(8080);
    server_addr.sin_addr.s_addr = inet_addr(ip);

    socket_fd = socket(AF_INET, SOCK_STREAM, 0);

    if(setsockopt(socket_fd, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int)) < 0){  //Permette di riutilizzare la porta subito dopo aver chiuso il server
        perror("Impossibile impostare le opzioni della socket");
        exit(1);
    }
    if (socket_fd == -1) {
        perror("Impossibile creare la socket");
        exit(1);
    }

    if (bind(socket_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Impossibile associare la socket alla porta");
        exit(1);
    }

    if (listen(socket_fd, 2) != 0) {
        printf("Il server non e in ascolto");
    }else printf("Listening...");

    while(1){
        Accept(client_fd, socket_fd);
    }
    closeConnection(socket_fd,client_fd);
}

void Accept(int client_fd, int socket_fd){
    addr_size = sizeof(client_addr);
    client_fd = accept(socket_fd, (struct sockaddr*)&client_addr, &addr_size);
    int error = 0;
    if(client_fd == -1){
        printf("Il server non e riuscito ad accettare la connessione con il client il cui indirizzo e' %s", inet_ntoa(client_addr.sin_addr));
    }else printf("Connessione accettata dal client con indirizzo: %s ...\n", inet_ntoa(client_addr.sin_addr));

    //Casto il client_fd in intptr_t (che è meglio di farsi una variabile allocata dinamicamente) e poi in void* per poterlo passare alla funzione receiveData
    
    if((error = pthread_create(&tid, NULL, receiveData, (void *) (intptr_t) client_fd)) != 0){  //Non utilizzare perror per stampare gli errori coi thread ma prendere il valore di ritorno della funzione e usare strerror
        printf("Errore nella creazione del thread: %s\n", strerror(error));
    }
    
    if((error = pthread_detach(tid)) != 0){  //Uso la detach così da non dover aspettare che il thread termini per poterlo chiudere
        printf("Errore nel detach del thread: %s\n", strerror(error));
    }
    
}

void * receiveData(void* client_fd_ptr){
    int client_fd = (intptr_t) client_fd_ptr; //Casto il void* in intptr_t e poi in int per poterlo utilizzare

    while(1){  //Condizione del while provvisoria: da rivedere
        int n = recv(client_fd, buffer, 1024, 0);
        if(n == 0){
            printf("Il client ha chiuso la connessione\n");
            break;
        }else if(n == -1){
            printf("Errore nella ricezione dei dati\n");
            break;
        }else{
            printf("Dati ricevuti: %s\n", buffer);
            send(client_fd, buffer, n, 0);
        }
    }
    
    pthread_exit(NULL);
}

void closeConnection(int socket_fd, int client_fd){
    close(socket_fd);
    close(client_fd);
}


