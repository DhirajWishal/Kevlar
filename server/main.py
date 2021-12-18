# Python 3 server example
import ssl
from http.server import BaseHTTPRequestHandler, HTTPServer

import Account
import CryptoService
import Database
import Packager
import XMLParser

hostName = "localhost"
serverPort = 2255


class MyServer(BaseHTTPRequestHandler):
    database = Database.Database()
    packager = Packager.Packager()
    packet_decryptor = CryptoService.PacketDecryption()

    def get_public_key(self):
        return self.packager.generate_handshake(CryptoService.to_base64(self.packet_decryptor.public_key))

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
            public_key = decrypted_data.decode("utf-8").replace(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kevlar mode=\"handshake\"><public keysize=\"2048\">", "")
            public_key = public_key.replace("</public></kevlar>", "")
            public_key = bytes(public_key, "utf-8")
            encrypter = CryptoService.PacketEncrypter(public_key)
            # print(CryptoService.recover_public_key(int(public_key), 65537))
            data_to_send = CryptoService.to_base64(encrypter.encrypt(bytes(self.get_public_key(), "utf-8")))
            # noinspection PyTypeChecker
            self.send_header("Content-Length", len(data_to_send))
            self.end_headers()
            self.wfile.write(data_to_send)


if __name__ == "__main__":
    webServer = HTTPServer((hostName, serverPort), MyServer)
    webServer.socket = ssl.wrap_socket(webServer.socket,
                                       server_side=True,
                                       certfile='creds/cert.pem',
                                       keyfile='creds/key.pem',
                                       ssl_version=ssl.PROTOCOL_TLS)

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")
