#include "DataManager.h"

#include <linux/vmalloc.h>

/**
 * @brief The vault structure.
 * This structure holds information about the ciphertext.
 */

static struct DataStore dataVault = {
	.pDataEntries = NULL,
	.pCipherText = NULL,
	.mDataEntryCount = 0,
	.mDataSize = 0,
};

/**
 * @brief This function is used to decrypt the data.
 *
 * @param pInputData The data to be decrypted.
 * @return unsigned char* The decrypted data.
 */
unsigned char *decrypt_data(const unsigned char *pInputData, const unsigned long size)
{
	unsigned char *pBuffer = (unsigned char *)vmalloc(size);

	// Check if the buffer was allocated.
	if (pBuffer == NULL)
	{
		pr_err("Failed to allocate buffer pointer.\n");
		return NULL;
	}

	// Check if we copied the data from the usermode to the kernelmode memory.
	if (copy_from_user(pBuffer, pInputData, size))
	{
		pr_err("Failed to copy buffer content.\n");
		return NULL;
	}

	return pBuffer; // TODO
}

/**
 * @brief This function is used to encrypt the data.
 *
 * @param pInputData The data to be encrypted.
 * @param pOutput The encrypted ciphertext.
 */
void encrypt_and_store(const unsigned char *pInputData, unsigned char *pOutput, const unsigned long size)
{
	// Check if the data was copied to the usermode.
	if (copy_to_user(pOutput, pInputData, size))
	{
		pr_err("Failed to copy buffer content.\n");
		return;
	} // TODO
}

void load_from_data_store(const struct DataStore store)
{
	pr_info("Loading store data...");
	dataVault.mDataSize = store.mDataSize;
	dataVault.mDataEntryCount = store.mDataEntryCount;

	dataVault.pCipherText = decrypt_data(store.pCipherText, store.mDataSize);
	dataVault.pDataEntries = (struct DataEntry *)vmalloc(sizeof(struct DataEntry) * dataVault.mDataEntryCount);

	// Check if the copy was successful.
	if (copy_from_user(dataVault.pDataEntries, store.pDataEntries, sizeof(struct DataEntry) * dataVault.mDataEntryCount))
	{
		pr_err("Failed to copy from the user!\n");
		set_current_status(Status_Unsuccessful);

		return;
	}

	if (dataVault.pCipherText != NULL)
		set_current_status(Status_Successful);
	else
		set_current_status(Status_Unsuccessful);
}

unsigned long get_data_size(void)
{
	set_current_status(Status_Successful);
	return dataVault.mDataSize;
}

void store_to_data_store(struct DataStore store)
{
	store.mDataSize = dataVault.mDataSize;
	store.mDataEntryCount = dataVault.mDataEntryCount;
	encrypt_and_store(dataVault.pCipherText, store.pCipherText, dataVault.mDataSize);

	if (copy_to_user(store.pDataEntries, dataVault.pDataEntries, sizeof(struct DataEntry) * dataVault.mDataEntryCount))
	{
		pr_err("Failed to copy data to the user!\n");
		set_current_status(Status_Unsuccessful);

		return;
	}

	set_current_status(Status_Successful);
}

void clear_data_manager(void)
{
	vfree(dataVault.pCipherText);
	vfree(dataVault.pDataEntries);

	dataVault.mDataEntryCount = 0;
	dataVault.mDataSize = 0;
	dataVault.pDataEntries = NULL;
	dataVault.pCipherText = NULL;
}

unsigned long get_entry_count(void)
{
	return dataVault.mDataEntryCount;
}

struct DataEntry *get_entries(void)
{
	return dataVault.pDataEntries;
}

void add_new_entry(struct NewEntry entry)
{
	// TODO
}