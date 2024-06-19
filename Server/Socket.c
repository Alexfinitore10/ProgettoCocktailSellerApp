#include "Socket.h"
#include "Database.h"
#include "dictionary.h"
#include "log.h"
#include <netinet/in.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

#define PORT 5978              // Porta del server
#define MAX_BUFFER_SIZE 1024   // Dimensione massima del buffer
#define IP_vecchio "127.0.0.1" // Indirizzo IP del server
#define IP "192.168.43.85"

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
  server_addr.sin_addr.s_addr =
      INADDR_ANY; // inet_addr(IP); // Ascolto su tutte le interfacce

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
  free_dictionary(dict);
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
      break; // se res == -1, ci siamo bloccati su recv, quindi non possiamo più
             // ricevere nulla, quindi esci dal loop
    } else if (res == 0) { // TODO: implement client disconnection
      handle_client_disconnection(client_socket); // disconnessione
      break;
    } else {
      handle_data_received(buffer, client_socket);
    }
  } while (res != 0);

  log_trace("Fine operazione ricevuta dal client---\n");
  close(client_socket);

  log_debug("Thread Terminato\n");
}

int parseCommand(char toParse[], const int client_fd) {
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
    return 0;
  }
  case 7: {
    log_info("Il cliente vuole eliminare dal carrello\n");
    break;
  }
  case 8: {
    log_info(
        "Il cliente ha effettuato un acquisto di un drink e un frullato\n");
    // TODO e a fa che chiama sia reduce shake che reduce cocktail
    // Deve arrivare una stringa formattata così: [8`Mojito`2`Frullato
    // Proteico`3`]
    handle_remove_drink_and_shake(client_fd);
    break;
  }
  case 9: {
    log_info("Recommended Drinks\n");

    handle_get_recommended_drinks(client_fd);
    break;
  }
  case 10: {
    log_info("Recommended Shakes");
    handle_get_recommended_shakes(client_fd);
    break;
  }
  default:
    log_warn("Comando non riconosciuto\n");
    break;
  }
}

int handle_data_received(char *buffer, const int client_socket) {
  log_debug("Dati ricevuti: %s\n", buffer);

  if (strlen(buffer) != 0) { // check carattere vuoto in stringa da client
    int res = parseCommand(buffer, client_socket);
    bzero(buffer, MAX_BUFFER_SIZE);
    if (res == 0) {
      return 0;
    } else {
      return 1;
    }
  } else {
    log_debug("Il client Non ha inserito nulla\n");
    return 1;
  }
}

// Implementations for each function
void handle_client_disconnection(int client_socket) {
  log_info("Il client numero: %d ha chiuso la connessione", client_socket);

  if (search_dictionary(dict, client_socket) == NULL) {
    log_info("L'utente non è loggato, quindi non può disconnettersi");
  } else {
    log_debug("L'utente che si è disconesso ha questa mail: %s",
              search_dictionary(dict, client_socket));
    logoff(search_dictionary(dict, client_socket));
    remove_dictionary(dict, client_socket);
  }
}

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
    status = send(client_fd, "NOK_Already\n", strlen("NOK_Already\n"), 0);
    break;
  case 'T':
    insert_dictionary(dict, client_fd, email);
    status = send(client_fd, "OK\n", strlen("OK\n"), 0);
    break;
  case 'F':
    status =
        send(client_fd, "NOK_Registration\n", strlen("NOK_Registration\n"), 0);
    break;
  default:
    status = send(client_fd, "NOK_Unknown\n", strlen("NOK_Unknown\n"), 0);
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

  // charArray[length + 1] = '\n';

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

void handle_remove_cocktail(char *nome, int quantita) {
  if (reduce_amount_cocktail(nome, quantita) == false) {
    log_error("C'è stato un errore nella rimozione del cocktail", nome);
  }
}

void handle_get_all_shakes(int client_fd) {
  log_info("Il cliente vuole vedere tutti i frullati\n");

  char *shakes = get_all_shakes();

  size_t length = strlen(shakes);

  log_debug("L'array in characters è : %s", shakes);

  // log_info("Ecco tutti i cocktail:%s\n", cocktails);//solo per testing
  log_debug("Stringa di lunghezza: %d", length);

  int status = send(client_fd, shakes, strlen(shakes), 0);

  if (status > 0) {
    log_info("Dati degli shakes inviati al client\n");
  } else {
    log_error("send error: %s", strerror(errno));
  }

  log_debug("Stringa inviata e puntatore liberato");

  free(shakes);
}

void handle_get_recommended_drinks(int client_fd) {
  connection_lock();
  char *risposta = get_recommended_drinks();
  connection_unlock();
  if (risposta == NULL) {
    log_error("C'è stato un errore nella raccolta dei cocktail consigliati");
    int status = send(client_fd, "NOKERR\n", strlen("NOKERR\n"), 0);
    if (status == -1) {
      log_error("send error: %s", strerror(errno));
    }
    return;
  } else if (risposta == "Ness") {
    log_error("C'è stato un errore, nessun drink è presente nella raccolta "
              "consigliati");
    int status = send(client_fd, "NOK0\n", strlen("NOK0\n"), 0);
    if (status == -1) {
      log_error("send error: %s", strerror(errno));
    }
    return;
  } else if (risposta == "Pochi") {
    log_error("C'è stato un errore, non sono presenti abbastanza drinks per "
              "effettuare il recommend");
    int status = send(client_fd, "NOK:(\n", strlen("NOK:(\n"), 0);
    if (status == -1) {
      log_error("send error: %s", strerror(errno));
    }
    return;
  }

  log_debug("Stringa: %s, di grandezza %d", risposta, strlen(risposta));

  int status = send(client_fd, risposta, strlen(risposta), 0);

  if (status > 0) {
    log_info("[Server] Dati dei cocktail consigliati inviati al client\n");
  } else {
    log_error("send error: %s", strerror(errno));
  }
}

void handle_get_recommended_shakes(int client_fd) {
  connection_lock();
  char *risposta = get_recommended_shakes();
  connection_unlock();
  if (risposta == NULL) {
    log_error("C'è stato un errore nella raccolta dei shake consigliati");
    int status = send(client_fd, "NOKERR\n", strlen("NOKERR\n"), 0);
    if (status == -1) {
      log_error("send error: %s", strerror(errno));
    }
    return;
  } else if (risposta == "Ness") {
    log_error("C'è stato un errore, nessun shake è presente nella raccolta "
              "consigliati");
    int status = send(client_fd, "NOK0\n", strlen("NOK0\n"), 0);
    if (status == -1) {
      log_error("send error: %s", strerror(errno));
    }
    return;
  } else if (risposta == "Pochi") {
    log_error("C'è stato un errore, non sono presenti abbastanza shakes per "
              "effettuare il recommend");
    int status = send(client_fd, "NOK:(\n", strlen("NOK:(\n"), 0);
    if (status == -1) {
      log_error("send error: %s", strerror(errno));
    }
    return;
  }

  log_debug("Stringa: %s, di grandezza %d", risposta, strlen(risposta));

  int status = send(client_fd, risposta, strlen(risposta), 0);

  if (status > 0) {
    log_info("[Server] Dati dei shakes consigliati inviati al client\n");
  } else {
    log_error("send error: %s", strerror(errno));
  }
}

void handle_remove_drink_and_shake(const int client_fd) {
  // devo far in modo di ricevere pacchetti continuamente da quando dice INICIO
  // A QUANDO DICE FINE;
  char buffer[MAX_BUFFER_SIZE] = {0};

  while (strcmp(buffer, "Fine\n") != 0) {

    int res = recv(client_fd, buffer, MAX_BUFFER_SIZE, 0);
    if (res > 0) {
      if (strcmp(buffer, "Fine\n") == 0) {
        break;
      }
      log_debug("Stringa arrivata dal client: %s", buffer);

      char *tipo = strtok(buffer, "`");
      log_debug("tipo: %s", tipo);
      char *name = strtok(NULL, "`");
      log_debug("nome: %s", name);
      char *quantita = strtok(NULL, "`");
      log_debug("quantita: %s", quantita);

      // distinguo se è un drink (1) o un shake (2)
      // tipo = strtok(tipo, " ");

      if (strcmp(tipo, "1") == 0) {
        log_debug("Ricevuto un drink: %s, %d ", name, atoi(quantita));

        // Creo la vendita
        if (get_cocktail_amount(name) >= atoi(quantita) &&
            create_sell(search_dictionary(dict, client_fd), name, "Drink",
                        atoi(quantita)) == true) {
          log_debug("Creazione vendita del drink andata a buon fine");
          reduce_amount_cocktail(name, atoi(quantita));
        } else {
          log_error("Errore nella creazione della vendita del drink");
          // invia al client "Errore"
          int status = send(client_fd, "ERRORE\n", 7, 0);
          if (status > 0) {
            log_debug("Errore inviata al client");
          } else {
            log_error("send error: %s", strerror(errno));
          }
          break;
        }
      } else if (strcmp(tipo, "2") == 0) {
        log_debug("Ricevuto un shake: %s, %d ", name, atoi(quantita));

        // Creo la vendita
        if (create_sell(search_dictionary(dict, client_fd), name, "Shake",
                        atoi(quantita)) == true) {
          log_debug("Creazione vendita dello shake andata a buon fine");
          reduce_amount_shake(name, atoi(quantita));
        } else {
          log_error("Errore nella creazione della vendita dello shake");
          int status = send(client_fd, "ERRORE\n", 7, 0);
          if (status > 0) {
            log_debug("Errore inviata al client");
          } else {
            log_error("send error: %s", strerror(errno));
          }
        }
      }
    } else if (res == 0) {
      log_error("Connessione col client chiusa");
      close(client_fd);
      return;
    } else {
      log_error("recv error: %s", strerror(errno));
      int status = send(client_fd, "ERRORE\n", 7, 0);
      if (status > 0) {
        log_debug("Errore inviata al client");
      } else {
        log_error("send error: %s", strerror(errno));
      }
      return;
    }
    bzero(buffer, sizeof(buffer));
  }
  sleep(1);
  // questo perchè alla fine di tutte le operazioni devo mandare FINE
  int send_result = send(client_fd, "Fine\n", strlen("Fine\n"), 0);
  if (send_result == -1) {
    log_error("send error: %s", strerror(errno));
  } else if (send_result > 0) {
    log_debug("Fine inviata al Client");
  }
}

void handle_remove_shake(char *nome, int quantita) {
  if (reduce_amount_shake(nome, quantita) == false) {
    log_error("Imposibile ridurre la quantità dei frullati");
  }
}

void free_dic() {
  if (dict != NULL)
    free_dictionary(dict);
}
