package com.erigir.chirp.exception;

import com.erigir.chirp.model.ChirpResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;

/**
 * Basically making this a seperate class so I can use it in both the filter and the exception handler
 * Created by chrweiss on 7/6/14.
 */
public class ChirpExceptionWriter {
    private static final Logger LOG = LoggerFactory.getLogger(ChirpExceptionWriter.class);
    private ObjectMapper objectMapper = createMapper();
    private String apiDocUrlPrefix;

    private ObjectMapper createMapper() {
        ObjectMapper rval = new ObjectMapper();
        rval.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        rval.configure(SerializationFeature.INDENT_OUTPUT, true);
        return rval;
    }

    public void writeExceptionToResponse(HttpServletRequest request, HttpServletResponse resp, Exception inEx) {
        try {
            Exception ex = preprocessException(inEx);


            LOG.debug("Handling failure to {}", request.getRequestURI(), ex);

            ChirpException se = AnnotationUtils.findAnnotation(ex.getClass(), ChirpException.class);

            ChirpErrorData sed = null;
            if (se == null) {
                sed = new ChirpErrorData(500, 500000, "An unexpected error occurred", ex.getClass().getSimpleName(), apiDocUrlPrefix + 500000, ex.getLocalizedMessage());
            } else {
                Object details = (StringUtils.trimToNull(se.detailObjectPropertyName()) == null) ? null : safeGetProperty(ex, se.detailObjectPropertyName());
                sed = new ChirpErrorData(se.httpStatusCode(), se.detailCode(), se.message(), se.developerMessage(), apiDocUrlPrefix + se.detailCode(), details);
            }

            resp.setStatus(sed.getHttpStatusCode());
            objectMapper.writeValue(resp.getOutputStream(), new ChirpResponse<ChirpErrorData>(sed, sed.getHttpStatusCode()));
        } catch (Exception e) {
            LOG.error("Really bad!  Error when trying to write error", e);
        }
    }

    /**
     * Converts certain types of generic exceptions to more usable and secure ones
     *
     * @param input
     * @return
     */
    private Exception preprocessException(Exception input) {
        Exception rval = input;
        if (HttpMessageNotReadableException.class.isAssignableFrom(input.getClass()) && JsonMappingException.class.isAssignableFrom(input.getCause().getClass())) {
            JsonMappingException jme = (JsonMappingException) input.getCause();
            rval = new BadJsonException(jme.getLocation());
            rval.initCause(input);
        }
        return rval;
    }

    private Object safeGetProperty(Object bean, String propName) {
        Object rval = null;
        if (bean != null && propName != null)
            try {
                rval = PropertyUtils.getProperty(bean, propName);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOG.error("Invalid property name '{}' on bean of type '{}' ", propName, bean.getClass());
            }
        return rval;
    }

    public void setApiDocUrlPrefix(String apiDocUrlPrefix) {
        this.apiDocUrlPrefix = apiDocUrlPrefix;
    }
}
