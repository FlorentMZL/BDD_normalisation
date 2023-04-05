#include "table.h"

table_t* table_create() {
    table_t* table = malloc(sizeof(table_t));
    table->records = NULL;
    table->size = 0;
    return table;
}

void table_destroy(table_t* table) {
    for (size_t i = 0; i < table->size; i++) {
        record_destroy(table->records[i]);
    }
    free(table->records);
    free(table);
}

void table_add_record(table_t* table, record_t* record) {
    table->records = realloc(table->records, sizeof(record_t*) * (table->size + 1));
    table->records[table->size] = record;
    table->size++;
}

void table_print(table_t* table) {
    for (size_t i = 0; i < table->size; i++) {
        record_print(table->records[i]);
        printf("\n");
    }
}