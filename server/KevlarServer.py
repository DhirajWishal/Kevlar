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

    def write_data(self, data: str):
        """
        Write data to be passed to the response.
        :param data: The data to write as a string.
        :return: None.
        """
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
        self.database.show_content()
        self.send_response(200)
        self.send_header("Content-type", "text/xml")
        self.handle_request(self.rfile.read(int(self.headers['Content-Length'])))

    def handle_request(self, data: bytes):
        """
        Get the data and handle the request.
        :param data: The request as bytes.
        :return: None
        """
        decoded_data = data.decode("utf-8")

        # Set up the xml parser to walk through the xml tree.
        xml_parser = XMLParser.XMLParser(decoded_data)

        # Handle the handshake.
        if xml_parser.mode == "handshake":
            # Generate the handshake.
            data_to_send: str = self.packager.generate_handshake(CryptoService.to_base64(self.crypto.shared_key),
                                                                 self.crypto.initialization_vector)
            self.write_data(data_to_send)

        # Handle the login request.
        elif xml_parser.mode == "login":
            username = ""
            password = ""

            # Walk through the xml tree and gather the required data.
            for element in xml_parser.tree.getroot():
                if element.tag == "username":
                    username = element.text
                elif element.tag == "password":
                    password = element.text

            # If the username exists within the database, we can then proceed to check if the password is valid.
            if self.database.user_exist(username):
                database_password = self.database.get_password(username)

                # Here we check if the password is valid or not.
                if database_password == password:
                    database_database = self.database.get_database(username)
                    validation_key = self.database.get_validation_key(username)

                    # If the password is valid, we can send back the correct account information back to the client.
                    self.write_data(self.packager.generate_account(username, database_password, database_database,
                                                                   CryptoService.hmac(database_database,
                                                                                      validation_key)))

                # If the password is invalid, we just send a form with just the username.
                else:
                    self.write_data(self.packager.generate_account(username, "", "", ""))

            # If the username does not exist, we send an empty response document.
            else:
                self.write_data(self.packager.generate_account("", "", "", ""))

        # Handle the user account request.
        elif xml_parser.mode == "account":
            username = ""
            password = ""
            database = ""
            hmac = ""
            user_validation_key = ""

            # Walk through the xml tree and get the required information.
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

            # If the user exists, we can proceed to update the account.
            if self.database.user_exist(username):
                validation_key = self.database.get_validation_key(username)

                # First we validate the incoming database data.
                if hmac == CryptoService.hmac(database, validation_key):

                    # If successful, we can update the table.
                    self.database.update(Account.Account(username, password, validation_key, database))
                    self.write_data(self.packager.generate_status("Successful"))

                # If not, we send an error status.
                else:
                    self.write_data(self.packager.generate_status("HMAC Error"))

            # If the username does not exist, we try to create a new account.
            else:
                # If the validation key parameter is empty in the xml, we consider that as a malformed xml document and
                # send an error status.
                if user_validation_key == "":
                    self.write_data(self.packager.generate_status("Empty validation key"))

                else:
                    # Else, we create a new account and then send a success note.
                    self.database.insert(Account.Account(username, password, user_validation_key, database))
                    self.write_data(self.packager.generate_status("Created user"))
