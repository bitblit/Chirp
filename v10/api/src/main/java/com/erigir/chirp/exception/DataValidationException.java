package com.erigir.chirp.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by chrweiss on 6/28/14.
 */
@ChirpException(
        httpStatusCode = 400,
        detailCode = 400100,
        message = "There are invalid values in the data submitted",
        developerMessage = "Data does not pass schema validation - check json schema and details object",
        detailObjectPropertyName = "errorMap"
)
public class DataValidationException extends RuntimeException {
    private Map<String, String> errorMap;

    public DataValidationException(BindingResult errors) {
        super();
        errorMap = toErrorMap(errors);
    }

    public DataValidationException(Map<String, String> errorMap) {
        super();
        this.errorMap = errorMap;
    }

    private static Map<String, String> toErrorMap(BindingResult errors) {
        TreeMap<String, String> rval = new TreeMap<>();

        for (FieldError fe : errors.getFieldErrors()) {
            rval.put(fe.getField(), fe.getDefaultMessage());
        }

        return rval;
    }

    public Map<String, String> getErrorMap() {
        return errorMap;
    }
}
