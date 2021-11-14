#include "Interface.h"

#include <linux/kdev_t.h>
#include <linux/slab.h>
#include <linux/uaccess.h>

enum Status currentStatus = Status_Ready;

enum Status get_current_status(void)
{
	return currentStatus;
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

	default:
		pr_info("Default\n");
		break;
	}

	return 0;
}