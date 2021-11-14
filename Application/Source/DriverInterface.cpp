#include "DriverInterface.hpp"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>

#include <stdexcept>

#include "Source/Defines.h"

#define IOCTL_ASSERT(exp, msg) \
	if (exp < 0)               \
	throw std::runtime_error(msg)

DriverInterface::DriverInterface()
{
	// Try and open the file.
	mFileDescriptor = open("/dev/KevlarInterface", O_RDWR);

	// If the file cannot be opened, handle the issue.
	if (mFileDescriptor < 0)
	{
		// Try to insert the module. If failed, throw an exception.
		if (system("sudo insmod Driver/Builder/Kevlar.ko") != 0)
			throw std::runtime_error("Failed to insert the driver into the kernel!");

		// Try and open the ioctl file again.
		mFileDescriptor = open("/dev/KevlarInterface", O_RDWR);

		// If failed, throw an exception.
		if (mFileDescriptor < 0)
			throw std::runtime_error("Failed to open the ioctl file!");

		bIsOpenedInternally = true;
	}
}

DriverInterface::~DriverInterface()
{
	// Close the ioctl file.
	close(mFileDescriptor);

	// If the kernel module is opened internally, make sure to remove it from the driver list.
	if (bIsOpenedInternally)
		system("sudo rmmod Kevlar");
}

bool DriverInterface::IsDriverReady() const
{
	Status driverStatus = Status::Status_Unknown;
	IOCTL_ASSERT(ioctl(mFileDescriptor, CommandType::CommandType_RequestStatus, &driverStatus), "Failed to get the driver status!");

	return driverStatus == Status::Status_Ready;
}

bool DriverInterface::IsCommandSuccessful() const
{
	Status driverStatus = Status::Status_Unknown;
	IOCTL_ASSERT(ioctl(mFileDescriptor, CommandType::CommandType_RequestStatus, &driverStatus), "Failed to get the driver status!");

	return driverStatus == Status::Status_Successful || driverStatus == Status::Status_Ready;
}