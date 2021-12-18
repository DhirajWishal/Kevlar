class Packager:
    """
    Packager class.
    This class is used to generate the required XML to send back to the client.
    """

    def __init__(self):
        self.xml_version = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        self.begin_root = "<kevlar>"
        self.end_root = "</kevlar>"

    def generate_handshake(self, key: str, iv: bytes):
        """
        Generate handshake by transmitting the key and IV to the client.
        :param key: The key to be sent.
        :param iv: The initialization vector to be sent.
        :return: The send-able xml document.
        """
        iv_array = [x for x in iv]

        document = self.xml_version + self.begin_root
        document += f"<key>{key}</key>"
        document += f"<iv>{iv_array}</iv>"
        return document + self.end_root

    def generate_account(self, username: str, password: str, database: str, hmac: str):
        """
        Generate account document by using the upload-able data.
        :param username: The account username.
        :param password: The account password.
        :param database: The account database.
        :param hmac: The authentication hmac of the database.
        :return: The xml document.
        """
        document = self.xml_version + self.begin_root
        document += f"<username>{username}</username>"
        document += f"<password>{password}</password>"
        document += f"<database>{database}</database>"
        document += f"<hmac>{hmac}</hmac>"
        return document + self.end_root
