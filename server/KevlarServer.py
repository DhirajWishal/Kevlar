from http.server import BaseHTTPRequestHandler

import CryptoService
import Database
import Packager
import XMLParser


class Server(BaseHTTPRequestHandler):
    database = Database.Database()
    packager = Packager.Packager()
    crypto = CryptoService.SymmetricService()

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
        is_encrypted = int(self.headers['Encrypted'])
        decrypted_data = CryptoService.from_base64(data)
        print(decrypted_data)
        xml_parser = XMLParser.XMLParser(decrypted_data)

        if xml_parser.mode == "handshake":
            data_to_send = self.packager.generate_handshake(CryptoService.to_base64(self.crypto.shared_key),
                                                            self.crypto.initialization_vector)
            self.send_header("Content-Length", len(data_to_send))
            self.end_headers()
            self.wfile.write(data_to_send)
