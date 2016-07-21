# Exception representing a 403
class IllegalAccessError(Exception):
    def __init__(self, code, message):
        self.code = code
        self.message = message

    def __str__(self):
        return repr(self.message)
