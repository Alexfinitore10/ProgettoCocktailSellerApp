#include "includes.h"


void get_all_cocktail();
void insert_cocktail(char[],double,int);
void reduce_amount(char[],int);

int main(){
    PGconn *conn;
    conn = PQconnectdb("dbname = elvino user = elvino password = elvino host = 127.0.0.1 port = 5432");

    if (PQstatus(conn) == CONNECTION_BAD){
        PQfinish(conn);
    }

    int ver = PQserverVersion(conn);

    printf("Server version %d\n",ver);

    PQfinish(conn);
}

void reduce_amount(char nome[], int quantita)
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