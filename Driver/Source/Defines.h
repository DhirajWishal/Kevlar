#pragma once

#define WRITE_COMMAND(number, type) _IOW('a', number, type)
#define READ_COMMAND(number, type) _IOR('a', number, type)

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
 * @brief Data strore structure.
 * This structure holds information about a single encrypted data pointer. This pointer is provided to the driver by the
 * client (usermode) application as the driver itself doesn't do any file I/O.
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
};