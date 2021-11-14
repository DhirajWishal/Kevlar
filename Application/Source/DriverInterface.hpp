#pragma once

/**
 * @brief Driver interface object.
 * This class is used to create a connection between the kernel driver in order to access and store data safely.
 */
class DriverInterface final
{
	int mFileDescriptor = 0;
	bool bIsOpenedInternally = false;

public:
	/**
	 * @brief Construct a new Driver Interface object.
	 * The constructor also can insert the driver if not already available.
	 */
	DriverInterface();

	/**
	 * @brief Destroy the Driver Interface object.
	 */
	~DriverInterface();

	/**
	 * @brief Check if the driver is ready.
	 * This is generally checked as soon as the connection is made.
	 * 
	 * @return true If the driver is ready.
	 * @return false If the driver is not ready, typically because of a connection issue.
	 */
	bool IsDriverReady() const;

	/**
	 * @brief Check if the command was executed successfully.
	 * 
	 * @return true If the command was successfully executed by the driver.
	 * @return false If the command failed.
	 */
	bool IsCommandSuccessful() const;
};