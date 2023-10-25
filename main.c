#include "includes.h"
#include "Login.h"
#include "Socket.h"
#include "Database.h"


void dbconnection(){
    createdb_query();
}



int main(){

    //chiamo le funzioni per connettermi al database
    dbconnection();    
    //chiamare la funzione di creazione socket nell'altro file.


    close_connection();
}








