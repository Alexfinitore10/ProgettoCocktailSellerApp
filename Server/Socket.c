 #include "Socket.h"
 #include "Database.h"

#define PORT 5978 // Porta del server
#define MAX_BUFFER_SIZE 1024 // Dimensione massima del buffer
#define IP "127.0.0.1" // Indirizzo IP del server

void startSocket() {
    int sockfd, new_sockfd; // Descrittori del socket
    struct sockaddr_in server_addr, client_addr; // Strutture per gli indirizzi
    socklen_t client_addr_len; // Dimensione della struttura dell'indirizzo del client
    char buffer[MAX_BUFFER_SIZE] = {0}; // Buffer per i dati
    pthread_t tid;

    // Creazione del socket
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        perror("socket");
        exit(1);
    }

    if(setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int)) < 0){  //Permette di riutilizzare la porta subito dopo aver chiuso il server
        perror("Impossibile impostare le opzioni della socket");
        exit(1);
    }

    // Impostazione dell'indirizzo del server
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr(IP); // Ascolto su tutte le interfacce

    // Bind del socket all'indirizzo
    if (bind(sockfd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("bind");
        exit(1);
    }

    // Ascolto per le connessioni
    if (listen(sockfd, 5) < 0) {
        perror("listen");
        exit(1);
    }

    printf("Server in ascolto sulla porta %d\n", PORT);

    while (1) {
        // Accettazione di una connessione
        client_addr_len = sizeof(client_addr);
        new_sockfd = accept(sockfd, (struct sockaddr *)&client_addr, &client_addr_len);
        if (new_sockfd < 0) {
            perror("accept");
            continue; // Riprova ad accettare una nuova connessione
        }
        //THREADS
        // printf("Connessione accettata da: %s\n", inet_ntoa(client_addr.sin_addr));
        // int error
        // if((error = pthread_create(&tid, NULL, receiveData, new_sockfd)) != 0){  
        //     printf("Errore nella creazione del thread: %s\n", strerror(error));
        // }
        
        // if((error = pthread_detach(tid)) != 0){  
        //     printf("Errore nel detach del thread: %s\n", strerror(error));
        // }
        //single threaded
        receiveData(new_sockfd);


        // Ricezione del messaggio dal client
        //int bytes_received = recv(new_sockfd, buffer, MAX_BUFFER_SIZE, 0);
        // if (bytes_received < 0) {
        //     perror("recv");
        //     close(new_sockfd);
        //     continue; // Riprova con la prossima connessione
        // }

        // Elaborazione del messaggio ricevuto
        //printf("Messaggio ricevuto dal client: %s\n", buffer);

        // Invio di una risposta al client

        

        // Chiusura della connessione client
        close(new_sockfd);
    }

    // Chiusura del socket server (non necessario in un ciclo infinito)
    close(sockfd);

}

    void receiveData(int client_fd) {
        char buffer[MAX_BUFFER_SIZE] = {0};
        int res = recv(client_fd, buffer , MAX_BUFFER_SIZE, 0);
        if(res == -1){
            printf("Errore nella ricezione dei dati\n");
        }else if(res == 0){
            printf("Il client ha chiuso la connessione\n");
        }else{
            printf("Dati ricevuti: %s\n", buffer);
            //buffer[strcspn(buffer, "\n")] = '\0';
            if (strlen(buffer) != 0){//check carattere vuoto in stringa da client
                parseCommand(buffer,client_fd);
            }else{
                printf("Il client Non hai inserito nulla\n");
            }
            //bzero(buffer, sizeof(buffer));
            memset(buffer, '\0', sizeof(buffer));
        }
    }

    void parseCommand(char toParse[],int client_fd){
        int commandNumber;

        char * token;

        token = strtok(toParse, "`");

        commandNumber = atoi(token);

        printf("Il comando è : %d", commandNumber);

        switch(commandNumber){
            case 1:{
                printf("Il cliente vuole entrare\n");
                char email[50] = {0};
                char password[50] = {0};
                token = strtok(NULL, "`");
                strcpy(email, token);
                token = strtok(NULL, "`");
                strcpy(password, token);
                if(signin(email,password))
                {
                    printf("Login andato a buon fine\n");
                    char *risposta = "OK";
                    int status = send(client_fd, risposta, strlen(risposta), 0);
                    if ((status == -1))
                    {
                        printf("send error");
                    }
                    printf("Risposta inviata al client: %s\n", risposta);
                    break;
                    //sendAll(client_fd, "OK");
                }else{printf("Login fallito\n");}
                break;
            }
            case 2:{
                printf("Il cliente vuole registrarsi\n");
                char email[50] = {0};
                char password[50] = {0};
                token = strtok(NULL, "`");
                strcpy(email, token);
                token = strtok(NULL, "`");
                strcpy(password, token);
                if(signup(email,password) == true) {
                    //stringa default di ACK
                    //sendAll(client_fd, "OK");
                }else{
                    //stringa default di NOK
                    write(client_fd, "NOK_Registration", 16);
                }
                
                
                break;
            }
            case 3:{
                printf("Il cliente vuole vedere tutti i drink\n");

                char * cocktails = get_all_cocktails();

                printf("%s\n", cocktails);

                //sendAll(client_fd, cocktails);

                free(cocktails);

                break;
            }
            case 4:{
                printf("Il cliente vuole vedere tutti i frullati\n");

                char * shakes = get_all_shakes();

                printf("%s\n", shakes);

                //sendAll(client_fd, shakes);

                free(shakes);

                break;
            }
            case 5:{
                printf("Il cliente vuole aggiungere al carrello\n");
                
                break;
            }
            case 6:{
                printf("Il cliente vuole vedere il carrello\n");
                break;
            }
            case 7:{
                printf("Il cliente vuole eliminare dal carrello\n");
                break;
            }
            case 8:{
                printf("Il cliente vuole confermare l'acquisto\n");
                break;
            }
            default:{
                printf("Comando non riconosciuto\n");
                break;
            }
        }
    }

    


// struct sockaddr_in server_addr, client_addr;
// const char* ip = "127.0.0.1";
// socklen_t addr_size;
// pthread_t tid;

// void startSocket(){ 
//     int socket_fd, client_fd;

//     socket_fd = socket(AF_INET, SOCK_STREAM, 0);
//     if (socket_fd == -1) {
//         perror("socket creation error");
//         exit(1);
//     }

//     if(setsockopt(socket_fd, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int)) < 0){  //Permette di riutilizzare la porta subito dopo aver chiuso il server
//         perror("Impossibile impostare le opzioni della socket");
//         exit(1);
//     }

//     server_addr.sin_family = AF_INET;
//     server_addr.sin_port = htons(5979);
//     server_addr.sin_addr.s_addr = inet_addr(ip);
//     printf("Socket Creata\n");



//     if (bind(socket_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
//         perror("Impossibile associare la socket alla porta");
//         exit(1);
//     }
//     printf("Bind Fatto\n");

//     if (listen(socket_fd, 5) < 0) {
//         printf("Il server non e in ascolto");
//     }else printf("Listening...\n");

//     Accept(client_fd, socket_fd);

//     closeConnection(socket_fd,client_fd);
// }

// void Accept(int client_fd, int socket_fd){
//     addr_size = sizeof(client_addr);
//     client_fd = accept(socket_fd, (struct sockaddr*)&client_addr, &addr_size);
//     if(client_fd < 0){
//         printf("Il server non e riuscito ad accettare la connessione con il client il cui indirizzo e' %s", inet_ntoa(client_addr.sin_addr));
//     }else printf("Connessione accettata dal client con indirizzo: %s ...\n", inet_ntoa(client_addr.sin_addr));

//     //Casto il client_fd in intptr_t (che è meglio di farsi una variabile allocata dinamicamente) e poi in void* per poterlo passare alla funzione receiveData

//     //senza thread
//     receiveData(client_fd);
    
//     // if((error = pthread_create(&tid, NULL, receiveData, (void *) (intptr_t) client_fd)) != 0){  
//     //     printf("Errore nella creazione del thread: %s\n", strerror(error));
//     // }
//     //Non utilizzare perror per stampare gli errori coi thread ma prendere il valore di ritorno della funzione e usare strerror
    
//     // if((error = pthread_detach(tid)) != 0){  
//     //     printf("Errore nel detach del thread: %s\n", strerror(error));
//     // }

//     //Uso la detach così da non dover aspettare che il thread termini per poterlo chiudere
    
// }

// void * receiveData(int client_fd){//void*
//     //int client_fd = (intptr_t) client_fd_ptr; //Casto il void* in intptr_t e poi in int per poterlo utilizzare
//     char buffer[1024] = {0};

//     while(1){  
//         int n = recv(client_fd, buffer, 1024, 0);
//         if(n == 0){
//             printf("Il client ha chiuso la connessione\n");
//             break;
//         }else if(n == -1){
//             printf("Errore nella ricezione dei dati\n");
//             break;
//         }else{
//             printf("Dati ricevuti: %s\n", buffer);
//             //buffer[strcspn(buffer, "\n")] = '\0';
//             if (strlen(buffer) != 0){//check carattere vuoto in stringa da client
//                 parseCommand(buffer,client_fd);
//             }else{
//                 printf("Non hai inserito nulla\n");
//             }
//             //bzero(buffer, sizeof(buffer));
//             memset(buffer, '\0', sizeof(buffer));
//         }
//     }
//     close(client_fd);
//     //pthread_exit(NULL);
// }

// void parseCommand(char *toParse, int client_fd){
//     int commandNumber;
//     char * token;

//     token = strtok(toParse, "`");

//     commandNumber = atoi(token);

//     printf("Il comando è : %d", commandNumber);

//     switch(commandNumber){
//         case 1:{
//             printf("Il cliente vuole entrare\n");
//             char email[50] = {0};
//             char password[50] = {0};

//             token = strtok(NULL, "`");

//             strcpy(email, token);

//             token = strtok(NULL, "`");

//             strcpy(password, token);

//             if(signin(email,password))
//             {
//                 printf("Login andato a buon fine\n");
//                 char *risposta = "OK";
//                 int status = send(client_fd, risposta, strlen(risposta), 0);
//                 if ((status == -1))
//                 {
//                     printf("send error");
//                 }
                
//                 //sendAll(client_fd, "OK");
//             }else{printf("Login fallito\n");}
            
            
//             break;
//         }
//         case 2:{
//             printf("Il cliente vuole registrarsi\n");
//             char email[50] = {0};
//             char password[50] = {0};

//             token = strtok(NULL, "`");

//             strcpy(email, token);

//             token = strtok(NULL, "`");

//             strcpy(password, token);

//             if(signup(email,password) == true) {
//                 //stringa default di ACK
//                 //sendAll(client_fd, "OK");
//             }else{
//                 //stringa default di NOK
//                 write(client_fd, "NOK_Registration", 16);
//             }
            
            
//             break;
//         }
//         case 3:{
//             printf("Il cliente vuole vedere tutti i drink\n");

//             char * cocktails = get_all_cocktails();

//             printf("%s\n", cocktails);

//             sendAll(client_fd, cocktails);

//             free(cocktails);

//             break;
//         }
//         case 4:{
//             printf("Il cliente vuole vedere tutti i frullati\n");

//             char * shakes = get_all_shakes();

//             printf("%s\n", shakes);

//             sendAll(client_fd, shakes);

//             free(shakes);

//             break;
//         }
//         case 5:{
//             printf("Il cliente vuole aggiungere al carrello\n");
            
//             break;
//         }
//         case 6:{
//             printf("Il cliente vuole vedere il carrello\n");
//             break;
//         }
//         case 7:{
//             printf("Il cliente vuole eliminare dal carrello\n");
//             break;
//         }
//         case 8:{
//             printf("Il cliente vuole confermare l'acquisto\n");
//             break;
//         }
//         default:{
//             printf("Comando non riconosciuto\n");
//             break;
//         }
//     }
// }

// void sendAll(int client_fd, char *str){
//     char buffer[512];
//     int str_len = strlen(str);
//     int bytes_sent = 0;

//     // Send the string in parts
//     while (bytes_sent < str_len) {
//         int bytes_to_send;
//         if (str_len - bytes_sent < 512) {
//             bytes_to_send = str_len - bytes_sent;
//         } else {
//             bytes_to_send = 512;
//         }
//         strncpy(buffer, str + bytes_sent, bytes_to_send);
//         write(client_fd, buffer, bytes_to_send);
//         bytes_sent += bytes_to_send;
//     }

//     // Send the confirmation message
//     //char *message = "dati inviati correttamente";
//     printf("\nNumero di byte inviati: %d\n", bytes_sent); 
//     //write(client_fd, message, strlen(message));
// }



// void closeConnection(int socket_fd, int client_fd){
//     close(socket_fd);
//     close(client_fd);
// }


