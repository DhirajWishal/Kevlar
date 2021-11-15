#include "DataManager.h"

#include <linux/vmalloc.h>

static struct
{
	unsigned char *pDataChunk; // The data chunk.
	unsigned long mDataSize;   // The size of the chunk.
} decryptedData;

static struct DataStore dataVault = {
	.pCipherText = NULL,
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
	dataVault.pCipherText = decrypt_data(store.pCipherText, store.mDataSize);

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
	encrypt_and_store(dataVault.pCipherText, store.pCipherText, dataVault.mDataSize);

	set_current_status(Status_Successful);
}

void clear_data_manager(void)
{
	vfree(dataVault.pCipherText);

	dataVault.mDataSize = 0;
	dataVault.pCipherText = NULL;

	vfree(decryptedData.pDataChunk);

	decryptedData.pDataChunk = NULL;
	decryptedData.mDataSize = 0;
}

void add_new_entry(struct CipherEntry *pEntry)
{
	// TODO
}

void decrypt_data_block(struct DecryptInformation information)
{
	// TODO
}

unsigned long get_decrypted_data_size(void)
{
	return decryptedData.mDataSize;
}

unsigned char *get_decrypted_data(void)
{
	return decryptedData.pDataChunk;
}