import enum


class Responses(enum.Enum):
    OK: int = 200
    CREATED: int = 201
    BAD_REQUEST: int = 400
    NOT_FOUND: int = 404
    INTERNAL_ERROR: int = 500
