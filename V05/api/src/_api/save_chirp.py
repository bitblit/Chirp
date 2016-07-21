
from _lib.lambda_api import LambdaAPI
from _lib.chirp_service import ChirpService

def lambda_handler(event, context):
    api = LambdaAPI("Saving chirp", "https://my-server/api-errors", event, context)
    api.set_root_logging_level(10)
    api.log_initial_status()
    chirp_service = ChirpService()


    try:
        api.except_on_missing_value([
            ['body-json','chirp-text']
        ])

        text = api.fetch_value(['body-json','chirp-text'])
        key = chirp_service.save(text)

        return api.api_success(key)

    except Exception as exception:
        api.convert_exception_to_error_response(exception)

if __name__ == '__main__':
    LambdaAPI.run_lambda_function_local(__file__, lambda_handler)
