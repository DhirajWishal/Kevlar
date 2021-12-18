import sqlite3
from sqlite3 import OperationalError, IntegrityError

import Account


class Database:
    """
    Database class. This class contains the connection and utility functions to access and manipulate data stored in
    the sqlite3 database.
    """

    def __init__(self):
        self.connection = sqlite3.connect("Accounts.db")

        # try and create the required tables.
        try:
            self.connection.execute("""SELECT * FROM User""")

        except OperationalError:
            self.connection.execute(
                """
                CREATE TABLE User(
                    username varchar(32) NOT NULL,
                    password char(32) NOT NULL,
                    validation char(32) NOT NULL,
                    database blob,
                    PRIMARY KEY(username)
                )
                """)

        self.commit()

    def insert(self, account: Account.Account):
        """
        Insert a new user into the database.

        :param account: The account to insert.
        :return: Boolean stating if the insertion was successful.
        """
        try:
            self.connection.execute(
                f"""
                    INSERT INTO User('username', 'password', 'validation', 'database')
                    VALUES ('{account.username}', '{account.password}', '{account.validation}', '{account.database}')
                """)

            return True

        except IntegrityError:
            return False

    def update(self, account: Account.Account):
        """
        Update an existing record in the table.
        Make sure that the record to be updated exists within the database.
        :param account: The account information to update with.
        :return: None.
        """

        self.connection.execute(f"""
            UPDATE User SET
            password = '{account.password}',
            validation = '{account.validation}',
            database = '{account.database}'
            WHERE username = '{account.username}'
            """)

    def user_exist(self, username):
        """
        Check if a user exists in the database.
        :param username: The username to check.
        :return: Boolean value stating if the user is present or not.
        """

        # test and see if anything is returned from the query. If so, we return true. Else false.
        for _ in self.connection.execute(f"""SELECT username FROM User WHERE username = '{username}'"""):
            return True

        return False

    def get_password(self, username):
        """
        Get the password from the database using the username.
        :param username: The username of the user.
        :return: The password. Returns a null string if it doesn't exist.
        """

        for row in self.connection.execute(f"""SELECT password FROM User WHERE username = '{username}'"""):
            return row

        return ""

    def get_database(self, username):
        """
        Get the database entry from the database using the username.
        :param username: The username of the user.
        :return: The database. Returns a null string if it doesn't exist.
        """

        for row in self.connection.execute(f"""SELECT database FROM User WHERE username = '{username}'"""):
            return row

        return ""

    def get_validation_key(self, username):
        """
        Get the validation key from the database using the username.
        :param username: The username of the user.
        :return: The validation key. Returns a null string if it doesn't exist.
        """

        for row in self.connection.execute(f"""SELECT validation FROM User WHERE username = '{username}'"""):
            return row

        return ""

    def show_content(self):
        """
        Display all the information stored in the user table.
        :return: None
        """
        result = self.connection.execute("""SELECT * FROM User""")

        for row in result:
            print(
                "Username:", row[0],
                "Password:", row[1],
                "Validation key:", row[2],
                "Database:", row[3],
            )

    def commit(self):
        """
        Commit all the transactions to the database.
        :return: None
        """
        self.connection.commit()

    def close(self):
        """
        Close the database connection.
        :return: None
        """
        self.connection.close()
