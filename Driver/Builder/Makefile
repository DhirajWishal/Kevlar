KDIR:=/lib/modules/$(shell uname -r)/build

Kevlar-y := ../Source/Kevlar.o 		\
			../Source/Interface.o	\
			../Source/DataManager.o \
			../Source/CryptographicServices.o 
obj-m := Kevlar.o

all:
	sudo make -C $(KDIR) M=$(PWD) modules
clean:
	sudo make -C $(KDIR) M=$(PWD) clean