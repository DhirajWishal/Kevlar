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
	CommandType_RequestStatus = READ_COMMAND(1, enum Status),

	// Login command.
	// This command is used to login to the system.
	CommandType_Login = WRITE_COMMAND(0, struct LoginInformation),
};