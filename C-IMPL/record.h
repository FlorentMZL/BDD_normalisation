#ifndef __RECORD_H__
#define __RECORD_H__

#include "map.h"

typedef struct record {
    map_t* fields;
} record_t;

record_t* record_create();
record_t* record_from_representation(char* representation);
void record_destroy(record_t* record);
void record_set(record_t* record, char* key, char* value);
char* record_get(record_t* record, char* key);
void record_print(record_t* record);
unsigned int record_equals(record_t* record1, record_t* record2);

#endif