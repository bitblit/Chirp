import logging
import traceback

import simplejson

from illegal_access_error import IllegalAccessError
from invalid_request_error import InvalidRequestError
from no_such_resource_error import NoSuchResourceError

"""
 Class just for handling API specific tasks (around consistent formatting of responses)
 on AWS Lambda.
"""


class LambdaAPI:
    def __init__(self, description, doc_url_prefix, event, context):
        # % isn't technically reserved but its still a pain in the butt
        self.RFC_3986_RESERVED = ['!', '*', '\'', '(', ')', '', ':', '@', '&', '=',
                                  '+', '$', ',', '/', '?', '#', '[', ']', '%']
        self.context = context
        self.event = event
        self.description = description
        self.doc_url_prefix = doc_url_prefix

    # Easy validation - throws exception on missing value
    def except_on_missing_value(self, path_list):
        LambdaAPI.raise_except_on_missing_value(self.event, path_list)

    # Simple function to fetch items from the event
    def fetch_value(self, path):
        return self.find_value(self.event, path)

    # Formats arbitrary error into a response
    def format_api_error(self, http_status, detail_code, message, dev_message):
        output_object = {
            'data': {
                'httpStatusCode': int(http_status),
                'detailCode': int(detail_code),
                'message': message,
                'developerMessage': dev_message,
                'moreInfoUrl': self.doc_url_prefix + "/" + str(http_status) + "-" + str(detail_code)
            },
            'code': int(str(http_status) + str(detail_code))
        }
        return output_object

    # Common function to dump the incoming status
    def log_initial_status(self):
        logging.info(self.description)
        logging.info("Request received:\n{0}".format(simplejson.dumps(self.event)))
        logging.info("Context received:\n{0}".format(self.context))

    # Default api success function (200 code, single object response)
    def api_success(self, output_object):
        response = self.format_api_response(output_object, 200)
        logging.debug("OK Response : {0}".format(simplejson.dumps(response)))
        return response

    # Converts an arbitrary exception to the right status code
    def convert_exception_to_error_response(self, exception):
        tb = traceback.format_exc(10)
        logging.error("Converting exception {0} to error : {1}".format(exception, tb))

        if isinstance(exception, InvalidRequestError):
            LambdaAPI.object_to_raised_exception(
                    self.format_api_error(400,exception.code, exception.message, exception.message))
        elif isinstance(exception, IllegalAccessError):
            LambdaAPI.object_to_raised_exception(
                    self.format_api_error(403,exception.code, exception.message, exception.message))
        elif isinstance(exception, NoSuchResourceError):
            LambdaAPI.object_to_raised_exception(
                    self.format_api_error(404,exception.code, exception.message, exception.message))
        else:
            LambdaAPI.object_to_raised_exception(
                    self.format_api_error(500,100, 'An internal server error occurred', exception.message))

    # Converts an object to string representation and raises it as an exception
    @staticmethod
    def object_to_raised_exception(object_to_convert):
        logging.error("Raising exception : {0}".format(object_to_convert))
        raise Exception(simplejson.dumps(object_to_convert))

    # Formats arbitrary object and code into a response
    @staticmethod
    def format_api_response(output_object, code, notes=None):
        return {
            'data': output_object,
            'code': int(code),
            'notes': notes
        }

    @staticmethod
    def find_value(to_search, path):
        current = to_search
        index = 0
        while index < len(path):
            key = path[index]
            if not current.has_key(key):
                return None
            index += 1
            current = current.get(key)
        return current

    @staticmethod
    def raise_except_on_missing_value(to_search, path_list):
        missing = []
        for path in path_list:
            if LambdaAPI.find_value(to_search, path) is None:
                missing.append(":".join(path))
        if len(missing) > 0:
            errors = ",".join(missing)
            raise InvalidRequestError(100, 'Fields missing ' + errors)
