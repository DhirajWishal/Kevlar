from http.server import BaseHTTPRequestHandler

import CryptoService
import Database
import Packager
import XMLParser


class Server(BaseHTTPRequestHandler):
    database = Database.Database()
    packager = Packager.Packager()
    crypto = CryptoService.SymmetricService()

    def write_data(self, data: str):
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(bytes(data, "utf-8"))

    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/xml")
        self.end_headers()
        self.wfile.write(bytes("Boo Hoo no one uses GET to send sensitive data!", "utf-8"))

    def do_POST(self):
        self.send_response(200)
        self.send_header("Content-type", "text/xml")
        self.handle_request(self.rfile.read(int(self.headers['Content-Length'])))

    def handle_request(self, data: bytes):
        decrypted_data: str = data.decode("utf-8")
        xml_parser = XMLParser.XMLParser(decrypted_data)

        if xml_parser.mode == "handshake":
            data_to_send: str = self.packager.generate_handshake(CryptoService.to_base64(self.crypto.shared_key),
                                                                 self.crypto.initialization_vector)
            self.write_data(data_to_send)

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
                                                                                      validation_key)))

                else:
                    self.write_data(self.packager.generate_account(username, "", "", ""))

            else:
                self.write_data(self.packager.generate_account("", "", "", ""))
