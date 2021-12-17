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
                    username varchar(256) NOT NULL,
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
        :return: None
        """
        try:
            self.connection.execute(
                f"""
                    INSERT INTO User('username', 'password', 'validation', 'database')
                    VALUES ('{account.username}', '{account.password}', '{account.validation}', '{account.database}')
                """)
        except IntegrityError:
            print("Failed to insert the account!")

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
