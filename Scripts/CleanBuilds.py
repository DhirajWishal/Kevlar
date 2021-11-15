# Script to quickly clean the builds.

import os

# Clean the build directory.
os.system("rm -r Builds")

# Clean the compiled binaries and commands.
os.system("sudo rm Driver/Builder/.Kevlar.ko.cmd")
os.system("sudo rm Driver/Builder/.Kevlar.mod.cmd")
os.system("sudo rm Driver/Builder/.Kevlar.mod.o.cmd")
os.system("sudo rm Driver/Builder/.Kevlar.o.cmd")
os.system("sudo rm Driver/Builder/.Module.symvers.cmd")
os.system("sudo rm Driver/Builder/.modules.order.cmd")
os.system("sudo rm Driver/Builder/Kevlar.ko")
os.system("sudo rm Driver/Builder/Kevlar.mod")
os.system("sudo rm Driver/Builder/Kevlar.mod.c")
os.system("sudo rm Driver/Builder/Kevlar.mod.o")
os.system("sudo rm Driver/Builder/Kevlar.o")
os.system("sudo rm Driver/Builder/Module.symvers")
os.system("sudo rm Driver/Builder/modules.order")

# Clean the binaries and commands for source files.
os.system("sudo rm Driver/Source/.Kevlar.o.cmd")
os.system("sudo rm Driver/Source/Kevlar.o")
os.system("sudo rm Driver/Source/.Interface.o.cmd")
os.system("sudo rm Driver/Source/Interface.o")
os.system("sudo rm Driver/Source/.DataManager.o.cmd")
os.system("sudo rm Driver/Source/DataManager.o")
os.system("sudo rm Driver/Source/.CryptographicServices.o.cmd")
os.system("sudo rm Driver/Source/CryptographicServices.o")