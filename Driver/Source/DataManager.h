#pragma once

#include "Interface.h"

/**
 * @brief Load ciphertext data from the client's data store.
 * 
 * @param store The data store to load the data from.
 */
void load_from_data_store(const struct DataStore store);

/**
 * @brief Get the data size of the ciphertext.
 * 
 * @return unsigned long The size in bytes.
 */
unsigned long get_data_size(void);

/**
 * @brief Store the ciphertext data in a client data store.
 * 
 * @param store The data store to store the data.
 */
void store_to_data_store(struct DataStore store);

/**
 * @brief Clear all the data stored in the vault.
 * This function is a must to make sure that we dont have data leaks. But make sure that all of its content is safe before doing this.
 */
void clear_data_manager(void);

/**
 * @brief Get the entry count.
 * 
 * @return unsigned long The number of entries stored.
 */
unsigned long get_entry_count(void);

/**
 * @brief Get all the stored entry objects.
 * 
 * @return struct DataEntry* The entries.
 */
struct DataEntry* get_entries(void);

/**
 * @brief Add a new entry to the list.
 * 
 * @param entry The new entry to add.
 */
void add_new_entry(struct NewEntry entry);