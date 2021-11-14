#include "DataManager.h"

#include <linux/vmalloc.h>

/**
 * @brief The vault structure.
 * This structure holds information about the ciphertext.
 */
struct Vault
{
	unsigned char *pCipherText;
	unsigned long mDataSize;
};

static struct Vault dataVault = {
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
		pr_err("Failed to allocate buffer pointer.");
		return NULL;
	}

	// Check if we copied the data from the usermode to the kernelmode memory.
	if (copy_from_user(pBuffer, pInputData, size))
	{
		pr_err("Failed to copy buffer content.");
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
		pr_err("Failed to copy buffer content.");
		return;
	} // TODO
}

void load_from_data_store(const struct DataStore store)
{
	pr_info("Loading store data...");
	dataVault.mDataSize = store.mDataSize;
	dataVault.pCipherText = decrypt_data(store.pCipherText, store.mDataSize);

	set_current_status(Status_Successful);
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
}