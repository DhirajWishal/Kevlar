#include "Interface.h"

#include <linux/kernel.h>
#include <linux/kdev_t.h>
#include <linux/device.h>
#include <linux/slab.h>
#include <linux/uaccess.h>

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
	int32_t value = 0;

	switch (command)
	{
	case WR_VALUE:
		if (copy_from_user(&value, (int32_t *)argument, sizeof(value)))
			pr_err("Data Write : Err!\n");

		pr_info("Value = %d\n", value);
		break;

	case RD_VALUE:
		if (copy_to_user((int32_t *)argument, &value, sizeof(value)))
			pr_err("Data Read : Err!\n");

		break;

	default:
		pr_info("Default\n");
		break;
	}

	return 0;
}