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
    if(client_fd != 0){
        printf("Il server non e riuscito ad accettare la connessione con il client %s", client_addr);
    }else printf("Connessione accettata...");
    pthread_create(&tid, NULL, &receiveData, client_fd);
    pthread_join(tid, NULL);
}

void* receiveData(void* client_fd){
    while(recv(client_fd, buffer, sizeof(buffer), 0) > 0){
        printf("Il client scrive: %s\n", buffer);
    }
    if(recv(client_fd, buffer, sizeof(buffer), 0) == 0){
        printf("Il client ha chiuso la connessione");
        closeConnection();   
    }else{
        printf("Errore nella ricezione del messaggio");
        closeConnection();
    }
    
    pthread_exit(NULL);
}

void closeConnection(int socket_fd, int client_fd){
    close(socket_fd);
    close(client_fd);
}


