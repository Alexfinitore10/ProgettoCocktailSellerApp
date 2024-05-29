#include "Database.h"
#include "log.h"
#include <libpq-fe.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>

// Variables
static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
PGconn *conn;
PGresult *res;
int id_vendita = 0;
char *feedback = "";
char *error_response = "";
char *firstdbcommand =
    "CREATE TABLE IF NOT EXISTS Cliente ("
    "email VARCHAR(255) PRIMARY KEY, "
    "password VARCHAR(255) NOT NULL, "
    "isLogged BOOLEAN NOT NULL DEFAULT false"
    "); "
    "CREATE TABLE IF NOT EXISTS Prodotti ("
    "prodotto_id SERIAL PRIMARY KEY, "
    "nome VARCHAR(255) NOT NULL UNIQUE, "
    "tipo VARCHAR(50) NOT NULL, " // 'cocktail' o 'frullato'
    "ingredienti VARCHAR(1000) NOT NULL, "
    "gradazione_alcolica DOUBLE PRECISION, " // Specifica per cocktail
    "prezzo DOUBLE PRECISION NOT NULL, "
    "quantita INTEGER NOT NULL"
    "); "
    "CREATE TABLE IF NOT EXISTS Vendite ("
    "vendita_id SERIAL PRIMARY KEY, "
    "utente_email VARCHAR(255), "
    "prodotto_id VARCHAR(255), "
    "quantita INT, "
    "tipo VARCHAR(50) NOT NULL,"
    "FOREIGN KEY (prodotto_id) REFERENCES Prodotti(nome), "
    "FOREIGN KEY (utente_email) REFERENCES Cliente(email)"
    ");";

char *oldfirstdbcommand =
    "CREATE TABLE IF NOT EXISTS Cliente(email VARCHAR(255) PRIMARY KEY, password VARCHAR(255) NOT NULL, isLogged BOOLEAN NOT NULL DEFAULT false);\
                        CREATE TABLE IF NOT EXISTS Cocktail(nome VARCHAR(255) PRIMARY KEY, ingredienti VARCHAR(1000) NOT NULL, gradazione_alcolica DOUBLE PRECISION , prezzo DOUBLE PRECISION , quantita INTEGER);\
                        CREATE TABLE IF NOT EXISTS Frullato(nome VARCHAR(255) PRIMARY KEY, ingredienti VARCHAR(1000) NOT NULL, prezzo DOUBLE PRECISION , quantita INTEGER);\
                        CREATE TABLE IF NOT EXISTS Vendite (\
                        id INTEGER PRIMARY KEY,\
                        cliente_id VARCHAR(255),\
                        cocktail_id VARCHAR(255) ,\
                        CONSTRAINT cliente_fk FOREIGN KEY (cliente_id) REFERENCES Cliente (email) ,\
                        CONSTRAINT cocktail_fk FOREIGN KEY (cocktail_id) REFERENCES Cocktail (nome));";

// Creating Database
void createdb_query() {

  // Provo a connettermi al Database
  while (testingConnection() == false) {
    log_warn("La connessione non e' effettuabile, riprovo tra 5 secondi...\n");
    sleep(5);
  }
  // primo comando da eseguire
  log_debug("Eseguo la query di costruzione del database\n");

  if (command(firstdbcommand)) {
    log_debug("Popolo il database...\n");
    cocktail_and_shake_population();
  }
}

// Populating the DB
void cocktail_and_shake_population() {
  insert_prodotto("Mojito", "Rum;Lime;Zucchero;Menta", 18.0, 6.0, 10,
                  "cocktail");
  insert_prodotto("Bloody Mary",
                  "Vodka;Succo di pomodoro;Tabasco;Sedano;Sale;Pepe;nero;Succo "
                  "di limone;Salsa Worchestershire",
                  25.0, 6.0, 13, "cocktail");
  insert_prodotto("White Russian",
                  "Vodka;Liquore al caffè;Ghiaccio;Panna fresca", 25.0, 7.0, 16,
                  "cocktail");
  insert_prodotto("Negroni", "Ghiaccio;Gin;Bitter Campari;Vermut Rosso", 28.0,
                  5.90, 10, "cocktail");
  insert_prodotto("Daquiri",
                  "Rum;Succo di lime;zucchero;ghiaccio;gocce di maraschino",
                  18.9, 6.44, 10, "cocktail");
  insert_prodotto("Dry Martini", "Gin;Scorza di Limone;Vermut Dry;Ghiaccio",
                  14.4, 7.90, 10, "cocktail");
  insert_prodotto("Margarita", "Succo di Lime;Ghiaccio;Triple Sec;Tequila",
                  25.4, 6.44, 10, "cocktail");
  insert_prodotto("Manhattan",
                  "rye wisky;vermout roso;gocce di Angostura;buccia di "
                  "arancia;ghiaccio;ciliegina al Maraschino",
                  30.0, 5.90, 10, "cocktail");
  insert_prodotto("Whiskey Sour",
                  "Whisky;Succo di limone;sciroppo di zucchero;albume", 23.0,
                  4.80, 10, "cocktail");
  insert_prodotto("Moscow Mule", "Vodka;Succo di lime;Ginger beer;Ghiaccio",
                  25.0, 6.44, 10, "cocktail");

  insert_prodotto("Frullato di frutta", "banana;fragola;kiwi;latte", 0.0, 5, 10,
                  "frullato");
  insert_prodotto("Frullato tropicale", "ananas;mango;succo d'arancia;latte",
                  0.0, 10, 10, "frullato");
  insert_prodotto("Frullato di bacche",
                  "fragole;mirtilli;lamponi;latte di mandorla", 0.0, 7, 10,
                  "frullato");
  insert_prodotto("Frullato proteico",
                  "banana;burro di arachidi;semi di chia;latte;proteine", 0.0,
                  4, 10, "frullato");
  insert_prodotto("Frullato esotico",
                  "papaya;ananas;latte di cocco;curcuma;pepe nero", 0.0, 3, 10,
                  "frullato");
}

void connection_lock() {
  log_debug("Mutex locked");
  pthread_mutex_lock(&mutex);
}

void connection_unlock() {
  log_debug("Mutex unlocked");
  pthread_mutex_unlock(&mutex);
}

bool is_connection_locked() { return pthread_mutex_trylock(&mutex) != 0; }

bool testingMutex() {
  if (is_connection_locked()) {
    return false;
  }
}

// A test for the reachability of the Database
bool testingConnection() {
  bool status;
  connection_lock();
  conn = PQconnectdb("dbname = dbcocktail user = postgres password = postgres "
                     "host = localhost port = 5432");
  if (conn == NULL) {
    feedback = "Connessione fallita";
    log_info("Connection to database status : %s\n", feedback);
    connection_unlock();
    return false;
  }
  switch (PQstatus(conn)) {
  case CONNECTION_OK:
    feedback = "Connesso al server\n";
    status = true;
    break;
  case CONNECTION_BAD:
    feedback = "Connessione fallita";
    status = false;
    break;
  default:
    feedback = "default status\n";
    status = false;
    break;
  }
  log_info("Connection to database status : %s\n", feedback);
  connection_unlock();
  return status;
}

// Takes a command (String) and returns true or false if it was successful or
// not
bool command(char *comando) {
  bool status = false;
  res = PQexec(conn, comando);
  if (checkres(res)) {
    return true;
  } else
    return false;
}

// Funzione di riduzione mandata al Database quando un ordine viene effettuato,
// controlla anche se può farlo o meno(?) Ovviamente fatta male
bool reduce_amount_cocktail(char *nome, int quantita) {

  if (is_drink_in_db(nome) == false) {
    log_error("Il cocktail %s non e' presente nel database", nome);
    return false; // Perchè?M
  }

  if (get_cocktail_amount(nome) < quantita) {
    log_error("Il cocktail %s non e' disponibile", nome);
    return false;
  } else {

    char reduce_amount_command[100];

    snprintf(reduce_amount_command, sizeof(reduce_amount_command),
             "UPDATE Prodotti SET quantita = quantita - $2 WHERE nome = $1");

    char quantita_string[20];

    // Utilizza snprintf per convertire l'intero in una stringa
    snprintf(quantita_string, sizeof(quantita_string), "%d", quantita);

    const char *paramValues[2] = {nome, quantita_string};

    int paramLengths[2] = {strlen(nome), sizeof(quantita)};

    PGresult *res = PQexecParams(conn, reduce_amount_command, 2, NULL,
                                 paramValues, paramLengths, NULL, 0);
    if (PQresultStatus(res) == PGRES_COMMAND_OK) {
      log_debug("Quantità del drink %s diminuita correttamente di %d", nome,
                quantita);
    } else {
      log_error("Errore nella riduzione della quantità del drink %s: %s", nome,
                PQerrorMessage(conn));
    }
    PQclear(res);
    return true;
  }
}
// Same for cocktails
bool reduce_amount_shake(char *nome, int quantita) { // TODO Da rifare

  if (is_shake_in_db(nome) == false) { // controllo correttezza nome
    log_error("Il frullato %s non e' presente nel database\n", nome);
    return false;
  }

  if (get_shake_amount(nome) < quantita) { // Controllo correttezza quantita
    log_error("Il frullato %s non e' disponibile\n", nome);
    return false;
  } else {

    char *reduce_amount_command =
        "UPDATE Prodotti SET quantita = quantita - $1 WHERE nome = $2";

    char quantita_string[100];

    snprintf(quantita_string, sizeof(quantita_string), "%d", quantita);

    const char *paramValues[2] = {quantita_string, nome};

    int paramLengths[2] = {strlen(quantita_string), strlen(nome)};

    PGresult *res = PQexecParams(conn, reduce_amount_command, 2, NULL,
                                 paramValues, paramLengths, NULL, 0);
    if (PQresultStatus(res) == PGRES_COMMAND_OK) {
      log_debug("Quantità dello shake %s diminuita correttamente di %d", nome,
                quantita);
    } else {
      log_error("Errore nella riduzione della quantità dello shakes %s: %s",
                nome, PQerrorMessage(conn));
    }
    PQclear(res);
    return true;
  }
}
// Checks the result of the query -> Used in line in the "command" function
bool checkres(PGresult *res) {
  char *risultato;
  ExecStatusType ris;
  bool status = false;
  ris = PQresultStatus(res);
  switch (ris) {
  case PGRES_COMMAND_OK:
    risultato = "Query completata\n";
    log_debug("%s : %s", risultato, PQresultErrorMessage(res));
    status = true;
    break;
  case PGRES_EMPTY_QUERY:
    risultato = "Query vuota\n";
    log_warn("%s : %s", risultato, PQresultErrorMessage(res));
    break;
  case PGRES_TUPLES_OK:
    risultato = "Query completata con ritorno di dati\n";
    log_debug("%s : %s", risultato, PQresultErrorMessage(res));
    status = true;
    break;
  case PGRES_FATAL_ERROR:
    risultato = "Errore fatale";
    log_error("%s : %s", risultato, PQresultErrorMessage(res));
    break;
  default:
    risultato = "Operazione non andata a buon fine\n";
    log_error("%s : %s", risultato, PQresultErrorMessage(res));
    break;
  }
  return status;
}

// Function to insert a cocktail in the DB. It's primarily used in the DB
// population function (Cocktail and shake_population)
void insert_prodotto(char nome[], char ingredienti[],
                     double gradazione_alcolica, double prezzo, int quantita,
                     char tipo[]) {
  const char *insert_prodotto_command =
      "INSERT INTO Prodotti(nome, tipo, ingredienti, gradazione_alcolica, "
      "prezzo, quantita) "
      "VALUES ($1, $2, $3, $4, $5, $6) ON CONFLICT (nome) DO NOTHING;";

  char gradazione_alcolica_string[100];
  const char *gradazione_alcolica_value;

  if (strcmp(tipo, "cocktail") == 0) {
    sprintf(gradazione_alcolica_string, "%f", gradazione_alcolica);
    gradazione_alcolica_value = gradazione_alcolica_string;
  } else {
    gradazione_alcolica_value = NULL;
  }

  char prezzo_string[100];
  sprintf(prezzo_string, "%f", prezzo);

  char quantita_string[100];
  sprintf(quantita_string, "%d", quantita);

  const char *paramValues[6] = {nome,          tipo,
                                ingredienti,   gradazione_alcolica_value,
                                prezzo_string, quantita_string};
  int paramLengths[6] = {
      strlen(nome),
      strlen(tipo),
      strlen(ingredienti),
      gradazione_alcolica_value ? strlen(gradazione_alcolica_value) : 0,
      strlen(prezzo_string),
      strlen(quantita_string)};
  int paramFormats[6] = {0, 0, 0, 0, 0, 0};

  PGresult *res = PQexecParams(conn, insert_prodotto_command, 6, NULL,
                               paramValues, paramLengths, paramFormats, 0);

  checkres(res);
}

// A full Get
char *get_all_cocktails() { // TODO da rifare
  char *get_all_cocktail_command =
      "SELECT CONCAT(nome, ', [', ingredienti, '], ', "
      "COALESCE(gradazione_alcolica::text, ''), ', ', prezzo, ', ', quantita) "
      "AS informazioni FROM Prodotti WHERE tipo = 'cocktail';";

  if (command(get_all_cocktail_command)) {
    // return printQuery(res);
    char *value = printQuery(res);
    return value;
  } else {
    printf("Errore nel recupero dei cocktail\n");
  }
}

char *get_all_shakes() {
  char *get_all_shake_command =
      "SELECT CONCAT(nome, ', [', ingredienti, '], ', prezzo, ', ', quantita) "
      "AS informazioni FROM Prodotti WHERE tipo = 'frullato';";

  if (command(get_all_shake_command)) {
    return printQuery(res);
  } else {
    printf("Errore nel recupero dei frullati\n");
  }
}

int get_cocktail_amount(char *nome) {

  char *get_cocktail_amount_command =
      "SELECT quantita FROM Prodotti WHERE nome = $1";

  const char *paramValues[1] = {nome};

  int paramLengths[1] = {strlen(nome)};

  int paramFormats[1] = {0};

  res = PQexecParams(conn, get_cocktail_amount_command, 1, NULL, paramValues,
                     paramLengths, paramFormats, 0);

  checkres(res);

  int quantita = atoi(PQgetvalue(res, 0, 0));

  return quantita;
}

int get_id_vendita() {
  char *get_id_vendita_command =
      "SELECT id FROM Vendite ORDER BY id DESC LIMIT 1";

  res = PQexec(conn, get_id_vendita_command);

  if (checkres(res) == false) {
    printf("Errore nel recupero dell'id della vendita\n");
    return -1;
  }

  int id = atoi(PQgetvalue(res, 0, 0));

  return id;
}

bool is_drink_in_db(char *nome) {
  char *is_drink_in_db_command = "SELECT nome FROM Prodotti WHERE nome = $1";

  const char *paramValues[1] = {nome};

  int paramLengths[1] = {strlen(nome)};

  int paramFormats[1] = {0};

  res = PQexecParams(conn, is_drink_in_db_command, 1, NULL, paramValues,
                     paramLengths, paramFormats, 0);

  if (PQntuples(res) == 0) {
    return false;
  } else {
    return true;
  }
}

bool is_shake_in_db(char *nome) {
  char *is_shake_in_db_command = "SELECT nome FROM Prodotti WHERE nome = $1";

  const char *paramValues[1] = {nome};

  int paramLengths[1] = {strlen(nome)};

  int paramFormats[1] = {0};

  res = PQexecParams(conn, is_shake_in_db_command, 1, NULL, paramValues,
                     paramLengths, paramFormats, 0);

  if (PQntuples(res) == 0) {
    return false;
  } else {
    return true;
  }
}

bool is_cliente_in_db(const char *email) {

  char *is_cliente_in_db_command = "SELECT email FROM Cliente WHERE email = $1";

  const char *paramValues[1] = {email};

  int paramLengths[1] = {strlen(email)};

  int paramFormats[1] = {0};

  res = PQexecParams(conn, is_cliente_in_db_command, 1, NULL, paramValues,
                     paramLengths, paramFormats, 0);

  if (PQntuples(res) == 0) {
    return false;
  } else {
    return true;
  }
}
// Registrazione utente
char signup(char *email, char *password) {

  // For testing
  printf("Email: %s\n", email);
  printf("Password: %s\n", password);
  // For testing

  if (is_cliente_in_db(email)) { // Check se è già registrato
    printf("Registrazione fallita: cliente già registrato\n");
    return 'A';
  } else {

    char *signup_command =
        "INSERT INTO Cliente(email, password) VALUES ($1, $2)";

    const char *paramValues[2] = {email, password};

    int paramLengths[2] = {strlen(email), strlen(password)};

    int paramFormats[2] = {0, 0};

    res = PQexecParams(conn, signup_command, 2, NULL, paramValues, paramLengths,
                       paramFormats, 0);

    if (checkres(res)) {
      printf("Registrazione effettuata con successo\n");
      return 'T';
    } else {
      printf("Registrazione fallita: %s\n", error_response);
      return 'F';
    }
  }
}
// Log-in Utente
bool signin(char *email, char *password) {
  if (are_credentials_correct(email, password)) {
    char *isLogged = "UPDATE Cliente SET isLogged = true WHERE email = $1";
    const char *paramValues[1] = {email};
    int paramLengths[1] = {strlen(email)};
    int paramFormats[1] = {0};
    res = PQexecParams(conn, isLogged, 1, NULL, paramValues, paramLengths,
                       paramFormats, 0);
    if (checkres(res)) {
      log_info("Login effettuato con successo\n");
      return true;
    } else {
      log_error("Login fallito: %s\n", error_response);
      return false;
    }
  } else {
    printf("Login fallito: email o password errati\n");
    return false;
  }
}

bool logoff(const char *email) {
  log_debug("email: %s", email);
  char *isLogged = "UPDATE Cliente SET isLogged = false WHERE email = $1";
  const char *paramValues[1] = {email};
  int paramLengths[1] = {strlen(email)};
  int paramFormats[1] = {0};
  res = PQexecParams(conn, isLogged, 1, NULL, paramValues, paramLengths,
                     paramFormats, 0);
  if (checkres(res)) {
    log_info("Logout effettuato");
    return true;
  } else {
    log_error("Impossibile effettuare il logout");
    return false;
  }
}

bool are_credentials_correct(char *email, char *password) {
  log_debug("email: %s, password: %s\n", email, password);
  char *credentials_command =
      "SELECT email FROM Cliente WHERE email = $1 AND password = $2";

  const char *paramValues[2] = {email, password};

  int paramLengths[2] = {strlen(email), strlen(password)};

  int paramFormats[2] = {0, 0};

  res = PQexecParams(conn, credentials_command, 2, NULL, paramValues,
                     paramLengths, paramFormats, 0);

  if (PQntuples(res) == 0) {
    return false;
  } else {
    return true;
  }
}
//
bool create_sell(const char *cliente_id, char *nome_bevanda, char *tipo,
                 int quantita) {
  const char *insert_command = "INSERT INTO Vendite(utente_email, prodotto_id, "
                               "tipo, quantita) VALUES ($1, $2, $3, $4)";

  char quantita_str[16]; // Buffer per la conversione dell'intero in stringa
  snprintf(quantita_str, sizeof(quantita_str), "%d", quantita);

  const char *paramValues[4] = {cliente_id, nome_bevanda, tipo, quantita_str};
  int paramLengths[4] = {0}; // 0 means "null terminated"
  int paramFormats[4] = {0}; // 0 means "text"

  res = PQexecParams(conn, insert_command, 4, NULL, paramValues, paramLengths,
                     paramFormats, 0);

  if (PQresultStatus(res) != PGRES_COMMAND_OK) {
    fprintf(stderr, "Insertion failed: %s", PQerrorMessage(conn));
    PQclear(res);
    return false;
  }
  PQclear(res);
  return true;
}

char *get_recommended_drinks() {
  char *query = "SELECT "
                "CONCAT("
                "p.nome, ', [', p.ingredienti, '], ', "
                "COALESCE(p.gradazione_alcolica::text, ''), ', ', "
                "p.prezzo, ', ', "
                "SUM(v.quantita)"
                ") AS formatted_string "
                "FROM "
                "Prodotti p "
                "JOIN "
                "Vendite v ON p.nome = v.prodotto_id "
                " WHERE "
                "p.tipo = 'cocktail' "
                "GROUP BY "
                "p.nome, p.ingredienti, p.gradazione_alcolica, p.prezzo "
                "ORDER BY "
                "SUM(v.quantita) DESC "
                "LIMIT "
                " 3;";

  if (command(query)) {
    // return printQuery(res);
    char *value = printQuery(res); // res è globale......................
    int rows = PQntuples(res);
    if (rows == 0) {
      fprintf(stderr, "La query non ha restituito alcun risultato.\n");
      PQclear(res);
      return strdup("Ness");
    } else if (rows < 3) {
      log_error("Non ci sono abbastanza drink per fare raccomandazioni. Solo "
                "%d drink trovati.\n",
                rows);
      PQclear(res);
      return strdup("Pochi");
    }
    PQclear(res);
    return value;
  } else {
    printf("Errore nel recupero dei cocktail\n");
  }
}

char *get_recommended_shakes() {
  char *query = "SELECT "
                "CONCAT("
                "p.nome, ', [', p.ingredienti, '], ', "
                "p.prezzo, ', ', "
                "SUM(v.quantita)"
                ") AS formatted_string "
                "FROM "
                "Prodotti p "
                "JOIN "
                "Vendite v ON p.nome = v.prodotto_id "
                " WHERE "
                "p.tipo = 'frullato' "
                "GROUP BY "
                "p.nome, p.ingredienti, p.prezzo "
                "ORDER BY "
                "SUM(v.quantita) DESC "
                "LIMIT "
                " 3;";

  if (command(query)) {
    // return printQuery(res);
    char *value = printQuery(res); // res è globale......................
    int rows = PQntuples(res);
    if (rows == 0) {
      fprintf(stderr, "La query non ha restituito alcun risultato.\n");
      PQclear(res);
      return strdup("Ness");
    } else if (rows < 3) {
      log_error("Non ci sono abbastanza shakes per fare raccomandazioni. Solo "
                "%d drink trovati.\n",
                rows);
      PQclear(res);
      return strdup("Pochi");
    }
    PQclear(res);
    return value;
  } else {
    printf("Errore nel recupero dei shakes\n");
  }
}

int get_shake_amount(char *nome) {

  char *get_shake_amount_command =
      "SELECT quantita FROM Prodotti WHERE nome = $1";

  const char *paramValues[1] = {nome};

  int paramLengths[1] = {strlen(nome)};

  int paramFormats[1] = {0};

  res = PQexecParams(conn, get_shake_amount_command, 1, NULL, paramValues,
                     paramLengths, paramFormats, 0);

  checkres(res);

  int quantita = atoi(PQgetvalue(res, 0, 0));

  return quantita;
}

char *printQuery(PGresult *res) {
  int nFields = PQnfields(res);
  int nTuples = PQntuples(res);

  size_t response_size = 512;
  char *response = malloc(response_size * sizeof(char));
  response[0] = '\0'; // Initialize the string

  for (int i = 0; i < nTuples; i++) {
    for (int j = 0; j < nFields; j++) {
      char *value = PQgetvalue(res, i, j);
      size_t value_len = strlen(value);

      // Check if the response string is about to overflow
      while (strlen(response) + value_len + 2 > response_size) {
        // Double the size of the response string
        response_size *= 2;
        response = realloc(response, response_size * sizeof(char));
      }

      // Concatenate the value and a tab character
      strcat(response, value);
      strcat(response, "\t");
    }
    strcat(response, "\n");
  }
  // response[strlen(response) - 1] = '`';

  return response;
}

void close_connection() {
  PQclear(res);
  PQfinish(conn);

  // Free the memory allocated for the response string
  free(res);
}
