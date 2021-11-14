#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/module.h>
#include <linux/kdev_t.h>
#include <linux/device.h>

#include "DataManager.h"

dev_t device = 0;
static struct cdev characterDevice = {};
static struct class *pDeviceClass = NULL;

// Setup the file operations.
static struct file_operations fileOperations = {
	.owner = THIS_MODULE,
	.read = read_file,
	.write = write_file,
	.open = open_file,
	.unlocked_ioctl = handle_ioctl_request,
	.release = release_file,
};

/**
 * @brief Initialization function used to initialize the driver.
 * 
 * @return int The return status.
 */
static int initialize(void)
{
	pr_info("Initializing Kevlar...\n");

	// Allocating major number.
	if (alloc_chrdev_region(&device, 0, 1, "Kevlar") < 0)
	{
		pr_err("Failed to allocate device major number!\n");
		return -1;
	}

	// Initializing the character device structure.
	cdev_init(&characterDevice, &fileOperations);

	// Adding the character device to the system.
	if (cdev_add(&characterDevice, device, 1) < 0)
	{
		pr_err("Failed to add the character device to the system\n");

		unregister_chrdev_region(device, 1);
		return -1;
	}

	// Creating the struct class.
	if ((pDeviceClass = class_create(THIS_MODULE, "KevlarClass")) == NULL)
	{
		pr_err("Failed to create the struct class\n");

		unregister_chrdev_region(device, 1);
		return -1;
	}

	// Creating the interface device.
	if (device_create(pDeviceClass, NULL, device, NULL, "KevlarInterface") == NULL)
	{
		pr_err("Failed to create the Device 1\n");

		class_destroy(pDeviceClass);
		return -1;
	}

	pr_info("Kevlar initialized!\n");
	return 0;
}

/**
 * @brief Terminate function used to terminate the driver.
 */
static void terminate(void)
{
	pr_info("Terminating Kevlar...\n");

	// Make sure to clear the data manager.
	clear_data_manager();

	device_destroy(pDeviceClass, device);
	class_destroy(pDeviceClass);
	cdev_del(&characterDevice);
	unregister_chrdev_region(device, 1);

	pr_info("Terminated Kevlar!\n");
}

module_init(initialize);
module_exit(terminate);

MODULE_AUTHOR("Dhiraj Wishal");
MODULE_VERSION("1.0");
MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("Secure driver to store password information");