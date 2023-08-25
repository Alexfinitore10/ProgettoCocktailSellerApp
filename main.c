#include "includes.h"
#include <string.h>


void get_all_cocktail();
void insert_cocktail(char*,double,int);
void reduce_amount(char*,int);
void testingConnection(PGconn*);
void command(PGconn*, char*, PGresult*);
void checkres(PGresult*);


char *feedback = "";

PGconn *conn;
PGresult *res;

int main(){
    
    char *comando_cliente = "";
    char *comando_cocktail = "";
    char *comando_frullato = "";
    //Creo l'oggetto database
    

    //Provo a connettermi al Database
    conn = PQconnectdb("dbname = dbcocktail user = postgres password = postgres host = localhost port = 5432");

    testingConnection(conn);

    comando_cliente = "CREATE TABLE IF NOT EXISTS Cliente(id INTEGER)";
    comando_cocktail = "CREATE TABLE IF NOT EXISTS Cocktail(id INTEGER)";
    comando_frullato = "CREATE TABLE IF NOT EXISTS Frullato(id INTEGER)";

    command(conn, comando_cliente, res);
    command(conn, comando_cocktail, res);
    command(conn, comando_frullato, res);
    

    PQfinish(conn);
}


void testingConnection(PGconn* conn){
    sleep(2);
    switch (PQstatus(conn))
    {
    case CONNECTION_STARTED:
        feedback = "Connessione in corso...\n";
        break;
    case CONNECTION_MADE:
        feedback = "Connesso al server\n";
        break;
    case CONNECTION_BAD:
        feedback = "Connessione fallita";
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