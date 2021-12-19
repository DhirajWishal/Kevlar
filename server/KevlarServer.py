from http.server import BaseHTTPRequestHandler

import Account
import CryptoService
import Database
import Packager
import XMLParser


class Server(BaseHTTPRequestHandler):
    database = Database.Database()
    packager = Packager.Packager()

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

        # Handle the check request.
        if xml_parser.mode == "check":
            self.handle_check(xml_parser)

        # Handle the login request.
        elif xml_parser.mode == "login":
            self.handle_login(xml_parser)

        # Handle the user create account request.
        elif xml_parser.mode == "account":
            self.handle_account(xml_parser)

        # Handle the user update request.
        elif xml_parser.mode == "update":
            self.handle_update(xml_parser)

    def handle_check(self, xml_parser):
        """
        Handle the client's check if account exist request.
        :param xml_parser: The xml parser used to parse the user request.
        :return: None
        """
        username = ""
        password = ""

        # Walk through the xml tree and gather the required data.
        for element in xml_parser.tree.getroot():
            if element.tag == "username":
                username = element.text
            elif element.tag == "password":
                password = element.text

        # Check if the username exists.
        if self.database.user_exist(username):
            # If true, check if the passwords match.
            if self.database.get_password(username) == password:
                # If yes, we just say that everything is okay.
                self.write_data(self.packager.generate_status("2"))

            else:
                # If the passwords don't match, we say that the status is 1 (the passwords mismatch)
                self.write_data(self.packager.generate_status("1"))

        else:
            # If the username isn't there in the database, we just send the status code 0.
            self.write_data(self.packager.generate_status("0"))

    def handle_login(self, xml_parser):
        """
        Handle the client's login request.
        :param xml_parser: The xml parser used to parse the user request.
        :return: None
        """
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
                initialization_vector = self.database.get_initialization_vector(username)

                # If the password is valid, we can send back the correct account information back to the client.
                self.write_data(self.packager.generate_account(username, database_password, database_database,
                                                               CryptoService.perform_hmac(database_database,
                                                                                          validation_key),
                                                               initialization_vector))

            # If the password is invalid, we just send a form with just the username.
            else:
                self.write_data(self.packager.generate_account(username, "", "", "", ""))

        # If the username does not exist, we send an empty response document.
        else:
            self.write_data(self.packager.generate_account("", "", "", "", ""))

    def handle_account(self, xml_parser):
        """
        Handle the client's create account request.
        :param xml_parser: The xml parser used to parse the request.
        :return: None
        """
        username = ""
        password = ""
        database = ""
        user_validation_key = ""
        initialization_vector = ""

        # Walk through the xml tree and get the required information.
        for element in xml_parser.tree.getroot():
            if element.tag == "username":
                username = element.text
            elif element.tag == "password":
                password = element.text
            elif element.tag == "database":
                database = element.text
            elif element.tag == "validation":
                user_validation_key = element.text
            elif element.tag == "iv":
                initialization_vector = element.text

        # If the user exists, we can proceed to update the account.
        if not self.database.user_exist(username):
            if initialization_vector == "":
                self.write_data(self.packager.generate_status("Invalid Initialization Vector"))
            else:
                if not self.database.insert(username, password, user_validation_key, database, initialization_vector):
                    self.write_data(self.packager.generate_status("Failed to insert data"))
                else:
                    self.write_data(self.packager.generate_status("Successful"))

    def handle_update(self, xml_parser):
        """
        Handle the client's update request.
        :param xml_parser: The xml parser used to parse the request.
        :return: None
        """
        username = ""
        password = ""
        database = ""
        hmac = ""

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

        # If the user exists, we can proceed to update the account.
        if self.database.user_exist(username):
            validation_key = self.database.get_validation_key(username)

            # First we validate the incoming database data.
            if hmac == CryptoService.perform_hmac(database, validation_key):

                # If successful, we can update the table.
                self.database.update(username, password, validation_key, database)
                self.write_data(self.packager.generate_status("Successful"))

            # If not, we send an error status.
            else:
                self.write_data(self.packager.generate_status("HMAC Error"))
