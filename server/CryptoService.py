import base64

from cryptography.hazmat.primitives import hashes, hmac


def perform_hmac(database: str, validation_key: str):
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
