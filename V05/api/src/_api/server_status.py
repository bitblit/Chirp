import datetime

from _lib.lambda_api import LambdaAPI
from _lib.illegal_access_error import IllegalAccessError
from _lib.invalid_request_error import InvalidRequestError
from _lib.no_such_resource_error import NoSuchResourceError

def lambda_handler(event, context):
    api = LambdaAPI("Getting server status", "https://my-server/api-errors", event, context)
    api.set_root_logging_level(10)
    api.log_initial_status()

    try:
        api.except_on_missing_value([
            ['body-json']
        ])

        error_code = api.fetch_value(['params','querystring', 'error'])
        if error_code is not None and len(error_code)>0:
            if str(error_code) == '500':
                raise Exception('Forced 500')
            elif str(error_code) == '404':
                raise NoSuchResourceError(100,"Forced 404")
            elif str(error_code) == '403':
                raise IllegalAccessError(100,"Forced 403")
            elif str(error_code) == '400':
                raise InvalidRequestError(100,"Forced 400")
            else:
                raise InvalidRequestError(100,
                                          "You attempted to force a code {0} but only 500, 403, 404, and 400 are supported"
                                          .format(error_code))

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
