package com.erigir.chirp.ctrl.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Serves general information about the server
 * <p/>
 * cweiss : 6/28/14 11:53 AM
 */
@Controller
@RequestMapping("/v1/info")
public class InformationCtrl {
    private static final Logger LOG = LoggerFactory.getLogger(InformationCtrl.class);


    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> serverInfo() {
        Map<String, Object> rval = new TreeMap<String, Object>();
        rval.put("serverTime", new Date());
        return rval;
    }

    @RequestMapping(value = "/force-500", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> force500Error() {
        throw new IllegalArgumentException("Forced 500 error");
    }



}
