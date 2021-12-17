# Python 3 server example
from http.server import BaseHTTPRequestHandler, HTTPServer

import Account
import CryptoService
import Database
import Packager

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
        self.end_headers()
        self.handle_request(self.rfile.read(int(self.headers['Content-Length'])))
        self.wfile.write(bytes("Niceeee", "utf-8"))

    def handle_request(self, data: bytes):
        decrypted_data = CryptoService.from_base64(data)
        print(decrypted_data)


if __name__ == "__main__":
    webServer = HTTPServer((hostName, serverPort), MyServer)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")
