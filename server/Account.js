/**
 * Account class. 
 * This class contains information about a single user account.
 */
module.exports = class Account {
	/**
	 * Construct the object using its values.
	 * 
	 * @param {string} username The account username.
	 * @param {string} password The account password.
	 * @param {string} validation The account validation key.
	 * @param {file} database The database file of the account.
	 */
	constructor(username, password, validation, database) {
		this.username = username;
		this.password = password;
		this.validation = validation;
		this.database = database;
	}
}