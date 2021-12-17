class Packager:
    """
    Packager class.
    This class is used to generate the required XML to send back to the client.
    """

    def __init__(self):
        self.xml_version = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        self.begin_root = "<kevlar>"
        self.end_root = "</kevlar>"

    def generate_handshake(self, public_key):
        """
        Generate the handshake document containing the public key of the server.
        :param public_key: The public key of the server.
        :return: The send-able xml document.
        """

        document = self.xml_version + self.begin_root
        document += f"<public keysize=\"2048\">{public_key}</public>"
        return document + self.end_root
