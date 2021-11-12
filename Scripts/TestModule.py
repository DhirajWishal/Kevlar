# Script to run the application.

import os

os.system("sudo insmod ./Driver/Builder/Kevlar.ko")
os.system("sudo rmmod Kevlar")
os.system("sudo dmesg -k --level=alert")