#include "CryptographicServices.h"

#include <linux/crypto.h>
#include <linux/scatterlist.h>

#include <crypto/skcipher.h>
#include <crypto/hash.h>
#include <crypto/if_alg.h>
#include <crypto/drbg.h>

static unsigned char IVData[16] = {
	0x00,
	0x11,
	0x22,
	0x33,
	0x44,
	0x55,
	0x66,
	0x77,
	0x88,
	0x99,
	0xAA,
	0xBB,
	0xCC,
	0xDD,
	0xEE,
	0xFF,
};

struct CryptographicOutput encrypt_data_symmetric(struct SymmetricInput input)
{
	struct scatterlist scatterList = {};
	struct crypto_skcipher *pSkcipher = NULL;
	struct skcipher_request *pRequest = NULL;
	struct crypto_wait wait = {};
	struct CryptographicOutput output = {
		.pOutput = NULL,
		.mDataLength = 0,
	};

	// Create the cipher handle.
	pSkcipher = crypto_alloc_skcipher("cbc-aes-aesni", 0, 0);
	if (IS_ERR(pSkcipher))
	{
		pr_err("Failed to setup the cryptographic services!\n");
		return output;
	}

	// Allocate the cipher request.
	pRequest = skcipher_request_alloc(pSkcipher, GFP_KERNEL);
	if (!pRequest)
	{
		pr_err("Failed to allocate the cipher request!\n");
		crypto_free_skcipher(pSkcipher);

		return output;
	}

	// Set the request callback.
	skcipher_request_set_callback(pRequest, CRYPTO_TFM_REQ_MAY_BACKLOG, crypto_req_done, &wait);

	// Set the key.
	if (crypto_skcipher_setkey(pSkcipher, input.pKey, 64))
	{
		pr_info("Failed to set the key!\n");
		crypto_free_skcipher(pSkcipher);
		skcipher_request_free(pRequest);

		return output;
	}

	// Encrypt blocks.
	sg_init_one(&scatterList, input.pInputData, input.mInputLength);
	skcipher_request_set_crypt(pRequest, &scatterList, &scatterList, input.mInputLength, IVData);
	crypto_init_wait(&wait);

	if (crypto_wait_req(crypto_skcipher_encrypt(pRequest), &wait))
	{
		pr_info("Failed to encrypt data!\n");
		crypto_free_skcipher(pSkcipher);
		skcipher_request_free(pRequest);

		return output;
	}

	// Get the encrypted data.
	return output;
}

struct CryptographicOutput decrypt_data_symmetric(struct SymmetricInput input)
{
	struct scatterlist scatterList = {};
	struct crypto_skcipher *pSkcipher = NULL;
	struct skcipher_request *pRequest = NULL;
	struct crypto_wait wait = {};
	struct CryptographicOutput output = {
		.pOutput = NULL,
		.mDataLength = 0,
	};

	// Create the cipher handle.
	pSkcipher = crypto_alloc_skcipher("cbc-aes-aesni", 0, 0);
	if (IS_ERR(pSkcipher))
	{
		pr_err("Failed to setup the cryptographic services!\n");
		return output;
	}

	// Allocate the cipher request.
	pRequest = skcipher_request_alloc(pSkcipher, GFP_KERNEL);
	if (!pRequest)
	{
		pr_err("Failed to allocate the cipher request!\n");
		crypto_free_skcipher(pSkcipher);

		return output;
	}

	// Set the request callback.
	skcipher_request_set_callback(pRequest, CRYPTO_TFM_REQ_MAY_BACKLOG, crypto_req_done, &wait);

	// Set the key.
	if (crypto_skcipher_setkey(pSkcipher, input.pKey, 64))
	{
		pr_info("Failed to set the key!\n");
		crypto_free_skcipher(pSkcipher);
		skcipher_request_free(pRequest);

		return output;
	}

	// Decrypt blocks.
	sg_init_one(&scatterList, input.pInputData, input.mInputLength);
	skcipher_request_set_crypt(pRequest, &scatterList, &scatterList, input.mInputLength, IVData);
	crypto_init_wait(&wait);

	if (crypto_wait_req(crypto_skcipher_decrypt(pRequest), &wait))
	{
		pr_info("Failed to decrypt data!\n");
		crypto_free_skcipher(pSkcipher);
		skcipher_request_free(pRequest);

		return output;
	}

	// Get the decrypted data.
	return output;
}