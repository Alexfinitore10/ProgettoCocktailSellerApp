#include "includes.h"

sqlite3* database;

void get_all_cocktail();
void insert_cocktail(char[],double,int);
void reduce_amount(char[],int);

int main(){
    sqlite3_open("Cocktail.db", &database);



    char* query = "CREATE TABLE IF NOT EXISTS  Cocktail(nome VARCHAR(50) PRIMARY KEY,"
                                         "prezzo DOUBLE(10,2) NOT NULL,"
                                         "quantita INTEGER NOT NULL);"; 
    
    if(sqlite3_exec(database, query, NULL, NULL, NULL) == SQLITE_OK)
    {
        printf("tabella creata\n");
    }

    get_all_cocktail();

    reduce_amount("Bloody Mary", 5);

    get_all_cocktail();

    //insert_cocktail("Moscow Mule", 6.00, 50);

    //get_all_cocktail();


    sqlite3_close(database);
}

void reduce_amount(char nome[], int quantita)
{
    int getquantita;

    char* alter = "UPDATE Cocktail SET quantita = ? WHERE nome = ?;";

    char* selectquantita = "SELECT quantita FROM Cocktail WHERE nome = ?;";

    sqlite3_stmt* statement;

    sqlite3_stmt* select;

    //setto il prepared statement per la select
    sqlite3_prepare_v2(database, selectquantita,strlen(selectquantita),&select, NULL);

    //bindo il prepared statement per la select
    sqlite3_bind_text(select, 1, nome, strlen(nome), SQLITE_TRANSIENT);


    if(sqlite3_step(select) == SQLITE_ROW)
    {
        printf("Query effettuata\n");
        getquantita = sqlite3_column_int(select,0);
        printf("Quantità : %d\n", getquantita);
    }else printf("c'è un problema con la query per il get della quantita\n");

    //calcolo la differenza tra le quantità
    if(getquantita < quantita)
    {
        printf("la quantità richiesta è maggiore di quella a disposizione\n");
    }else
    {
        printf("Aggiorno i numeri\n");
        getquantita -= quantita;

        sqlite3_prepare_v2(database, alter,strlen(alter),&statement, NULL);
        sqlite3_bind_int(statement, 1, getquantita);
        sqlite3_bind_text(statement, 2, nome, strlen(nome), SQLITE_TRANSIENT);

        if(sqlite3_step(statement) == SQLITE_DONE)
        {
            printf("Quantità diminuita con successo\n");
        }else printf("Quantità non diminuita con successo\n");
    }
}

void get_all_cocktail()
{
    char* insert= "SELECT * FROM Cocktail;";

    sqlite3_stmt* statement;

    sqlite3_prepare_v2(database,insert,strlen(insert),&statement, NULL);

    int status, row = 0;

    while (1)
    {
        if((status = sqlite3_step(statement)) == SQLITE_ROW)
        {
            printf("Nome : %s\t", sqlite3_column_text(statement,0));
            printf("Prezzo : %f\t", sqlite3_column_double(statement,1));
            printf("Quantita : %d\n", sqlite3_column_int(statement,2));
        }else if(status == SQLITE_DONE){
            break;
        }else{
            printf("Non lo ha fatto bene\n");
        }
    }
    
        

    //query pulita
    sqlite3_finalize(statement);
}

void insert_cocktail(char nome[], double prezzo, int quantita)
{
    char* insert= "INSERT INTO Cocktail(nome, prezzo, quantita)"
                  "VALUES(?,?,?);";

    sqlite3_stmt* statement;

    sqlite3_prepare_v2(database,insert,strlen(insert),&statement, NULL);

    sqlite3_bind_text(statement,1,nome, strlen(nome), SQLITE_TRANSIENT);
    sqlite3_bind_double(statement,2,prezzo);
    sqlite3_bind_int(statement,3,quantita);

    if(sqlite3_step(statement) == SQLITE_DONE)
    {
        printf("controlla ed esegue la query\n");
    }else printf("Non lo ha fatto bene\n");

    //query pulita
    sqlite3_finalize(statement);
}