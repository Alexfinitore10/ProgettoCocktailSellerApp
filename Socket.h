#ifndef Socket_h
#define Socket_h

int socket_fd, client_fd;
struct sockaddr_in server_addr, client_addr;
const char* ip = "127.0.0.1";
char buffer[1024];
socklen_t addr_size;

void startSocket();

void Createsocket();

void Bindsocket();

void Listen();

void receiveData();

void closeConnection();

#endif
 