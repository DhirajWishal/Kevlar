#pragma once

#include <linux/fs.h>
#include <linux/cdev.h>
#include <linux/ioctl.h>

#include "Defines.h"

/**
 * @brief Get the current status of the driver.
 * 
 * @return Status The status of the application.
 */
enum Status get_current_status(void);

/**
 * @brief Function to handle file openings.
 * 
 * @param pNode The inode pointer.
 * @param pFile The file pointer.
 * @return int The return status.
 */
int open_file(struct inode *pNode, struct file *pFile);

/**
 * @brief Function to handle file release.
 * 
 * @param pNode The inode pointer.
 * @param pFile The file pointer.
 * @return int The return status.
 */
int release_file(struct inode *pNode, struct file *pFile);

/**
 * @brief Function to handle file read.
 * 
 * @param pFile The file pointer.
 * @param pBuffer The user content buffer.
 * @param length The length of the content.
 * @param pOffset The offsets.
 * @return ssize_t The return status.
 */
ssize_t read_file(struct file *pFile, char __user *pBuffer, size_t length, loff_t *pOffset);

/**
 * @brief Function to handle file write.
 * 
 * @param pFile The file pointer.
 * @param pBuffer The character buffer.
 * @param length The length of the buffer.
 * @param pOffset The offsets.
 * @return ssize_t The return status.
 */
ssize_t write_file(struct file *pFile, const char *pBuffer, size_t length, loff_t *pOffset);

/**
 * @brief Function to handle ioctl requests.
 * 
 * @param pFile The file pointer.
 * @param command The command sent to the driver.
 * @param argument The argument sent to the driver.
 * @return long The retrurn status.
 */
long handle_ioctl_request(struct file *pFile, unsigned int command, unsigned long argument);