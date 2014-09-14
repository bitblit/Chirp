package com.erigir.chirp.exception;

import java.util.List;

/**
 * Created by chrweiss on 6/28/14.
 */
@ChirpException(
        httpStatusCode = 404,
        detailCode = 400107,
        message = "Your application is currently misconfigured",
        developerMessage = "You requested a version of the API that does not currently exist",
        detailObjectPropertyName = "validVersions"
)
public class NoSuchVersionException extends RuntimeException {
    private List<Integer> validVersions;

    public NoSuchVersionException(List<Integer> validVersions) {
        this.validVersions = validVersions;
    }

    public List<Integer> getValidVersions() {
        return validVersions;
    }
}
