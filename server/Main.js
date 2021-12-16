var http = require('http');
const port = 8080

// Create the http server.
const server = http.createServer(function (req, res) {
	res.writeHead(200, {
		'Content-Type': 'text/html'
	});
	res.write(req.url);
	res.end();
}).listen(port);