#include "map.h"

map_t* map_create() {
    map_t* map = malloc(sizeof(map_t));
    map->entries = NULL;
    map->size = 0;
    return map;
}

void map_destroy(map_t* map) {
    for (size_t i = 0; i < map->size; i++) {
        free(map->entries[i]->key);
        free(map->entries[i]->value);
        free(map->entries[i]);
    }
    free(map->entries);
    free(map);
}

void map_set(map_t* map, void* key, void* value) {
    for (size_t i = 0; i < map->size; i++) {
        if (map->entries[i]->key == key) {
            map->entries[i]->value = value;
            return;
        }
    }
    map->entries = realloc(map->entries, sizeof(map_entry_t*) * (map->size + 1));
    map->entries[map->size] = malloc(sizeof(map_entry_t));
    map->entries[map->size]->key = key;
    map->entries[map->size]->value = value;
    map->size++;
}

void* map_get(map_t* map, void* key) {
    for (size_t i = 0; i < map->size; i++) {
        if (strcmp(map->entries[i]->key, key) == 0) {
            return map->entries[i]->value;
        }
    }
    return NULL;
}

void map_remove(map_t* map, void* key) {
    for (size_t i = 0; i < map->size; i++) {
        if (strcmp(map->entries[i]->key, key) == 0) {
            free(map->entries[i]->key);
            free(map->entries[i]->value);
            free(map->entries[i]);
            map->entries[i] = map->entries[map->size - 1];
            map->size--;
            map->entries = realloc(map->entries, sizeof(map_entry_t*) * map->size);
            return;
        }
    }
}

int map_has(map_t* map, void* key) {
    for (size_t i = 0; i < map->size; i++) {
        if (map->entries[i]->key == key) {
            return 1;
        }
    }
    return 0;
}

void map_clear(map_t* map) {
    for (size_t i = 0; i < map->size; i++) {
        free(map->entries[i]->key);
        free(map->entries[i]->value);
        free(map->entries[i]);
    }
    free(map->entries);
    map->entries = NULL;
    map->size = 0;
}

size_t map_size(map_t* map) {
    return map->size;
}
