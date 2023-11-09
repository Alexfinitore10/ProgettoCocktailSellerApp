#include "includes.h"
#include "Socket.h"

void startSocket(){
    Createsocket();
    Bindsocket();
    Listen();
    receiveData();
    closeConnection();
}

void Createsocket(){
    socket_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (socket_fd == -1) {
        perror("Impossibile creare la socket");
        exit(1);
    }
}

void Bindsocket(){
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(8080);
    server_addr.sin_addr.s_addr = inet_addr(ip);
    if (bind(socket_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Impossibile associare la socket alla porta");
        exit(1);
    }
}

void Listen(){
    if (listen(socket_fd, 2) != 0) {
        printf("Il server non e in ascolto");
    }else printf("Listening...");

    addr_size = sizeof(client_addr);
    client_fd = (socket_fd, (struct sockaddr*)&client_addr, &addr_size);
    if(client_fd == -1){
        printf("Il server non e riuscito ad accettare la connessione con il client %d", client_addr);
    }else printf("Connessione accettata...");
}

void receiveData(){
    recv(client_fd, buffer, sizeof(buffer), 0);
    printf("Il client scrive: %s\n", buffer);
}

void closeConnection(){
    close(socket_fd);
    close(client_fd);
}


