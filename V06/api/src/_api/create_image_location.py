
from _lib.lambda_api import LambdaAPI
from _lib.chirp_service import ChirpService

def lambda_handler(event, context):
    api = LambdaAPI("Creating image location", "https://my-server/api-errors", event, context)
    api.set_root_logging_level(10)
    api.log_initial_status()
    chirp_service = ChirpService()


    try:
        api.except_on_missing_value([
        ])

        location = chirp_service.create_image_location()

        return api.api_success(location)

    except Exception as exception:
        api.convert_exception_to_error_response(exception)

if __name__ == '__main__':
    LambdaAPI.run_lambda_function_local(__file__, lambda_handler)
