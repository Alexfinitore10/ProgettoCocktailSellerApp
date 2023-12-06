#include <arpa/inet.h>
#include <sys/socket.h>
#include <stdio.h>
#include <stdlib.h>

int main(){
    int socket_fd, client_fd;
    struct sockaddr_in server_addr, client_addr;
    const char* ip = "127.0.0.1";

    printf("aaa\n");

    

    socket_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (socket_fd == -1) {
        perror("Impossibile creare la socket");
        exit(1);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(5978);
    server_addr.sin_addr.s_addr = inet_addr(ip);
    printf("Socket Creata");
}