#include "includes.h"
#include <string.h>
#include "Login.h"
#include "Socket.h"


void get_all_cocktail();
void insert_cocktail(char*,double,int);
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
                        CREATE TABLE IF NOT EXISTS Cocktail(id INTEGER PRIMARY KEY, nome VARCHAR(255) NOT NULL, ingredienti TEXT NOT NULL, gradazione_alcolica DECIMAL(1,1) NOT NULL);\
                        CREATE TABLE IF NOT EXISTS Frullato(id INTEGER PRIMARY KEY, nome VARCHAR(255) NOT NULL, ingredienti TEXT NOT NULL, gusto VARCHAR(10) NOT NULL);\
                        CREATE TABLE IF NOT EXISTS Vendite (\
                        id INTEGER PRIMARY KEY,\
                        cliente_id INTEGER,\
                        cocktail_id INTEGER,\
                        CONSTRAINT cliente_fk FOREIGN KEY (cliente_id) REFERENCES Cliente (id),\
                        CONSTRAINT cocktail_fk FOREIGN KEY (cocktail_id) REFERENCES Cocktail (id));";

int main(){

    //chiamo le funzioni per connettermi al database
    dbconnection();    
    //chiamare la funzione di creazione socket nell'altro file.


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

}


void reduce_amount(char *nome, int quantita)
{
    //TODO
}

void get_all_cocktail()
{
    //TODO
}

void insert_cocktail(char nome[], double prezzo, int quantita)
{
    //TODO
}

