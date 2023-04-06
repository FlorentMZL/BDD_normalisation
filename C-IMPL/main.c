#include "table.h"

int main() {
    table_t* table = table_create();
    record_t* record = record_from_representation("{ [name]: [John], [age]: [21] }");
    table_add_record(table, record);
    table_print(table);
    table_destroy(table);
    return 0;
}