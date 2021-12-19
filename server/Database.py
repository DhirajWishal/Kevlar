import sqlite3
from sqlite3 import IntegrityError

import Account


class Database:
    """
    Database class. This class contains the connection and utility functions to access and manipulate data stored in
    the sqlite3 database.
    """

    def __init__(self):
        self.connection = sqlite3.connect("Accounts.db")

        # try and create the required tables.
        self.connection.execute(
            """
            CREATE TABLE IF NOT EXISTS User(
                username varchar(32) NOT NULL,
                password char(32) NOT NULL,
                validation char(32) NOT NULL,
                database blob,
                initializationVector char(16) NOT NULL,
                PRIMARY KEY(username)
            )
            """)

        self.commit()

    def insert(self, username: str, password: str, validation: str, database: str, initialization_vector: str):
        """
        Insert a new user into the database.
        :param username: The account username.
        :param password: The account password.
        :param validation: The account validation password.
        :param database: The database string.
        :param initialization_vector: The initialization vector used to encrypt the passwords.
        :return: Boolean stating if it was successful inserted or not.
        """
        try:
            self.connection.execute(
                f"""
                    INSERT INTO User('username', 'password', 'validation', 'database', 'initializationVector')
                    VALUES ('{username}', '{password}', '{validation}', '{database}', '{initialization_vector}')
                """)

            self.commit()
            return True

        except IntegrityError:
            return False

    def update(self, username: str, password: str, validation: str, database: str):
        """
        Update a table in the database.
        :param username: The username of the user.
        :param password: The password.
        :param validation: The validation key.
        :param database: The database data to set.
        :return: None
        """
        self.connection.execute(
            f"""
                UPDATE User SET
                password = '{password}',
                validation = '{validation}',
                database = '{database}'
                WHERE username = '{username}'
            """)

        self.commit()

    def user_exist(self, username: str):
        """
        Check if a user exists in the database.
        :param username: The username to check.
        :return: Boolean value stating if the user is present or not.
        """
        # test and see if anything is returned from the query. If so, we return true. Else false.
        for _ in self.connection.execute(f"SELECT username FROM User WHERE username = '{username}'"):
            return True

        return False

    def get_password(self, username: str):
        """
        Get the password from the database using the username.
        :param username: The username of the user.
        :return: The password. Returns a null string if it doesn't exist.
        """
        for row in self.connection.execute(f"SELECT password FROM User WHERE username = '{username}'"):
            return row[0]

        return ""

    def get_database(self, username: str):
        """
        Get the database entry from the database using the username.
        :param username: The username of the user.
        :return: The database. Returns a null string if it doesn't exist.
        """
        for row in self.connection.execute(f"SELECT database FROM User WHERE username = '{username}'"):
            return row[0]

        return ""

    def get_validation_key(self, username: str):
        """
        Get the validation key from the database using the username.
        :param username: The username of the user.
        :return: The validation key. Returns a null string if it doesn't exist.
        """
        for row in self.connection.execute(f"SELECT validation FROM User WHERE username = '{username}'"):
            return row[0]

        return ""

    def get_initialization_vector(self, username: str):
        """
        Get the initialization vector from the database using the username.
        :param username: The username of the user.
        :return: The initialization vector. Returns a null string if it doesn't exist.
        """
        for row in self.connection.execute(f"SELECT initializationVector FROM User WHERE username = '{username}'"):
            return row[0]

        return ""

    def show_content(self):
        """
        Display all the information stored in the user table.
        :return: None
        """
        result = self.connection.execute("SELECT * FROM User")
        for row in result:
            print(
                "Username:", row[0],
                "Password:", row[1],
                "Validation key:", row[2],
                "Database:", row[3],
                "Initialization Vector:", row[4]
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
