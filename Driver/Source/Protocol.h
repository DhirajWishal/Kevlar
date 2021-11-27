#pragma once

/**
 * @brief Kevlar driver communication protocol.
 * This file contains the drive communication protocol used by Kevlar to securely store data in the kernel space.
 *
 * Kevlar uses the IOCTL protocol to communicate with the driver (see here: https://en.wikipedia.org/wiki/Ioctl). The basic operations are storing and retrieving data.
 * This protol is not necessarily a secure protocol as multiple applications can have a connection with the IOCTL control file. Because of this, the application needs
 * to properly authenticate with the driver and that authentication is used to validate the commands sent by the client application.
 *
 * Upon requesting a connection, the driver will generate a random ID, and will be transmitted to the client application. This ID will not be generated again. From there
 * on, that ID is used to authenticate the client application, and will be used each and every time the client communicates with the driver. Since the client application is
 * responsible of managing the driver's lifetime, manually deleting the generated ID is not needed.
 */

#define WRITE_COMMAND(number, type) _IOW('a', number, type)
#define READ_COMMAND(number, type) _IOR('a', number, type)

/**
 * @brief Data strore option enum.
 * This enum specifies how to strore the incoming data block.
 */
enum DataStoreOption
{
	DataStoreOption_Set,
	DataStoreOption_Append,
};

/**
 * @brief Submit data structure.
 * This structure is used to submit data to the driver.
 */
struct SubmitData
{
	unsigned char *pDataBlock;		   // The data block containing the binary data.
	unsigned long mConnectionID;	   // The connection ID given to the application.
	unsigned long mSize;			   // The size of the data block.
	enum DataStoreOption mStoreOption; // The data store option.
};

/**
 * @brief Request data size structure.
 * This structure is used to get the internally stored data block size.
 */
struct RequestDataSize
{
	unsigned long mConnectionID; // The connection ID of the application.
	unsigned long mSize;		 // The internal data block size.
};

/**
 * @brief Request data structure.
 * This structure is used to request data from the driver.
 *
 * Make sure that the offset + size does not go beyond the internal data block size.
 */
struct RequestData
{
	unsigned char *pDataBlock;	 // The data block to copy the data to.
	unsigned long mConnectionID; // The connection ID used to authenticate the client application.
	unsigned long mSize;		 // The size of data.
	unsigned long mOffset;		 // The data offset.
};

/**
 * @brief Command type enum.
 * This enum contains all the supported commands in the Kevlar driver protocol.
 */
enum CommandType
{
	// Setup a new connection with the deriver.
	CommandType_SetupConnection = WRITE_COMMAND(0, unsigned long),

	// Terminate the driver session.
	// This will make sure that the internal content is deallocated and will be ready to exit.
	CommandType_TerminateSession = WRITE_COMMAND(1, unsigned long),

	// Submit data command.
	// This command will submit data to the driver.
	CommandType_SubmitData = WRITE_COMMAND(2. struct SubmitData),

	// Request the total internally stored data size.
	CommandType_RequestDataSize = READ_COMMAND(3, struct RequestDataSize),

	// Request a block of data from the driver.
	CommandType_RequestData = READ_COMMAND(4, struct RequestData)
};