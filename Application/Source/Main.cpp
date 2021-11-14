#include "DriverInterface.hpp"

#include <iostream>

int main()
{
	std::cout << "Hello from Kevlar!" << std::endl;
	DriverInterface interface;

	std::cout << "Is driver ready? " << (interface.IsDriverReady() ? "Yes" : "No") << std::endl;

	return 0;
}