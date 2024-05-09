#include "Database.h"
#include "log.h"
#include <stdio.h>

// Variables
PGconn *conn;
PGresult *res;
int id_vendita = 0;
char *feedback = "";
char *error_response = "";
char *firstdbcommand =
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
  insert_cocktail("Mojito", "Rum;Lime;Zucchero;Menta", 18.0, 6.0, 10);
  insert_cocktail("Bloody Mary",
                  "Vodka;Succo di pomodoro;Tabasco;Sedano;Sale;Pepe;"
                  "nero;Succo di limone;Salsa Worchestershire",
                  25.0, 6.0, 13);
  insert_cocktail("White Russian",
                  "Vodka;Liquore al caffè;Ghiaccio;Panna fresca", 25.0, 7.0,
                  16);
  insert_cocktail("Negroni", "Ghiaccio;Gin;Bitter Campari;Vermut Rosso", 28.0,
                  5.90, 10);
  insert_cocktail("Daquiri",
                  "Rum;Succo di lime;zucchero;ghiaccio;gocce di maraschino",
                  18.9, 6.44, 10);
  insert_cocktail("Dry Martini", "Gin;Scorza di Limone;Vermut Dry;Ghiaccio",
                  14.4, 7.90, 10);
  insert_cocktail("Margarita", "Succo di Lime;Ghiaccio;Triple Sec;Tequila",
                  25.4, 6.44, 10);
  insert_cocktail("Manhattan",
                  "rye wisky;vermout roso;gocce di Angostura;buccia di "
                  "arancia;ghiaccio;ciliegina al Maraschino",
                  30.0, 5.90, 10);
  insert_cocktail("Whiskey Sour",
                  "Whisky;Succo di limone;sciroppo di zucchero;albume", 23.0,
                  4.80, 10);
  insert_cocktail("Moscow Mule", "Vodka;Succo di lime;Ginger beer;Ghiaccio",
                  25.0, 6.44, 10);
  insert_shake("Frullato di frutta", "banana;fragola;kiwi;latte", 3, 5);
  insert_shake("Frullato tropicale", "ananas;mango;succo d'arancia;latte", 4,
               10);
  insert_shake("Frullato di bacche",
               "fragole;mirtilli;lamponi;latte di mandorla", 3.5, 7);
  insert_shake("Frullato proteico",
               "banana;burro di arachidi;semi di chia;latte;proteine", 5, 4);
  insert_shake("Frullato esotico",
               "papaya;ananas;latte di cocco;curcuma;pepe nero", 6, 3);
}

// A test for the reachability of the Database
bool testingConnection() {
  conn = PQconnectdb("dbname = dbcocktail user = postgres password = postgres "
                     "host = localhost port = 5432");
  sleep(2);
  bool status = false;
  switch (PQstatus(conn)) {
  case CONNECTION_OK:
    feedback = "Connesso al server\n";
    status = true;
    break;
  case CONNECTION_BAD:
    feedback = "Connessione fallita";
    break;
  default:
    feedback = "default status\n";
    break;
  }
  log_info("Connection to database status : %s\n", feedback);
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
    log_error("Il cocktail %s non e' presente nel database\n", nome);
    return false; // Perchè?
  }

  if (get_cocktail_amount(nome) < quantita) {
    log_error("Il cocktail %s non e' disponibile\n", nome);
    return false;
  } else {

    char *reduce_amount_command =
        "UPDATE Cocktail SET quantita = quantita - $1 WHERE nome = $2";

    char quantita_string[100];

    log_debug(quantita_string, "%d", quantita);

    const char *paramValues[2] = {quantita_string, nome};

    int paramLengths[2] = {strlen(quantita_string), strlen(nome)};

    int paramFormats[2] = {0, 0};

    PQexecParams(conn, reduce_amount_command, 2, NULL, paramValues,
                 paramLengths, paramFormats, 0);
  }
}
// Same for cocktails
bool reduce_amount_shake(char *nome, int quantita) {

  if (is_shake_in_db(nome) == false) {
    log_error("Il frullato %s non e' presente nel database\n", nome);
    return false;
  }

  if (get_shake_amount(nome) < quantita) {
    log_error("Il frullato %s non e' disponibile\n", nome);
    return false;
  } else {

    char *reduce_amount_command =
        "UPDATE Frullato SET quantita = quantita - $1 WHERE nome = $2";

    char quantita_string[100];

    log_debug(quantita_string, "%d", quantita);

    const char *paramValues[2] = {quantita_string, nome};

    int paramLengths[2] = {strlen(quantita_string), strlen(nome)};

    int paramFormats[2] = {0, 0};

    PQexecParams(conn, reduce_amount_command, 2, NULL, paramValues,
                 paramLengths, paramFormats, 0);
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
void insert_cocktail(char nome[], char ingredienti[],
                     double gradazione_alcolica, double prezzo, int quantita) {
  char *insert_cocktail_command =
      "INSERT INTO Cocktail(nome, ingredienti, gradazione_alcolica, prezzo , "
      "quantita) VALUES ($1, $2, $3, $4, $5)ON CONFLICT (nome) DO NOTHING";

  char gradazione_alcolica_string[100];

  sprintf(gradazione_alcolica_string, "%f", gradazione_alcolica);

  char prezzo_string[100];

  sprintf(prezzo_string, "%f", prezzo);

  char quantita_string[100];

  sprintf(quantita_string, "%d", quantita);

  const char *paramValues[5] = {nome, ingredienti, gradazione_alcolica_string,
                                prezzo_string, quantita_string};

  int paramLengths[5] = {strlen(nome), strlen(ingredienti),
                         strlen(gradazione_alcolica_string),
                         strlen(prezzo_string), strlen(quantita_string)};

  int paramFormats[5] = {0, 0, 0, 0, 0};

  res = PQexecParams(conn, insert_cocktail_command, 5, NULL, paramValues,
                     paramLengths, paramFormats, 0);

  checkres(res);
}

// A full Get
char *get_all_cocktails() {
  char *get_all_cocktail_command =
      "SELECT CONCAT(nome, ', [', ingredienti, '], ', gradazione_alcolica, ', ', prezzo, ', ', quantita)\
   AS informazioni FROM cocktail";

  if (command(get_all_cocktail_command)) {
    // return printQuery(res);
    char *value = printQuery(res);
    log_debug(value);
    return value;
  } else {
    printf("Errore nel recupero dei cocktail\n");
  }
}

char *get_all_shakes() {
  char *get_all_shake_command = "SELECT * FROM Frullato";

  if (command(get_all_shake_command)) {
    return printQuery(res);
  } else {
    printf("Errore nel recupero dei frullati\n");
  }
}

int get_cocktail_amount(char *nome) {

  char *get_cocktail_amount_command =
      "SELECT quantita FROM Cocktail WHERE nome = $1";

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
  char *is_drink_in_db_command = "SELECT nome FROM Cocktail WHERE nome = $1";

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
  char *is_shake_in_db_command = "SELECT nome FROM Frullato WHERE nome = $1";

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

bool is_cliente_in_db(char *email) {

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
    printf("Login effettuato con successo\n");
    return true;
  } else {
    printf("Login fallito: email o password errati\n");
    return false;
  }
}

bool are_credentials_correct(char *email, char *password) {
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
bool create_sell(char *cliente_id, char *coctail_id) {
  char *create_sell_command =
      "INSERT INTO Vendite(id, cliente_id, cocktail_id) VALUES ($1, $2, $3)";

  if (is_cliente_in_db(cliente_id) == false) {
    printf("Inserimento vendita fallito: cliente non registrato\n");
    return false;
  }

  if (is_drink_in_db(coctail_id) == false) {
    printf("Inserimento vendita fallito: cocktail non presente nel database\n");
    return false;
  }

  id_vendita = get_id_vendita() + 1;

  char id_vendita_string[100];

  sprintf(id_vendita_string, "%d", id_vendita);

  const char *paramValues[3] = {id_vendita_string, cliente_id, coctail_id};

  int paramLengths[3] = {strlen(id_vendita_string), strlen(cliente_id),
                         strlen(coctail_id)};

  int paramFormats[3] = {0, 0, 0};

  res = PQexecParams(conn, create_sell_command, 3, NULL, paramValues,
                     paramLengths, paramFormats, 0);

  if (checkres(res)) {
    printf("Inserimento vendita effettuato con successo\n");
    return true;
  } else {
    printf("Inserimento vendita fallito: %s\n", error_response);
    return false;
  }
}

void insert_shake(char nome[], char ingredienti[], double prezzo,
                  int quantita) {
  char *insert_shake_command =
      "INSERT INTO Frullato(nome, ingredienti, prezzo , quantita) VALUES ($1, "
      "$2, $3, $4)ON CONFLICT (nome) DO NOTHING";

  char prezzo_string[100];

  sprintf(prezzo_string, "%f", prezzo);

  char quantita_string[100];

  sprintf(quantita_string, "%d", quantita);

  const char *paramValues[4] = {nome, ingredienti, prezzo_string,
                                quantita_string};

  int paramLengths[4] = {strlen(nome), strlen(ingredienti),
                         strlen(prezzo_string), strlen(quantita_string)};

  int paramFormats[4] = {0, 0, 0, 0};

  res = PQexecParams(conn, insert_shake_command, 4, NULL, paramValues,
                     paramLengths, paramFormats, 0);

  checkres(res);
}

int get_shake_amount(char *nome) {

  char *get_shake_amount_command =
      "SELECT quantita FROM Frullato WHERE nome = $1";

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

void close_connection() { PQfinish(conn); }

// char[1000] ingredienti = sprintf("%s;")
// sprintf("%s, [%s], %f, %f, %d", nome, ingredienti, gradazione_alcolica,
// prezzo, quantita)
