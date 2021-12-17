# Python 3 server example
from http.server import BaseHTTPRequestHandler, HTTPServer

import Account
import CryptoService
import Database
import Packager
import Responses

hostName = "localhost"
serverPort = 2255


class MyServer(BaseHTTPRequestHandler):
    database = Database.Database()
    packager = Packager.Packager()
    packet_decryptor = CryptoService.PacketDecryption()

    def get_public_key(self):
        return self.packager.generate_handshake(CryptoService.to_base64(self.packet_decryptor.public_key))

    def do_GET(self):
        self.send_response(Responses.Responses.OK)
        self.send_header("Content-type", "text/xml")
        self.end_headers()
        self.wfile.write(bytes(self.get_public_key(), "utf-8"))

    def do_POST(self):
        self.send_response(Responses.Responses.OK)
        self.send_header("Content-type", "text/xml")
        self.end_headers()
        self.wfile.write(bytes(self.get_public_key(), "utf-8"))


if __name__ == "__main__":
    webServer = HTTPServer((hostName, serverPort), MyServer)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")
