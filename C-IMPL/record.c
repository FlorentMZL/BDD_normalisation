#include "record.h"

record_t* record_create() {
    record_t* record = malloc(sizeof(record_t));
    record->fields = map_create();
    return record;
}

record_t* record_from_representation(char* representation) {
    // Parse representation in the form of "{ key1 = value1, key2 = value2, ... }"
    record_t* record = record_create();
    char* representation_copy = malloc(strlen(representation) + 1);
    strcpy(representation_copy, representation);
    char** tokens = split(representation_copy, " ");
    // Skip first token, which is "{"
    for (size_t i = 1; i < strlen(representation_copy); i++) {
        
    }
    return record;
}

void record_destroy(record_t* record) {
    map_destroy(record->fields);
    free(record);
}

void record_set(record_t* record, char* key, char* value) {
    map_set(record->fields, key, value);
}

char* record_get(record_t* record, char* key) {
    return map_get(record->fields, key);
}

void record_print(record_t* record) {
    for (size_t i = 0; i < record->fields->size; i++) {
        printf("%s: %s\n", (char*) record->fields->entries[i]->key, (char*) record->fields->entries[i]->value);
    }
}

unsigned int record_equals(record_t* record1, record_t* record2) {
    if (record1->fields->size != record2->fields->size) {
        return 0;
    }
    for (size_t i = 0; i < record1->fields->size; i++) {
        if (strcmp(record1->fields->entries[i]->key, record2->fields->entries[i]->key) != 0) {
            return 0;
        }
        if (strcmp(record1->fields->entries[i]->value, record2->fields->entries[i]->value) != 0) {
            return 0;
        }
    }
    return 1;
}