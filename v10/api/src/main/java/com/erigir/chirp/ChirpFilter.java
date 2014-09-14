package com.erigir.chirp;

import com.erigir.chirp.exception.ChirpExceptionWriter;
import com.erigir.chirp.exception.NoSuchVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * This filter (at the moment) just sets the Max-Age header so CloudFront wont cache API calls
 * and CORS headers so we can be called from lots of places
 *
 * @author cweiss
 */
@Component(value = "chirpFilter")
public class ChirpFilter implements Filter {
    private static Logger LOG = LoggerFactory.getLogger(ChirpFilter.class);
    private ChirpExceptionWriter chirpExceptionWriter;
    private List<Integer> validVersions;

    private String corsAllowMethods;
    private Integer corsMaxAge;
    private String corsAllowHeaders;

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) arg0;
        HttpServletResponse resp = (HttpServletResponse) arg1;

        try {
            String uri = validateAndRemoveVersion(request.getRequestURI());


            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", corsAllowMethods);
            resp.setHeader("Access-Control-Max-Age", String.valueOf(corsMaxAge));
            resp.setHeader("Access-Control-Allow-Headers", corsAllowHeaders);
            resp.setHeader("Max-Age", "30");

            arg2.doFilter(arg0, arg1); // Matched a no-key regex, handle publicly
        } catch (Exception ex) {
            chirpExceptionWriter.writeExceptionToResponse(request, resp, ex);
        }

    }

    private String validateAndRemoveVersion(String input) {
        String rval = input;
        Integer foundVersion = null;
        if (input != null && input.startsWith("/v")) {
            int split = input.indexOf("/", 2);
            if (split != -1) {
                rval = input.substring(split);
                String test = input.substring(2, split);
                try {
                    foundVersion = new Integer(test);
                } catch (NumberFormatException nfe) {
                    LOG.warn("Couldn't parse version number {}", test);
                }
            }
        }

        if (foundVersion == null || !validVersions.contains(foundVersion)) {
            throw new NoSuchVersionException(validVersions);
        }
        return rval;

    }

    @Override
    public void destroy() {
        // Do nothing
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Do nothing
    }

    public void setCorsAllowMethods(String corsAllowMethods) {
        this.corsAllowMethods = corsAllowMethods;
    }

    public void setCorsAllowHeaders(String corsAllowHeaders) {
        this.corsAllowHeaders = corsAllowHeaders;
    }

    public void setChirpExceptionWriter(ChirpExceptionWriter chirpExceptionWriter) {
        this.chirpExceptionWriter = chirpExceptionWriter;
    }

    public void setCorsMaxAge(Integer corsMaxAge) {
        this.corsMaxAge = corsMaxAge;
    }

    public void setValidVersions(List<Integer> validVersions) {
        this.validVersions = validVersions;
    }
}
