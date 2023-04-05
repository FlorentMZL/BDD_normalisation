#include "table.h"

int main() {
    table_t* table = table_create();
    record_t* record = record_create();
    char* key = malloc(sizeof(char) * 5);
    strcpy(key, "name");
    char* name = malloc(sizeof(char) * 5);
    strcpy(name, "John");
    record_set(record, key, name);
    table_add_record(table, record);
    table_print(table);
    table_destroy(table);
    return 0;
}