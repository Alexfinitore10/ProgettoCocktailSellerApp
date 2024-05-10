#ifndef DICTIONARY_H
#define DICTIONARY_H

// Definizione della struttura per il nodo della lista collegata
typedef struct Node {
  int key;
  char *value;
  struct Node *next;
} Node;

// Definizione del tipo Dictionary come un puntatore al primo nodo della lista
typedef struct {
  Node *head;
} Dictionary;

// Funzione per creare un nuovo dizionario vuoto
Dictionary *create_dictionary();

// Funzione per inserire una coppia chiave-valore nel dizionario
void insert_dictionary(Dictionary *dict, int key, const char *value);

// Funzione per cercare un valore dato una chiave nel dizionario
const char *search_dictionary(Dictionary *dict, int key);

// Funzione per rimuovere una coppia chiave-valore dal dizionario
void remove_dictionary(Dictionary *dict, int key);

// Funzione per deallocare la memoria utilizzata dal dizionario
void free_dictionary(Dictionary *dict);

#endif /* DICTIONARY_H */
