import base64

from cryptography.hazmat.primitives import hashes, hmac, padding
from cryptography.hazmat.primitives.ciphers import (
    Cipher, algorithms, modes
)

import os


def generate_aes_key():
    """
    Generate a random AES key to be used for client data encryption.
    :return: The generated AES key.
    """
    hasher = hashes.Hash(hashes.SHA3_256())
    hasher.update(os.urandom(1))
    return hasher.finalize()


class SymmetricService:
    """
    Symmetric key cryptographic service class. This class contains all the necessary methods and member variables
    required for encryption and decryption of data packets.
    """

    def __init__(self):
        self.shared_key = generate_aes_key()
        self.initialization_vector = os.urandom(16)
        self.salt = b"Kevlar"

    def encrypt(self, data: bytes):
        """
        Encrypt a block of data.
        :param data: The data to encrypt.
        :return: The encrypted data.
        """
        padder = padding.PKCS7(algorithms.AES.block_size).padder()
        data = padder.update(data) + padder.finalize()

        encryptor = Cipher(
            algorithms.AES(self.shared_key),
            modes.CBC(self.initialization_vector),
        ).encryptor()
        return encryptor.update(data) + encryptor.finalize()

    def decrypt(self, data: bytes):
        """
        Decrypt a block of data.
        :param data: The data to decrypt.
        :return: The decrypted data.
        """
        decryptor = Cipher(
            algorithms.AES(self.shared_key),
            modes.CBC(self.initialization_vector),
        ).decryptor()
        return decryptor.update(data) + decryptor.finalize()


def hmac(database: str, validation_key: str):
    """
    Utility function to generate an authentication code using HMAC.
    :param database: The database data.
    :param validation_key: The validation key.
    :return: The signature.
    """
    hasher = hmac.HMAC(bytes(validation_key, "utf-8"), hashes.SHA256())
    hasher.update(bytes(database, "utf-8"))
    return to_base64(hasher.finalize())


def to_base64(data):
    """
    Utility function to base64 encode.
    :param data: The data to encode.
    :return: The base64 encoded data.
    """
    return base64.b64encode(data)


def from_base64(data):
    """
    Utility function to base64 decode.
    :param data: The data to decode.
    :return: The decoded data.
    """
    return base64.b64decode(data)
