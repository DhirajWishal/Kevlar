const mysql = require("mysql")

class Database {
	constructor() {
		this.connection = mysql.createConnection({
			host: "localhost",
			user: "yourusername",
			password: "yourpassword"
		  });
	}
}