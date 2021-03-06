import random
import ssl
from http.server import HTTPServer

import KevlarServer

hostName = "localhost"
serverPort = 2255

certificates = ["../credentials/dhirajcert.pem", "../credentials/thulanacert.pem", "../credentials/faizancert.pem"]
keys = ["../credentials/dhirajkey.pem", "../credentials/thulanakey.pem", "../credentials/faizankey.pem"]

if __name__ == "__main__":
    index = random.randint(0, len(certificates) - 1)
    certificate = certificates[index]
    key = keys[index]

    server = HTTPServer((hostName, serverPort), KevlarServer.Server)
    context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    context.load_cert_chain(certfile=certificate, keyfile=key)
    server.socket = context.wrap_socket(server.socket, server_side=True)

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass

    server.server_close()
    print("Server stopped.")
