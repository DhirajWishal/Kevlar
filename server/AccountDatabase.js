const {
	MongoClient
} = require('mongodb');
const filesystem = require("fs")
const Account = require("./Account")

module.exports = class AccountDatabase {
	/**
	 * Default constructor.
	 * This will create the database connection.
	 */
	constructor() {
		this.uri = "mongodb+srv://Kevlar:ForCryptoProject@cluster0.jr8m5.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
	}

	/**
	 * Check is a user exists in the database.
	 * 
	 * @param {string} username The username.
	 * @param {string} password The password.
	 * @returns Boolean value stating if the user exists or not.
	 */
	async doesUserExist(username, password, callback) {
		return this.client.connect().then((error) => {
			if (error) throw error;

			const cluster = this.client.db("Cluster0");
			const userCollection = cluster.collection("User");
			return userCollection.findOne({
				username: username,
				password: password
			}).then(function (err, result) {
				if (err) throw err;
				return (result) ? true : false;
			});
		});
	}

	setAccount(account) {
		const client = new MongoClient("mongodb+srv://Kevlar:ForCryptoProject@cluster0.jr8m5.mongodb.net/myFirstDatabase?retryWrites=true&w=majority", {
			useNewUrlParser: true,
			useUnifiedTopology: true,
			tls: true // Enable TLS security.
		});
		
		client.connect(function (database) {
			console.log("connected to database!");

			const cluster = client.db("Cluster0");
			const userCollection = cluster.collection("User");

			userCollection.insertOne(
				account.account,
				function (error, result) {
					if (error) throw error;
					console.log("inserted data!");
					database.close();
				});

			userCollection.findOne({
				username: account.username,
				password: account.password
			}, function (err, result) {
				if (err) throw err;

				if (result) {
					userCollection.updateOne({
						username: account.username,
						password: account.password
					}, {
						$set: {
							database: account.database
						}
					}, function (error, result) {
						if (error) throw error;
						console.log("updated data!");
						database.close();
					});
				} else {
					userCollection.insertOne(
						account.account,
						function (error, result) {
							if (error) throw error;
							console.log("inserted data!");
							database.close();
						});
				}
			});
		});
	}

	/**
	 * Create a new user account.
	 * 
	 * @param {Account} account The account object to insert. 
	 */
	createUserAccount(account) {
		this.client.connect().then((error) => {
			if (error) throw error;

			const cluster = this.client.db("Cluster0");
			const userCollection = cluster.collection("User");
			userCollection.insertOne(
				account
			).then(function (error, result) {
				if (error) throw error;
				this.client.close();
			}).catch((error) => {
				this.client.close();
			});
		});
	}

	/**
	 * Get an account from the database.
	 * 
	 * @param {string} username The username.
	 * @param {string} password The password.
	 * @returns Returns an account if the username and password contains within the database, or returns null.
	 */
	async getUserAccount(username, password) {
		this.userCollection.findOne({
			username: username,
			password: password
		}).then(function (error, result) {
			if (error.acknowledged == false) throw error;
			if (result != undefined)
				return new Account(result.username, result.password, result.verification, atob(String(item.database)));

			return null;
		}).catch((error) => {
			console.log(error)
			return null;
		});
	}

	/**
	 * Update a single user dat using the username and password.
	 * 
	 * @param {string} username The username.
	 * @param {string} password The password.
	 * @param {string} database The base64 encoded data.
	 */
	async updateUserAccount(username, password, database) {
		this.updateCollection.updateOne({
			username: username,
			password: password
		}, {
			$set: {
				database: database
			}
		}).then(function (error, result) {
			if (error.acknowledged == false) throw error;
		}).catch((error) => {
			console.log(error)
		});
	}

	/**
	 * Close the database connection.
	 */
	close() {
		this.client.close();
	}
}