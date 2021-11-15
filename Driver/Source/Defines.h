#pragma once

#define WRITE_COMMAND(number, type) _IOW('a', number, type)
#define READ_COMMAND(number, type) _IOR('a', number, type)

#define MAX_KEY_LENGTH (512 / 8)

/**
 * @brief Login information structure.
 * This structure contains information to setup a new connection.
 */
struct LoginInformation
{
	unsigned char *pKey;	 // The key value.
	unsigned int mKeyLength; // The key length in bits.
};

/**
 * @brief Cipher chunk structure.
 * Since we are just storing the ciphertext in the driver side, we identify a single key string using its offset and length,
 * This structure provide the means of identifying a single chunk of ciphertext.
 */
struct CipherChunk
{
	unsigned long mOffset; // The offset in the ciphertext stream.
	unsigned long mLength; // The length of the ciphertext.
};

/**
 * @brief Cipher entry structure.
 * This structure is used to submit ciphertext to the driver.
 */
struct CipherEntry
{
	struct CipherChunk mResultChunk; // The resulting ciphertext chunk.
	unsigned char *pCipherText;		 // The ciphertext to append.
	unsigned long mDataLength;		 // The length of the ciphertext.
};

/**
 * @brief Decrypt information structure.
 * This structure contains information required to decrypt a single key stream.
 */
struct DecryptInformation
{
	struct CipherChunk mChunkInfo;				  // The chunk to decrypt.
	unsigned char mDecryptionKey[MAX_KEY_LENGTH]; // The decipher key.
};

/**
 * @brief Data strore structure.
 * This structure holds information about the encrypted key block, and the data entries.
 */
struct DataStore
{
	unsigned char *pCipherText; // The data pointer.
	unsigned long mDataSize;	// The size of the data.
};

/**
 * @brief Status enum.
 * This enum contains all the status information.
 */
enum Status
{
	Status_Unknown,
	Status_Ready,
	Status_Successful,
	Status_Unsuccessful,
};

/**
 * @brief Command type enum.
 * This enum contains all the commands supported by Kevlar and is used for ioctl communication.
 */
enum CommandType
{
	// Request status command.
	// This command is used to get the status of the driver.
	CommandType_RequestStatus = READ_COMMAND(0, enum Status),

	// Login command.
	// This command is used to login to the system.
	CommandType_Login = WRITE_COMMAND(1, struct LoginInformation),

	// Submit data store command.
	// This command is used to submit the data pointer to the driver.
	CommandType_SubmitDataStore = WRITE_COMMAND(2, struct DataStore),

	// Request for the data size.
	// This command is used to request for the total ciphertext size.
	CommandType_RequestDataSize = READ_COMMAND(3, unsigned long),

	// Request data store from the driver command.
	// Since the driver does not handle file I/O, all the ciphertext storage must be handled by the client application.
	// This command is required to get back the ciphertext and store in a file. Upon driver shutdown, it automatically deallocates the stored
	// memory block used for ciphertext.
	CommandType_RequestDataStore = READ_COMMAND(4, struct DataStore),

	// Submit a new chunk of data to the driver.
	// After including the new ciphertext, the resulting chunk's size will be set to the incoming argument.
	CommandType_SubmitNewEntry = WRITE_COMMAND(5, struct CipherEntry *),

	// Decrypt command.
	// Command the driver to decrypt a chunk of data.
	CommandType_Decrypt = WRITE_COMMAND(6, struct DecryptInformation),

	// Request for the decrypted data size.
	CommandType_RequestDecryptedDataSize = READ_COMMAND(7, unsigned long),

	// Request the decrypted data.
	// If the data was successfully decrypted, the data will be copied to the provided pointer.
	CommandType_RequestDecryptedData = READ_COMMAND(8, unsigned char *),
};