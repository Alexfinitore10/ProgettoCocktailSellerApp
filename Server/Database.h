#include "includes.h"


void createdb_query();
char * get_all_cocktails();
bool is_drink_in_db(char *);
int get_cocktail_amount(char *);
int get_id_vendita();
void reduce_amount(char *, int);
bool signup(char *,char *);
bool signin(char *, char *);
bool is_cliente_in_db(char *);
bool are_credentials_correct(char *, char *);
bool checkres(PGresult*);
bool command(char *comando);
bool testingConnection();
void insert_cocktail(char[], char [], double, double, int);
void close_connection();
void cocktail_population();
bool create_sell(char *, char *);
char * printQuery(PGresult *);