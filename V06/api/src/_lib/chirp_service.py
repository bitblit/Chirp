import logging
import boto3
from boto3.dynamodb.conditions import Key
import time
import uuid

"""
    Service for interacting with chirp
"""

class ChirpService:
    def __init__(self):
        self.field="VALUE"
        self.dynamodb = boto3.resource('dynamodb', region_name='us-east-1')
        self.table = self.dynamodb.Table('chirp')
        self.anonymous_user='ANON'
        self.bucket='chirp01'
        self.s3 = boto3.client('s3')
        self.default_link_expiration_in_seconds=300

    # Return a list of all chirps
    def get_chirp_count(self):
        # TODO: Make this faster later
        return len(self.fetch_list())

    # Saves a new chirp and returns its guid
    def save(self,chirp_text,image_location):

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
        if image_location is not None and len(image_location)>0:
            item['image_location']=image_location

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

        return_value = []
        for item in response['Items']:
            if 'image_location' in item:
                item['image_url']=self.create_image_url(item['image_location'])
            return_value.append(item)

        return return_value

    def create_image_url(self,path):
        result = self.s3.generate_presigned_url(
                ClientMethod='get_object',
                Params={
                    'Bucket': self.bucket,
                    'Key': path,
                },
                ExpiresIn=self.default_link_expiration_in_seconds
        )
        return result

    def create_image_location(self):
        full_path = "customer-image/{0}.jpg".format(uuid.uuid4())
        logging.info("Creating upload link for customer {0}".format(full_path))
        result = self.s3.generate_presigned_url(
                ClientMethod='put_object',
                Params={
                    'Bucket': self.bucket,
                    'Key': full_path,
                    'ContentType': 'image/jpg'  # , #'multipart/form-data',
                    # 'ContentLength':req['contentSize']
                    # 'Conditions':{}
                },
                ExpiresIn=self.default_link_expiration_in_seconds
        )

        return {
            'path':full_path,
            'url':result
        }

if __name__ == '__main__':
    logging.getLogger().setLevel(10)
    print('Running test')

    service = ChirpService()

    print("Count: {0}".format(service.get_chirp_count()))
