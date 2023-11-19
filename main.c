#include "includes.h"
#include "Login.h"
#include "Socket.h"
#include "Database.h"


void dbconnection(){
    createdb_query();
}



int main(){
    dbconnection();

    close_connection();
}








