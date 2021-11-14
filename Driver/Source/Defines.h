#pragma once

#define WRITE_COMMAND(number, type) _IOW('a', number, type)
#define READ_COMMAND(number, type) _IOR('a', number, type)

#define MAX_TITLE_LENGTH 20
#define MAX_DESCRIPTION_LENGTH 256

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
 * @brief Data entry structure.
 * This structure holds information about a single entry.
 * 
 * We store all the keys in a single block of data. Because of this, its much more easier to index and use as we reduce the amount of cache misses and we can
 * directly index everyting using a single block, which also makes it easier to store and retrieve them.
 * 
 * We also allow for custom encryption keys, where the user can specify a key for a single entry.
 */
struct DataEntry
{
	char mTitle[MAX_TITLE_LENGTH];			   // The title of the entry.
	char mDescription[MAX_DESCRIPTION_LENGTH]; // The data description.
	unsigned char mKeyLength;				   // The key length.
	unsigned char mKeyOffset;				   // The key offset in the key data block.
	bool bHasCustomKey;						   // State whether here we use a custom key.
};

/**
 * @brief Data strore structure.
 * This structure holds information about the encrypted key block, and the data entries.
 */
struct DataStore
{
	struct DataEntry *pDataEntries; // The stored data entries.
	unsigned char *pCipherText;		// The data pointer.

	unsigned long mDataEntryCount; // The number of data entries stored.
	unsigned long mDataSize;	   // The size of the data.
};

/**
 * @brief New entry structure.
 * This structure contains information about a new entry.
 */
struct NewEntry
{
	struct DataEntry mEntry;  // The entry information.
	unsigned char *pKeyData;  // The key data.
	unsigned long mKeyLength; // The length of the key.
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

	// Request the number of entries in the data store.
	CommandType_RequestEntryCount = READ_COMMAND(5, unsigned long),

	// Request the data entries.
	// This will copy all the stored data entries to a user provided pointer.
	CommandType_RequestEntries = READ_COMMAND(6, struct DataEntry *),

	// Submit new entry command.
	// This command is used to submit a new entry to the driver.
	CommandType_SubmitNewEntry = WRITE_COMMAND(7, struct NewEntry),
};