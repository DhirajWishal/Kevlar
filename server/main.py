# Python 3 server example
import ssl
from http.server import HTTPServer

import KevlarServer

hostName = "localhost"
serverPort = 2255

if __name__ == "__main__":
    webServer = HTTPServer((hostName, serverPort), KevlarServer.Server)
    context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    context.load_cert_chain(certfile='creds/cert.pem', keyfile='creds/key.pem')
    webServer.socket = context.wrap_socket(webServer.socket, server_side=True)

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")
