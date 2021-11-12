#include <linux/init.h>
#include <linux/module.h>
MODULE_LICENSE("Apache 2.0");

static int initialize(void)
{
    pr_alert("Initializing Kevlar!\n");
    return 0;
}

static void terminate(void)
{
    pr_alert("Terminating Kevlar!\n");
}

module_init(initialize);
module_exit(terminate);