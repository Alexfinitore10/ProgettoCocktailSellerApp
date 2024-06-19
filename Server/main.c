#include "Database.h"
#include "Login.h"
#include "Socket.h"
#include "includes.h"
#include <signal.h>
#include <sys/types.h>

void signal_handler();

int main() {
  signal(SIGINT, signal_handler);
  log_set_level(0);
  /* log_trace("Provo il trace");
  log_debug("Provo il debug");
  log_info("Provo l'info");
  log_warn("Provo il warn");
  log_error("Provo l'error");
  log_fatal("Provo il fatal"); */

  createdb_query();
  startSocket();
  // closeConnection();
}

void signal_handler() {
  log_info("Chiusura db in corso...");
  close_connection();
  free_dic();
  exit(0);
}
