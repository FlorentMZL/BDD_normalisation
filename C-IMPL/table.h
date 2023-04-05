#ifndef __TABLE_H__
#define __TABLE_H__

#include "record.h"

typedef struct table {
    record_t** records;
    size_t size;
} table_t;

table_t* table_create();
void table_destroy(table_t* table);
void table_add_record(table_t* table, record_t* record);
void table_print(table_t* table);

#endif