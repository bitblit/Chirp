# Exception representing a 400
class InvalidRequestError(Exception):
    def __init__(self, code, message):
        self.code = code
        self.message = message

    def __str__(self):
        return repr(self.message)
