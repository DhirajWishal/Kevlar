#include "DriverInterface.hpp"

#include <iostream>

int main()
{
	try
	{
		std::cout << "Hello from Kevlar!" << std::endl;
		DriverInterface interface;

		std::cout << "Is driver ready? " << (interface.IsDriverReady() ? "Yes" : "No") << std::endl;

		PlainText text = "Hello World";
		std::cout << "Plaintext: " << text << std::endl;

		CipherText ciphertext = ToCipherText(text);
		interface.SubmitData(ciphertext);

		CipherText retrievedData = interface.RequestData();
		PlainText data = ToPlainText(retrievedData);
		std::cout << "Data from driver: " << data << std::endl;
	}
	catch (const std::exception &e)
	{
		std::cerr << e.what() << '\n';
	}

	return 0;
}