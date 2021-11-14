#include "DataManager.h"

#include <linux/kdev_t.h>
#include <linux/slab.h>
#include <linux/uaccess.h>

static enum Status currentStatus = Status_Ready;

enum Status get_current_status(void)
{
	return currentStatus;
}

void set_current_status(enum Status status)
{
	currentStatus = status;
}

int open_file(struct inode *pNode, struct file *pFile)
{
	return 0;
}

int release_file(struct inode *pNode, struct file *pFile)
{
	return 0;
}

ssize_t read_file(struct file *pFile, char __user *pBuffer, size_t length, loff_t *pOffset)
{
	return 0;
}

ssize_t write_file(struct file *pFile, const char *pBuffer, size_t length, loff_t *pOffset)
{
	return length;
}

long handle_ioctl_request(struct file *pFile, unsigned int command, unsigned long argument)
{
	switch (command)
	{
	case CommandType_RequestStatus:
		if (copy_to_user((enum Status *)argument, &currentStatus, sizeof(enum Status)))
			pr_err("Failed to send the status to the user!\n");
		break;

	case CommandType_Login:
		break;

	case CommandType_SubmitDataStore:
	{
		struct DataStore store = {};
		if (copy_from_user(&store, (struct DataStore *)argument, sizeof(struct DataStore)))
			pr_err("Failed load the data store from user!\n");
		else
			load_from_data_store(store);
	}
	break;

	case CommandType_RequestDataSize:
	{
		unsigned long size = get_data_size();
		if (copy_to_user((unsigned long *)argument, &size, sizeof(unsigned long)))
			pr_err("Failed to send the data size to the user!\n");
		break;
	}
	break;

	case CommandType_RequestDataStore:
	{
		struct DataStore store = {};
		if (copy_from_user(&store, (struct DataStore *)argument, sizeof(struct DataStore)))
			pr_err("Failed load the data store from user!\n");
		else
			store_to_data_store(store);
	}
	break;

	default:
		pr_info("Default\n");
		break;
	}

	return 0;
}