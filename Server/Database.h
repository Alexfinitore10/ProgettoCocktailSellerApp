#include "includes.h"


void createdb_query();
char * get_all_cocktails();
char * get_all_shakes();
bool is_drink_in_db(char *);
bool is_shake_in_db(char *);
int get_cocktail_amount(char *);
int get_shake_amount(char *);
int get_id_vendita();
void reduce_amount_cocktail(char *, int);
void reduce_amount_shake(char *, int);
bool signup(char *,char *);
bool signin(char *, char *);
bool is_cliente_in_db(char *);
bool are_credentials_correct(char *, char *);
bool checkres(PGresult*);
bool command(char *);
bool testingConnection();
void insert_cocktail(char[], char [], double, double, int);
void insert_shake(char [], char [], double, int);
void close_connection();
void cocktail_and_shake_population();
bool create_sell(char *, char *);
char * printQuery(PGresult *);