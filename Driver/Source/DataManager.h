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
 * @brief Add a new ciphertext entry.
 *
 * @param pEntry The entry pointer.
 */
void add_new_entry(struct CipherEntry *pEntry);

/**
 * @brief Decrypt a block of data from the internal store.
 *
 * @param information The decryption information.
 */
void decrypt_data_block(struct DecryptInformation information);

/**
 * @brief Get the decrypted data size from the decrypted pool.
 *
 * @return unsigned long The size in bytes.
 */
unsigned long get_decrypted_data_size(void);

/**
 * @brief Copy the decrypted data to a pointer.
 *
 * @param pStore The pointer to copy the data to.
 */
void copy_decrypted_data(unsigned char *pStore);

/**
 * @brief Get the decrypted data pointer.
 *
 * @return unsigned char* The data pointer.
 */
unsigned char *get_decrypted_data(void);