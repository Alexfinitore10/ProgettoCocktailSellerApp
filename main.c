#include "includes.h"
#include <string.h>


void get_all_cocktail();
void insert_cocktail(char*,double,int);
void reduce_amount(char*,int);
void testingConnection(PGconn*);
void command(PGconn*, char*, PGresult*);
void checkres(PGresult*);


char *feedback = "";

int main(){
    
    char *comando = "";
    //Creo l'oggetto database
    PGconn *conn;
    PGresult *res;

    //Provo a connettermi al Database
    conn = PQconnectdb("dbname = dbcocktail user = postgres password = postgres host = localhost port = 5432");

    testingConnection(conn);

    comando = "CREATE TABLE IF NOT EXISTS Bruschetta(id INTEGER)";

    command(conn, comando, res);
    

    

     PQfinish(conn);
}


void testingConnection(PGconn* conn){
    sleep(2);
    switch (PQstatus(conn))
    {
    case CONNECTION_STARTED:
        feedback = "Connecting...\n";
        break;
    case CONNECTION_MADE:
        feedback = "Connected to server...\n";
        break;
    case CONNECTION_BAD:
        feedback = "connessione non riuscita\n";
        break;
    default:
        feedback = "default\n";
        break;
    }
    printf("Connection status : %s", feedback);
}

void command(PGconn* conn, char *comando, PGresult* res){
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
        risultato = "Query Completata";
        break;
    case PGRES_EMPTY_QUERY:
        risultato = "query vuota";
        break;
    case PGRES_TUPLES_OK:
        risultato = "Query completata con ritorno di dati";
        break;
    case PGRES_FATAL_ERROR:
        risultato = "errore fatale\n";
        break;
    default:
        risultato = "operazioe non andata a buon fine";
        break;
    }
    printf(risultato);
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