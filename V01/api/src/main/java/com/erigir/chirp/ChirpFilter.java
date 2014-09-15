package com.erigir.chirp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter (at the moment) just sets the Max-Age header so CloudFront wont cache API calls
 * and CORS headers so we can be called from lots of places
 *
 * @author cweiss
 */
@Component(value = "chirpFilter")
public class ChirpFilter implements Filter {
    private static Logger LOG = LoggerFactory.getLogger(ChirpFilter.class);

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) arg0;
        HttpServletResponse resp = (HttpServletResponse) arg1;

        resp.setHeader("Max-Age", "30");
        arg2.doFilter(arg0, arg1); // Matched a no-key regex, handle publicly

    }

    @Override
    public void destroy() {
        // Do nothing
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Do nothing
    }

}
