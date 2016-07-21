import logging
import boto3
from boto3.dynamodb.conditions import Key
import time

"""
    Service for interacting with chirp
"""

class ChirpService:
    def __init__(self):
        self.field="VALUE"
        self.dynamodb = boto3.resource('dynamodb', region_name='us-east-1')
        self.table = self.dynamodb.Table('chirp')
        self.anonymous_user='ANON'

    # Return a list of all chirps
    def get_chirp_count(self):
        # TODO: Make this faster later
        return len(self.fetch_list())

    # Saves a new chirp and returns its guid
    def save(self,chirp_text):

        chirp_to_save = chirp_text
        if chirp_to_save is None:
            chirp_to_save = 'EMPTY';
        if len(chirp_to_save)>140:
            chirp_to_save = chirp_to_save[0:135]+"..."

        item = {
            'userId': self.anonymous_user,
            'timestamp': int(time.time()),
            'chirp_text': chirp_to_save
        }

        self.table.put_item(Item=item)

        return "{0}_{1}".format(item['userId'],item['timestamp'])

    def fetch_chirp_by_id(self, chirp_id):
        pieces = chirp_id.split("_")
        lookup = self.table.get_item(Key={'userId': pieces[0], 'timeStamp': pieces[1]})

        if lookup is None or 'Item' not in lookup:
            return None
        else:
            return lookup['Item']

    def fetch_list(self):
        response = self.table.query(KeyConditionExpression=Key('userId').eq(self.anonymous_user))
        return response['Items']

if __name__ == '__main__':
    logging.getLogger().setLevel(10)
    print('Running test')

    service = ChirpService()

    print("Count: {0}".format(service.get_chirp_count()))
