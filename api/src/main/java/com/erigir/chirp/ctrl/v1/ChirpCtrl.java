package com.erigir.chirp.ctrl.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.erigir.chirp.service.ChirpService;

@Controller
@RequestMapping("/api/v1/chirp")
public class ChirpCtrl {
    private static final Logger LOG = LoggerFactory.getLogger(ChirpCtrl.class);

    @Resource(name="chirpService")
    private ChirpService chirpService;

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public
    @ResponseBody int chirpCount()
    {
        return chirpService.getChirpCount();
    }

}
