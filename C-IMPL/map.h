#ifndef __MAP_H__
#define __MAP_H__

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct map_entry {
    void* key;
    void* value;
} map_entry_t;

typedef struct map {
    map_entry_t** entries;
    size_t size;
} map_t;

map_t* map_create();
void map_destroy(map_t* map);
void map_set(map_t* map, void* key, void* value);
void* map_get(map_t* map, void* key);
void map_remove(map_t* map, void* key);
int map_has(map_t* map, void* key);
void map_clear(map_t* map);
size_t map_size(map_t* map);

#endif