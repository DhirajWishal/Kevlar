#pragma once

/**
 * @brief Cryptographic output structure.
 * This structure contains information about a single cryptographic output.
 */
struct CryptographicOutput
{
	unsigned char *pOutput;	   // The output data.
	unsigned long mDataLength; // The data length.
};

/**
 * @brief Symmetric input structure.
 * This structure is used to submit information for a symmetric cryptography function.
 */
struct SymmetricInput
{
	unsigned char pKey[64];
	unsigned char *pInputData;
	unsigned long mInputLength;
};

/**
 * @brief Encrypt a block of data using the cbc-aes-aesni instruction set.
 * 
 * @param input The input data used for encryption.
 * @return struct CryptographicOutput The encrypted data.
 */
struct CryptographicOutput encrypt_data_symmetric(struct SymmetricInput input);

/**
 * @brief Decrypt a block of data using the cbc-aes-aesni instruction set.
 * 
 * @param input The input data used for decryption.
 * @return struct CryptographicOutput The decrypted data.
 */
struct CryptographicOutput decrypt_data_symmetric(struct SymmetricInput input);