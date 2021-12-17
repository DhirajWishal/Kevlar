class Account:
    """
    Account class.
    This class holds information about a single user account.
    """

    def __init__(self, username, password, validation, database):
        self.username = username
        self.password = password
        self.validation = validation
        self.database = database
