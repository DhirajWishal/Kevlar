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
};