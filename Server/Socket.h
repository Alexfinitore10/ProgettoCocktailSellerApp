#include "includes.h"
/*
void startSocket();

void Accept(int,int);

void* receiveData(void*);//void*

void parseCommand(char[],int);

int sendAll(int, char *);

void closeConnection(int,int);
 */

void startSocket();
void *receiveData(void *client_fd);
int parseCommand(char toParse[], const int client_fd);
void handle_client_disconnection(int client_socket);
int handle_data_received(char *buffer, const int client_socket);

// Function prototypes for each case in the switch statement
void handle_signin(int client_fd, char *email, char *password);
void handle_signup(int client_fd, char *email, char *password);
void handle_get_all_cocktails(int client_fd);
void handle_get_all_shakes(int client_fd);
void handle_remove_drink_and_shake();
void free_dic();
//  Add function prototypes for remaining cases (5, 6, 7, 8)
