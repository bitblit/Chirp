import logging

"""
    Service for interacting with chirp - more on this later
"""

class ChirpService:
    def __init__(self):
        self.field="VALUE"

    # Return a list of all chirps
    def get_chirp_count(self):
        return 0

    # Saves a new chirp and returns its guid
    def save(self,chirp):
        return None

    def fetch_chirp_by_id(self):
        return None

    def fetch_list(self):
        return None

if __name__ == '__main__':
    logging.getLogger().setLevel(10)
    print('Running test')

    service = ChirpService()

    print("Count: {0}".format(service.get_chirp_count()))
