const http = require('http');
const Account = require('./Account');
const AccountDatabase = require('./AccountDatabase');
const database = new AccountDatabase();
const port = 8080;

// Create the http server.
const server = http.createServer(function (req, res) {
	database.setAccount(new Account("Dhiraj", "Wishal", "Samaranayake", "Hello World".toString("base64")));

	res.writeHead(200, {
		'Content-Type': 'text/html'
	});
	res.write(req.url);
	res.end();
}).listen(port);