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
void parseCommand(char toParse[], int client_fd);

// Function prototypes for each case in the switch statement
void handle_signin(int client_fd, char *email, char *password);
void handle_signup(int client_fd, char *email, char *password);
void handle_get_all_cocktails(int client_fd);
void handle_get_all_shakes(int client_fd);
//  Add function prototypes for remaining cases (5, 6, 7, 8)
