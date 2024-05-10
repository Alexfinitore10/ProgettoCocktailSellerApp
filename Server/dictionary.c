#include "dictionary.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Funzione per creare un nuovo nodo
Node *createNode(int key, const char *value) {
  Node *newNode = (Node *)malloc(sizeof(Node));
  newNode->key = key;
  newNode->value = strdup(value);
  newNode->next = NULL;
  return newNode;
}

// Funzione per creare un nuovo dizionario vuoto
Dictionary *create_dictionary() {
  Dictionary *dict = (Dictionary *)malloc(sizeof(Dictionary));
  dict->head = NULL;
  return dict;
}

// Funzione per inserire una coppia chiave-valore nel dizionario
void insert_dictionary(Dictionary *dict, int key, const char *value) {
  Node *newNode = createNode(key, value);
  if (dict->head == NULL) {
    dict->head = newNode;
  } else {
    Node *current = dict->head;
    while (current->next != NULL) {
      current = current->next;
    }
    current->next = newNode;
  }
}

// Funzione per cercare un valore dato una chiave nel dizionario
const char *search_dictionary(Dictionary *dict, int key) {
  Node *current = dict->head;
  while (current != NULL) {
    if (current->key == key) {
      return current->value;
    }
    current = current->next;
  }
  return NULL; // Ritorna NULL se la chiave non è stata trovata
}

// Funzione per rimuovere una coppia chiave-valore dal dizionario
void remove_dictionary(Dictionary *dict, int key) {
  Node *current = dict->head;
  Node *prev = NULL;

  // Cerca la chiave da rimuovere
  while (current != NULL && current->key != key) {
    prev = current;
    current = current->next;
  }

  // Se la chiave è stata trovata, rimuovi il nodo corrispondente
  if (current != NULL) {
    if (prev == NULL) {
      // Se il nodo da rimuovere è il primo nella lista
      dict->head = current->next;
    } else {
      // Se il nodo da rimuovere non è il primo nella lista
      prev->next = current->next;
    }
    free(current->value);
    free(current);
  }
}

// Funzione per deallocare la memoria utilizzata dal dizionario
void free_dictionary(Dictionary *dict) {
  Node *current = dict->head;
  while (current != NULL) {
    Node *next = current->next;
    free(current->value);
    free(current);
    current = next;
  }
  free(dict);
}
