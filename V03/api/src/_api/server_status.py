import datetime

from _lib.lambda_api import LambdaAPI
from _lib.illegal_access_error import IllegalAccessError
from _lib.invalid_request_error import InvalidRequestError
from _lib.no_such_resource_error import NoSuchResourceError

def lambda_handler(event, context):
    api = LambdaAPI("Creating text item", "https://ms-spike.timecommerce.net/api-errors", event, context)
    api.log_initial_status()

    try:
        api.except_on_missing_value([
            ['body-json']
        ])

        error_code = api.fetch_value(['query', 'error'])
        if error_code is not None:
            if str(error_code) == '500':
                raise Exception('Forced 500')
            elif str(error_code) == '404':
                raise NoSuchResourceError(100,"Forced 404")
            elif str(error_code) == '403':
                raise IllegalAccessError(100,"Forced 403")
            else:
                raise InvalidRequestError(100,
                                          "You attempted to force a code " + str(
                                    error_code) + " but only 500, 403, and 404 are supported",
                                "Bad Request")

        now = datetime.datetime.now()

        to_output = {
            'version': '4',
            'serverTime': int(now.strftime('%s')),
            'serverTimeFormatted': now.strftime('%Y-%m-%d %H:%M:%S'),
            'serverStartTime': int(now.strftime('%s')),
            'serverStartTimeFormatted': now.strftime('%Y-%m-%d %H:%M:%S'),
            'status': 'ok'
        }

        return api.api_success(to_output)
    except Exception as exception:
        api.convert_exception_to_error_response(exception)

if __name__ == '__main__':
    LambdaAPI.run_lambda_function_local(__file__, lambda_handler)
