#include "includes.h"
#include "Login.h"
#include "Socket.h"
#include "Database.h"




int main(){
    log_set_level(2);
    log_trace("Provo il trace");
    log_debug("Provo il debug");
    log_info("Provo l'info");
    log_warn("Provo il warn");
    log_error("Provo l'error");
    log_fatal("Provo il fatal");

    createdb_query();
    startSocket();
    //closeConnection();
}








