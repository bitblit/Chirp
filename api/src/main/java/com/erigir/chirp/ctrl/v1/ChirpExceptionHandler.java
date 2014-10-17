package com.erigir.chirp.ctrl.v1;

import com.erigir.chirp.exception.ChirpExceptionWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chrweiss on 7/6/14.
 */
@ControllerAdvice
public class ChirpExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ChirpExceptionHandler.class);

    @Resource(name = "chirpExceptionWriter")
    private ChirpExceptionWriter chirpExceptionWriter;

    @ExceptionHandler(value = Exception.class)
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse resp, Exception ex) {
        chirpExceptionWriter.writeExceptionToResponse(request, resp, ex);
        return null;
    }

}
