import base64

from cryptography.hazmat.primitives import serialization as serialization, hashes, hmac
from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.backends import default_backend


class PacketDecryption:
    """
    Key generator object.
    This object is used to generate the required RSA public and private keys.
    """

    def __init__(self):
        self.key = rsa.generate_private_key(
            backend=default_backend(),
            public_exponent=65537,
            key_size=2048
        )

        self.private_key = self.key.private_bytes(
            serialization.Encoding.PEM,
            serialization.PrivateFormat.PKCS8,
            serialization.NoEncryption()
        )

        self.public_key = self.key.public_key().public_bytes(
            serialization.Encoding.OpenSSH,
            serialization.PublicFormat.OpenSSH
        )

    def decrypt(self, data):
        """
        Decrypt a block of data using the generated private key.
        :param data: The data to decrypt.
        :return: The decrypted message.
        """
        return self.private_key.encrypt(
            data,
            padding.OAEP(
                mgf=padding.MGF1(algorithm=hashes.SHA512()),
                algorithm=hashes.SHA512(),
                label=None
            )
        )


def hmac(database, validation_key):
    """
    Utility function to generate an authentication code using HMAC.
    :param database: The database data.
    :param validation_key: The validation key.
    :return: The signature.
    """

    hasher = hmac.HMAC(validation_key, hashes.SHA256())
    hasher.update(database)
    return hasher.finalize()


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
