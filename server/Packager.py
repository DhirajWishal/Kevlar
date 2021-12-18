class Packager:
    """
    Packager class.
    This class is used to generate the required XML to send back to the client.
    """

    def __init__(self):
        self.xml_version = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        self.begin_root = "<kevlar>"
        self.end_root = "</kevlar>"

    def generate_handshake(self, key, iv):
        """
        Generate handshake by transmitting the key and IV to the client.
        :param key: The key to be sent.
        :param iv: The initialization vector to be sent.
        :return: The send-able xml document.
        """

        document = self.xml_version + self.begin_root
        document += f"<key>{key}</key>"
        document += f"<iv>{iv}</iv>"
        return document + self.end_root
