#include "includes.h"
#include "Login.h"
#include "Socket.h"


void get_all_cocktail();
void insert_cocktail(char[], char[], double, double, int);
void reduce_amount(char*,int);
bool testingConnection(PGconn*);
void command(char*);
void checkres(PGresult*);
void dbconnection();
void signup();
void opensocket();

char *feedback = "";

//Connessione Al DB
PGconn *conn;

//Risultato dal database
PGresult *res;

char *firstdbcommand = "CREATE TABLE IF NOT EXISTS Cliente(id INTEGER PRIMARY KEY, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL);\
                        CREATE TABLE IF NOT EXISTS Cocktail(nome VARCHAR(255) PRIMARY KEY, ingredienti TEXT NOT NULL, gradazione_alcolica DECIMAL(1,1) NOT NULL, prezzo DOUBLE PRECISION , quantita INTEGER);\
                        CREATE TABLE IF NOT EXISTS Frullato(id INTEGER PRIMARY KEY, nome VARCHAR(255) NOT NULL, ingredienti TEXT NOT NULL, gusto VARCHAR(10) NOT NULL);\
                        CREATE TABLE IF NOT EXISTS Vendite (\
                        id INTEGER PRIMARY KEY,\
                        cliente_id INTEGER,\
                        cocktail_id VARCHAR(255) ,\
                        CONSTRAINT cliente_fk FOREIGN KEY (cliente_id) REFERENCES Cliente (id),\
                        CONSTRAINT cocktail_fk FOREIGN KEY (cocktail_id) REFERENCES Cocktail (nome));";

int main(){

    //chiamo le funzioni per connettermi al database
    dbconnection();    
    //chiamare la funzione di creazione socket nell'altro file.

    insert_cocktail("Mojito", "Rum, Lime, Zucchero, Menta", 0.1, 5.0, 10);

    PQfinish(conn);
}



void dbconnection(){

    conn = PQconnectdb("dbname = dbcocktail user = postgres password = postgres host = localhost port = 5432");
    
    //Provo a connettermi al Database
    while (testingConnection(conn) == false){
        printf("La connessione non e effettuabile, riprovo tra 5 secondi...\n");
        sleep(5);
    }
    //primo comando da eseguire
    command(firstdbcommand);

    

}


bool testingConnection(PGconn* conn){
    sleep(2);
    bool status = false;
    switch (PQstatus(conn))
    {
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
    printf("Connection status : %s\n", feedback);
    return status;
}


void command(char *comando){
    res = PQexec(conn, comando);
    checkres(res);
}

void checkres(PGresult* res){
    char *risultato;
    ExecStatusType ris;
    ris = PQresultStatus(res);
    switch (ris)
    {
    case PGRES_COMMAND_OK:
        risultato = "Query completata\n";
        break;
    case PGRES_EMPTY_QUERY:
        risultato = "Query vuota\n";
        break;
    case PGRES_TUPLES_OK:
        risultato = "Query completata con ritorno di dati\n";
        break;
    case PGRES_FATAL_ERROR:
        risultato = "Errore fatale\n";
        break;
    default:
        risultato = "Operazione non andata a buon fine\n";
        break;
    }
    printf("%s",risultato);
}

void signup(){
    //TODO
}


void reduce_amount(char *nome, int quantita)
{
    //TODO
}

void get_all_cocktail()
{
    char *get_all_cocktail_command = "SELECT * FROM Cocktail";

    command(get_all_cocktail_command);
}

void insert_cocktail(char nome[], char ingredienti[], double gradazione_alcolica ,double prezzo, int quantita)
{
    char *insert_cocktail_command = "INSERT INTO Cocktail(nome, ingredienti, gradazione_alcolica, prezzo , quantita) VALUES ($1, $2, $3, $4, $5)";

    char gradazione_alcolica_string[100];

    sprintf(gradazione_alcolica_string, "%f", gradazione_alcolica);

    char prezzo_string[100];

    sprintf(prezzo_string, "%f", prezzo);

    char quantita_string[100];

    sprintf(quantita_string, "%d", quantita);

    const char *paramValues[5] = {nome, ingredienti, gradazione_alcolica_string, prezzo_string, quantita_string};

    int paramLengths[5] = {strlen(nome), strlen(ingredienti), strlen(gradazione_alcolica_string), strlen(prezzo_string), strlen(quantita_string)};

    int paramFormats[5] = {0, 0, 0, 0, 0};

    res = PQexecParams(conn, insert_cocktail_command, 5, NULL, paramValues, paramLengths, paramFormats, 0);

    checkres(res);
    
}

