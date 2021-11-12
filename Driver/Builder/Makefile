KDIR:=/lib/modules/$(shell uname -r)/build
kevlar-y := ../Source/Kevlar.o
obj-m := kevlar.o

all:
	sudo make -C $(KDIR) M=$(PWD) modules
clean:
	sudo make -C $(KDIR) M=$(PWD) clean