#include "includes.h"


void createdb_query();
void get_all_cocktails();
void reduce_amount(char *, int);
void signup();
void signin();
void checkres(PGresult*);
void command(char *comando);
bool testingConnection(PGconn* conn);
void insert_cocktail(char[], char [], double, double, int);
void close_connection();