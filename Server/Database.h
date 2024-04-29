#include "includes.h"

//Generics
void createdb_query();
void cocktail_and_shake_population();
char * printQuery(PGresult *);
void close_connection();
//Commands
bool checkres(PGresult*);
bool command(char *);
bool testingConnection();
//Gets
char * get_all_cocktails();
char * get_all_shakes();
int get_cocktail_amount(char *);
int get_shake_amount(char *);
int get_id_vendita();
//Inserts
bool reduce_amount_cocktail(char *, int);//Vorrei cambiarla in Bool
bool reduce_amount_shake(char *, int);//Vorrei cambiarla in Bool
void insert_cocktail(char[], char [], double, double, int);
void insert_shake(char [], char [], double, int);
//Checks
bool is_cliente_in_db(char *);
bool is_drink_in_db(char *);
bool is_shake_in_db(char *);
bool are_credentials_correct(char *, char *);
//SignIns
bool signup(char *,char *);
bool signin(char *, char *);
//Sells
bool create_sell(char *, char *);
