#include "includes.h"

int socket_fd;
struct sockaddr_in server_addr;
const char* ip = "127.0.0.1";

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
    server_addr.sin_addr.s_addr = inet_addr(const char *ip);
    if (bind(socket_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Impossibile associare la socket alla porta");
        exit(1);
    }
}
