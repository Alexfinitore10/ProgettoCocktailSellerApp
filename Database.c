#include "Database.h"

PGconn *conn;
PGresult *res;
char *feedback = ""; 
char *error_response = "";
char *firstdbcommand = "CREATE TABLE IF NOT EXISTS Cliente(id INTEGER PRIMARY KEY, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL);\
                        CREATE TABLE IF NOT EXISTS Cocktail(nome VARCHAR(255) PRIMARY KEY, ingredienti VARCHAR(1000) NOT NULL, gradazione_alcolica DOUBLE PRECISION , prezzo DOUBLE PRECISION , quantita INTEGER);\
                        CREATE TABLE IF NOT EXISTS Frullato(id INTEGER PRIMARY KEY, nome VARCHAR(255) NOT NULL, ingredienti VARCHAR(1000) NOT NULL, gusto VARCHAR(10) NOT NULL);\
                        CREATE TABLE IF NOT EXISTS Vendite (\
                        id INTEGER PRIMARY KEY,\
                        cliente_id INTEGER,\
                        cocktail_id VARCHAR(255) ,\
                        CONSTRAINT cliente_fk FOREIGN KEY (cliente_id) REFERENCES Cliente (id),\
                        CONSTRAINT cocktail_fk FOREIGN KEY (cocktail_id) REFERENCES Cocktail (nome));";

void createdb_query(){

    //Provo a connettermi al Database
    while (testingConnection() == false){
        printf("La connessione non e' effettuabile, riprovo tra 5 secondi...\n");
        sleep(5);
    }
    //primo comando da eseguire
    printf("Eseguo la query di costruzione del database\n");
    
    if (command(firstdbcommand)){
        printf("Popolo il database...\n");
        cocktail_population();
    }

    
    


}

void cocktail_population(){
    insert_cocktail("Mojito", "Rum, Lime, Zucchero, Menta", 18.0, 6.0, 10);
    insert_cocktail("Bloody Mary", "Vodka, Succo di pomodoro, Tabasco , Sedano , Sale , Pepe nero , Succo di limone , Salsa Worchestershire", 25.0, 6.0, 13);
    insert_cocktail("White Russian", "Vodka, Liquore al caffè, Ghiaccio , Panna fresca", 25.0, 7.0, 16);
    insert_cocktail("Negroni", "Ghiaccio, Gin, Bitter Campari, Vermut Rosso", 28.0, 5.90, 10);
    insert_cocktail("Daquiri", "Rum, Succo di lime, zucchero, ghiaccio, gocce di maraschino", 18.9, 6.44, 10);
    insert_cocktail("Dry Martini", "Gin, Scorza di Limone, Vermut Dry, Ghiaccio", 14.4, 7.90, 10);
    insert_cocktail("Maragarita", "Succo di Lime, Ghiaccio, Triple Sec, Tequila", 25.4, 6.44, 10);
    insert_cocktail("Manhattan", "rye wisky, vermout roso, gocce di Angostura, buccia di arancia, ghiaccio, ciliegina al Maraschino", 30.0, 5.90, 10);
    insert_cocktail("Whiskey Sour", "Whisky, Succo di limone, sciroppo di zucchero, albume", 23.0, 4.80, 10);
}





bool testingConnection(){
    conn = PQconnectdb("dbname = dbcocktail user = postgres password = postgres host = localhost port = 5432");
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


bool command(char *comando){
    bool status = false;
    res = PQexec(conn, comando);
    if (checkres(res)){
        return true;
    }else return false;
}


void reduce_amount(char *nome, int quantita){

    if(is_drink_in_db(nome) == false){
        printf("Il cocktail %s non e' presente nel database\n", nome);
        return;
    }

    if(get_cocktail_amount(nome) < quantita){
        printf("Il cocktail %s non e' disponibile\n", nome);
        return;
    }

    char *reduce_amount_command = "UPDATE Cocktail SET quantita = quantita - $1 WHERE nome = $2";

    char quantita_string[100];

    sprintf(quantita_string, "%d", quantita);

    const char *paramValues[2] = {quantita_string, nome};

    int paramLengths[2] = {strlen(quantita_string), strlen(nome)};

    int paramFormats[2] = {0, 0};

    PQexecParams(conn, reduce_amount_command, 2, NULL, paramValues, paramLengths, paramFormats, 0);

    
}


bool checkres(PGresult* res){
    char *risultato;
    ExecStatusType ris;
    bool status = false;
    ris = PQresultStatus(res);
    switch (ris)
    {
    case PGRES_COMMAND_OK:
        risultato = "Query completata\n";
        status = true;
        break;
    case PGRES_EMPTY_QUERY:
        risultato = "Query vuota\n";
        break;
    case PGRES_TUPLES_OK:
        risultato = "Query completata con ritorno di dati\n";
        status = true;
        break;
    case PGRES_FATAL_ERROR:
        risultato = "Errore fatale\n";
        break;
    default:
        risultato = "Operazione non andata a buon fine\n";
        break;
    }
    error_response = PQresultErrorMessage(res);
    printf("%s: %s",risultato, error_response);
    return status;
}


void insert_cocktail(char nome[], char ingredienti[], double gradazione_alcolica ,double prezzo, int quantita){
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


void get_all_cocktails(){
    char *get_all_cocktail_command = "SELECT * FROM Cocktail";

    command(get_all_cocktail_command);
}

int get_cocktail_amount(char * nome){

    char *get_cocktail_amount_command = "SELECT quantita FROM Cocktail WHERE nome = $1";

    const char *paramValues[1] = {nome};

    int paramLengths[1] = {strlen(nome)};

    int paramFormats[1] = {0};

    res = PQexecParams(conn, get_cocktail_amount_command, 1, NULL, paramValues, paramLengths, paramFormats, 0);

    checkres(res);

    int quantita = atoi(PQgetvalue(res, 0, 0));

    return quantita;
}


bool is_drink_in_db(char * nome){
    char *is_drink_in_db_command = "SELECT nome FROM Cocktail WHERE nome = $1";

    const char *paramValues[1] = {nome};

    int paramLengths[1] = {strlen(nome)};

    int paramFormats[1] = {0};

    res = PQexecParams(conn, is_drink_in_db_command, 1, NULL, paramValues, paramLengths, paramFormats, 0);

    if (PQntuples(res) == 0){
        return false;
    }
    else{
        return true;
    }
}


void signup(){
    //TODO
}



void signin(){
    //TODO
}

void close_connection(){
    PQfinish(conn);
}

