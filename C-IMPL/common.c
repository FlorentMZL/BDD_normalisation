#include "common.h"


char** split(char* string, char* delimiter) {
    char** result = NULL;
    char* token = strtok(string, delimiter);
    size_t size = 0;
    while (token != NULL) {
        result = realloc(result, sizeof(char*) * ++size);
        result[size - 1] = token;
        token = strtok(NULL, delimiter);
    }
    result = realloc(result, sizeof(char*) * (size + 1));
    result[size] = NULL;
    return result;
}