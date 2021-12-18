from http.server import BaseHTTPRequestHandler

import Account
import CryptoService
import Database
import Packager
import XMLParser


class Server(BaseHTTPRequestHandler):
    database = Database.Database()
    packager = Packager.Packager()
    crypto = CryptoService.SymmetricService()

    def write_data(self, data: str, should_encrypt: int):
        """
        Write data to be passed to the response.
        :param data: The data to write as a string.
        :param should_encrypt whether to encrypt data.
        :return: None.
        """
        if should_encrypt == 1:
            data = self.crypto.encrypt(data)

        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(bytes(data, "utf-8"))

    def do_GET(self):
        """
        Handle GET requests.
        :return: None
        """
        self.send_response(200)
        self.send_header("Content-type", "text/xml")
        self.end_headers()
        self.wfile.write(bytes("Boo Hoo no one uses GET to send sensitive data!", "utf-8"))

    def do_POST(self):
        """
        Handle POST requests.
        :return: None
        """
        self.send_response(200)
        self.send_header("Content-type", "text/xml")
        self.handle_request(self.rfile.read(int(self.headers['Content-Length'])))

    def handle_request(self, data: bytes):
        """
        Get the data and handle the request.
        :param data: The request as bytes.
        :return: None
        """
        decrypted_data: str = data.decode("utf-8")
        is_encrypted = int(self.headers["Encrypted:"])
        if is_encrypted == 1:
            data_to_decrypt = CryptoService.from_base64(decrypted_data)
            decrypted_data = self.crypto.decrypt(data_to_decrypt)

        xml_parser = XMLParser.XMLParser(decrypted_data)

        # Handle the handshake.
        if xml_parser.mode == "handshake":
            data_to_send: str = self.packager.generate_handshake(CryptoService.to_base64(self.crypto.shared_key),
                                                                 self.crypto.initialization_vector)
            self.write_data(data_to_send, is_encrypted)

        # Handle the login request.
        elif xml_parser.mode == "login":
            username = ""
            password = ""
            for element in xml_parser.tree.getroot():
                if element.tag == "username":
                    username = element.text
                elif element.tag == "password":
                    password = element.text

            if self.database.user_exist(username):
                database_password = self.database.get_password(username)

                if database_password == password:
                    database_database = self.database.get_database(username)
                    validation_key = self.database.get_validation_key(username)
                    self.write_data(self.packager.generate_account(username, database_password, database_database,
                                                                   CryptoService.hmac(database_database,
                                                                                      validation_key)), is_encrypted)

                else:
                    self.write_data(self.packager.generate_account(username, "", "", ""), is_encrypted)

            else:
                self.write_data(self.packager.generate_account("", "", "", ""), is_encrypted)

        # Handle the user account request.
        elif xml_parser.mode == "account":
            username = ""
            password = ""
            database = ""
            hmac = ""
            user_validation_key = ""

            for element in xml_parser.tree.getroot():
                if element.tag == "username":
                    username = element.text
                elif element.tag == "password":
                    password = element.text
                elif element.tag == "database":
                    database = element.text
                elif element.tag == "hmac":
                    hmac = element.text
                elif element.tag == "validation":
                    user_validation_key = element.text

            if self.database.user_exist(username):
                validation_key = self.database.get_validation_key(username)

                if hmac == CryptoService.hmac(database, validation_key):
                    self.database.update(Account.Account(username, password, validation_key, database))
                    self.write_data(self.packager.generate_status("Successful"), is_encrypted)

                else:
                    self.write_data(self.packager.generate_status("HMAC Error"), is_encrypted)

            else:
                if user_validation_key == "":
                    self.write_data(self.packager.generate_status("Empty validation key"), is_encrypted)

                else:
                    self.database.insert(Account.Account(username, password, user_validation_key, database))
                    self.write_data(self.packager.generate_status("Created user"), is_encrypted)
