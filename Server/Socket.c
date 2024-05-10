/* #include "Socket.h"
#include "Database.h"
#include "log.h"
#include <stdio.h>
#include <string.h>

#define PORT 5978            // Porta del server
#define MAX_BUFFER_SIZE 1024 // Dimensione massima del buffer
#define IP "127.0.0.1"       // Indirizzo IP del server

void startSocket() {
  int sockfd, new_sockfd;                      // Descrittori del socket
  struct sockaddr_in server_addr, client_addr; // Strutture per gli indirizzi
  socklen_t
      client_addr_len; // Dimensione della struttura dell'indirizzo del client
  char buffer[MAX_BUFFER_SIZE] = {0}; // Buffer per i dati
  pthread_t tid;

  // Creazione del socket
  sockfd = socket(AF_INET, SOCK_STREAM, 0);
  if (sockfd < 0) {
    log_error("socket");
    exit(1);
  }

  if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int)) <
      0) { // Permette di riutilizzare la porta subito dopo aver chiuso il
           // server
    log_error("Impossibile impostare le opzioni della socket");
    exit(1);
  }

  // Impostazione dell'indirizzo del server
  server_addr.sin_family = AF_INET;
  server_addr.sin_port = htons(PORT);
  server_addr.sin_addr.s_addr = inet_addr(IP); // Ascolto su tutte le interfacce

  // Bind del socket all'indirizzo
  if (bind(sockfd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
    log_error("bind");
    exit(1);
  }

  // Ascolto per le connessioni
  if (listen(sockfd, 5) < 0) {
    log_error("listen");
    exit(1);
  }

  log_info("Server in ascolto sulla porta %d\n", PORT);

  while (1) {
    // Accettazione di una connessione
    client_addr_len = sizeof(client_addr);
    new_sockfd = accept(sockfd, (struct sockaddr *)&client_addr,
                        &client_addr_len); // si ferma qua
    if (new_sockfd < 0) {
      log_error("accept");
      continue; // Riprova ad accettare una nuova connessione
    }
    // THREADS
    log_info("Connessione accettata da: %d\n", new_sockfd);
    int error;
    if ((error = pthread_create(&tid, NULL, receiveData, &new_sockfd)) != 0) {
      log_error("Errore nella creazione del thread: %s\n", strerror(error));
      close(new_sockfd);
      continue;
    }
    log_debug("Thread creato con indirizzo: %d\n", tid);

    if ((error = pthread_detach(tid)) != 0) {
      log_error("Errore nel detach del thread: %s\n", strerror(error));
    }
  }

  close(new_sockfd);

  // Chiusura del socket server (non necessario in un ciclo infinito)
  close(sockfd);
}

void *receiveData(void *client_fd) {
  log_debug("Thread Creato\n");
  char buffer[MAX_BUFFER_SIZE] = {0};
  int res;

  int client_socket = *((int *)client_fd);
  do {
    log_trace("Aspetto operazione dal client---\n");

    res = recv(client_socket, buffer, MAX_BUFFER_SIZE, 0);
    log_debug("Il client %d ha inviato un buffer da: %d bytes quindi...\n",
              client_socket, res);
    if (res == -1) {
      log_error("%d", res);
    } else if (res == 0) {
      log_info("Il client ha chiuso la connessione\n");
      break;
    } else {
      log_debug("Dati ricevuti: %s\n", buffer);
      // buffer[strcspn(buffer, "\n")] = '\0';
      if (strlen(buffer) != 0) { // check carattere vuoto in stringa da client
        parseCommand(buffer, client_socket);
        bzero(buffer, sizeof(buffer));
      } else {
        log_debug("Il client Non hai inserito nulla\n");
      }
      // bzero(buffer, sizeof(buffer));
    }
  } while (res != 0);

  log_trace("Fine operazione ricevuta dal client---\n");
  close(client_socket);

  log_debug("Thread Terminato\n");
}

void parseCommand(char toParse[], int client_fd) {
  int commandNumber;

  char *token;

  token = strtok(toParse, "`");

  commandNumber = atoi(token);

  log_debug("Il comando è : %d\n", commandNumber);

  switch (commandNumber) {
  case 1: {
    log_debug("Il cliente vuole entrare\n");
    char email[50] = {0};
    char password[50] = {0};
    token = strtok(NULL, "`");
    log_debug("1");
    strcpy(email, token);
    token = strtok(NULL, "`");
    log_debug("2");
    strcpy(password, token);
    if (signin(email, password)) {
      log_info("[Server] Login andato a buon fine\n");
      char risposta[] = "OK\n";
      // int status = write(client_fd, risposta, strlen(risposta));
      int status = send(client_fd, risposta, strlen(risposta), 0);
      if (status == -1) {
        log_error("send error");
      } else {
        log_debug("Risposta inviata al client: %s\n", risposta);
        log_debug("bytes inviati: %d su bytes totali: %d\n", status,
                  strlen(risposta));
      }
      // sendAll(client_fd, "OK");
    } else {
      log_error("Login fallito\n");
      int status = send(client_fd, "NOK_Login\n", strlen("NOK_Login\n"), 0);
      if (status == -1) {
        log_error("send error");
      } else {
        log_debug("Risposta inviata al client: %s\n", "NOK_Login\n");
        log_debug("bytes inviati: %d su bytes totali: %d\n", status,
                  strlen("NOK_Login\n"));
      }
    }
    break;
  }
  case 2: {
    log_info("Il cliente vuole registrarsi\n");
    char email[50] = {0};
    char password[50] = {0};
    int status;
    token = strtok(NULL, "`");
    log_debug("3");
    strcpy(email, token);
    token = strtok(NULL, "`");
    log_debug("4");
    strcpy(password, token);
    char reg_status = signup(email, password);
    switch (reg_status) {
    case 'A':
      status = send(client_fd, "NOK_Already\n", 15, 0);
      if (status > 0) {
        log_debug("Risposta inviata al client: %s\n", "NOK_Already");
      } else {
        log_error("send error: %s", strerror(errno));
      }
      break;
    case 'T':
      status = send(client_fd, "OK\n", strlen("OK\n"), 0);
      if (status > 0) {
        log_debug("Risposta inviata al client: %s\n", "OK\n");
      } else {
        log_error("send error: %s", strerror(errno));
      }
      break;
    case 'F':
      status = send(client_fd, "NOK_Registration\n", 18, 0);
      if (status > 0) {
        log_debug("Risposta inviata al client: %s\n", "NOK_Registration\n");
      } else {
        log_error("send error: %s", strerror(errno));
      }
    default:
      status = send(client_fd, "NOK_Unknown\n", 18, 0);
      if (status > 0) {
        log_debug("Risposta inviata al client: %s\n", "NOK_Registration");
      } else {
        log_error("send error: %s", strerror(errno));
      }
    }

  } break;
  case 3: {
    log_info("Il cliente vuole vedere tutti i drink\n");

    char *cocktails = get_all_cocktails();

    log_debug("Drink presi dal database");

    size_t length = strlen(cocktails);

    char *charArray = (char *)malloc((length + 2) * sizeof(char));

    // ma chi t'aa chiesto
    for (int i = 0; i < length; i++) {
      charArray[i] = cocktails[i];
      if (cocktails[i] == '\n') {
        charArray[i] = '`';
      }
    }

    charArray[length + 1] = '\n';

    log_debug("L'array in characters è : %s", charArray);

    // log_info("Ecco tutti i cocktail:%s\n", cocktails);//solo per testing
    log_debug("Stringa di lunghezza: %d", length);

    int status = send(client_fd, charArray, strlen(cocktails), 0);

    if (status > 0) {
      log_info("[Server] Dati dei Cocktail inviati al client\n");
    } else {
      log_error("send error: %s", strerror(errno));
    }

    log_debug("Stringa inviata e puntatore liberato");

    free(cocktails);

    break;
  }
  case 4: {
    log_info("Il cliente vuole vedere tutti i frullati\n");

    char *shakes = get_all_shakes();

    log_debug("%s\n", shakes);

    // sendAll(client_fd, shakes);

    free(shakes);

    break;
  }
  case 5: {
    log_info("Il cliente vuole aggiungere al carrello\n");

    break;
  }
  case 6: {
    log_info("Il cliente vuole vedere il carrello\n");
    break;
  }
  case 7: {
    log_info("Il cliente vuole eliminare dal carrello\n");
    break;
  }
  case 8: {
    log_info("Il cliente vuole confermare l'acquisto\n");
    break;
  }
  default: {
    log_warn("Comando non riconosciuto\n");
    break;
  }
  }
}
 */

#include "Database.h"
#include "Socket.h"
#include "dictionary.h"
#include "log.h"
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>

#define PORT 5978            // Porta del server
#define MAX_BUFFER_SIZE 1024 // Dimensione massima del buffer
#define IP "127.0.0.1"       // Indirizzo IP del server

Dictionary *dict;

void startSocket() {
  dict = create_dictionary();
  int sockfd, new_sockfd;                      // Descrittori del socket
  struct sockaddr_in server_addr, client_addr; // Strutture per gli indirizzi
  socklen_t
      client_addr_len; // Dimensione della struttura dell'indirizzo del client
  char buffer[MAX_BUFFER_SIZE] = {0}; // Buffer per i dati
  pthread_t tid;

  // Creazione del socket
  sockfd = socket(AF_INET, SOCK_STREAM, 0);
  if (sockfd < 0) {
    log_error("socket");
    exit(1);
  }

  if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int)) <
      0) { // Permette di riutilizzare la porta subito dopo aver chiuso il
           // server
    log_error("Impossibile impostare le opzioni della socket");
    exit(1);
  }

  // Impostazione dell'indirizzo del server
  server_addr.sin_family = AF_INET;
  server_addr.sin_port = htons(PORT);
  server_addr.sin_addr.s_addr = inet_addr(IP); // Ascolto su tutte le interfacce

  // Bind del socket all'indirizzo
  if (bind(sockfd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
    log_error("bind");
    exit(1);
  }

  // Ascolto per le connessioni
  if (listen(sockfd, 5) < 0) {
    log_error("listen");
    exit(1);
  }

  log_info("Server in ascolto sulla porta %d\n", PORT);

  while (1) {
    // Accettazione di una connessione
    client_addr_len = sizeof(client_addr);
    new_sockfd = accept(sockfd, (struct sockaddr *)&client_addr,
                        &client_addr_len); // si ferma qua
    if (new_sockfd < 0) {
      log_error("accept");
      continue; // Riprova ad accettare una nuova connessione
    }
    // THREADS
    log_info("Connessione accettata da: %d\n", new_sockfd);
    int error;
    if ((error = pthread_create(&tid, NULL, receiveData, &new_sockfd)) != 0) {
      log_error("Errore nella creazione del thread: %s\n", strerror(error));
      close(new_sockfd);
      continue;
    }
    log_debug("Thread creato con indirizzo: %d\n", tid);

    if ((error = pthread_detach(tid)) != 0) {
      log_error("Errore nel detach del thread: %s\n", strerror(error));
    }
  }

  close(new_sockfd);

  // Chiusura del socket server (non necessario in un ciclo infinito)
  close(sockfd);
}

void *receiveData(void *client_fd) {
  log_debug("Thread Creato\n");
  char buffer[MAX_BUFFER_SIZE] = {0};
  int res;

  int client_socket = *((int *)client_fd);
  do {
    log_trace("Aspetto operazione dal client---\n");

    res = recv(client_socket, buffer, MAX_BUFFER_SIZE, 0);

    log_debug("Il client %d ha inviato un buffer da: %d bytes quindi...\n",
              client_socket, res);
    if (res == -1) {
      log_error("%d", res);
    } else if (res == 0) { // TODO: implement client disconnection

      log_info("Il client numero: %d ha chiuso la connessione", client_socket);
      log_debug("L'utente che si è disconesso ha questa mail: %s",
                search_dictionary(dict, client_socket));

      logoff(search_dictionary(dict, client_socket));
      remove_dictionary(dict, client_socket);
      break;
    } else {
      log_debug("Dati ricevuti: %s\n", buffer);
      // buffer[strcspn(buffer, "\n")] = '\0';
      if (strlen(buffer) != 0) { // check carattere vuoto in stringa da client
        parseCommand(buffer, client_socket);
        bzero(buffer, sizeof(buffer));
      } else {
        log_debug("Il client Non ha inserito nulla\n");
      }
      // bzero(buffer, sizeof(buffer));
    }
  } while (res != 0);

  log_trace("Fine operazione ricevuta dal client---\n");
  close(client_socket);

  log_debug("Thread Terminato\n");
}

void parseCommand(char toParse[], const int client_fd) {
  int commandNumber;

  char *token;

  token = strtok(toParse, "`");

  commandNumber = atoi(token);

  log_debug("Il comando è : %d\n", commandNumber);

  switch (commandNumber) {
  case 1:
    handle_signin(client_fd, strtok(NULL, "`"), strtok(NULL, "`"));
    break;
  case 2:
    handle_signup(client_fd, strtok(NULL, "`"), strtok(NULL, "`"));
    break;
  case 3:
    handle_get_all_cocktails(client_fd);
    break;
  case 4:
    handle_get_all_shakes(client_fd);
    break;
  case 5: {
    log_info("Il cliente vuole aggiungere al carrello\n");
    break;
  }
  case 6: {
    log_info("Il cliente vuole disconnettersi\n");
    logoff(search_dictionary(dict, client_fd));
    remove_dictionary(dict, client_fd);
    send(client_fd, "5\n", sizeof("5\n"), 0);
    break;
  }
  case 7: {
    log_info("Il cliente vuole eliminare dal carrello\n");
    break;
  }
  case 8: {
    log_info("Il cliente vuole confermare l'acquisto\n");
    break;
  }
  case 9: {

    break;
  }
  default:
    log_warn("Comando non riconosciuto\n");
    break;
  }
}

// Implementations for each function

void disconnessione(char *buffer) {}

void handle_signin(int client_fd, char *password, char *email) {
  if (signin(email, password)) {
    log_info("[Server] Login andato a buon fine\n");
    insert_dictionary(dict, client_fd, email);
    char risposta[] = "OK\n";
    int status = send(client_fd, risposta, strlen(risposta), 0);
    if (status == -1) {
      log_error("send error");
    } else {
      log_debug("Risposta inviata al client: %s\n", risposta);
      log_debug("bytes inviati: %d su bytes totali: %d\n", status,
                strlen(risposta));
    }
  } else {
    log_error("Login fallito\n");
    int status = send(client_fd, "NOK_Login\n", strlen("NOK_Login\n"), 0);
    if (status == -1) {
      log_error("send error");
    } else {
      log_debug("Risposta inviata al client: %s\n", "NOK_Login\n");
      log_debug("bytes inviati: %d su bytes totali: %d\n", status,
                strlen("NOK_Login\n"));
    }
  }
}

void handle_signup(int client_fd, char *password, char *email) {
  log_debug("Email: {%s}, Password: {%s}\n", email, password);
  char reg_status = signup(email, password);
  int status;
  switch (reg_status) {
  case 'A':
    status = send(client_fd, "NOK_Already\n", 15, 0);
    break;
  case 'T':
    status = send(client_fd, "OK\n", strlen("OK\n"), 0);
    break;
  case 'F':
    status = send(client_fd, "NOK_Registration\n", 18, 0);
    break;
  default:
    status = send(client_fd, "NOK_Unknown\n", 18, 0);
  }
  if (status > 0) {
    log_debug("Risposta inviata al client");
  } else {
    log_error("send error: %s", strerror(errno));
  }
}

void handle_get_all_cocktails(int client_fd) {
  char *cocktails = get_all_cocktails();

  size_t length = strlen(cocktails);

  char *charArray = (char *)malloc((length + 2) * sizeof(char));

  // ma chi t'aa chiesto
  for (int i = 0; i < length; i++) {
    charArray[i] = cocktails[i];
  }

  charArray[length + 1] = '\n';

  log_debug("L'array in characters è : %s", charArray);

  // log_info("Ecco tutti i cocktail:%s\n", cocktails);//solo per testing
  log_debug("Stringa di lunghezza: %d", length);

  int status = send(client_fd, charArray, strlen(cocktails), 0);

  if (status > 0) {
    log_info("[Server] Dati dei Cocktail inviati al client\n");
  } else {
    log_error("send error: %s", strerror(errno));
  }

  log_debug("Stringa inviata e puntatore liberato");

  free(cocktails);
}

void handle_get_all_shakes(int client_fd) {
  log_info("Il cliente vuole vedere tutti i frullati\n");

  char *shakes = get_all_shakes();

  log_debug("%s\n", shakes);

  // sendAll(client_fd, shakes);

  free(shakes);
}