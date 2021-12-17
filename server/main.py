# Python 3 server example
from http.server import BaseHTTPRequestHandler, HTTPServer

import Account
import Database

hostName = "localhost"
serverPort = 8080


class MyServer(BaseHTTPRequestHandler):
    database = Database.Database()

    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/xml")
        self.end_headers()
        self.wfile.write(bytes("<html><head><title>https://pythonbasics.org</title></head>", "utf-8"))
        self.wfile.write(bytes("<p>Request: %s</p>" % self.path, "utf-8"))
        self.wfile.write(bytes("<body>", "utf-8"))
        self.wfile.write(bytes("<p>This is an example web server.</p>", "utf-8"))
        self.wfile.write(bytes("</body></html>", "utf-8"))

        self.database.show_content()

        if self.database.user_exist("Dhiraj"):
            self.database.update(Account.Account("Dhiraj", "00000000000000000000000000000000",
                                                 "00000000000000000000000000000000", 0))

        self.database.commit()


if __name__ == "__main__":
    webServer = HTTPServer((hostName, serverPort), MyServer)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")
