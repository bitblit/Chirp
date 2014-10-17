package com.erigir.chirp.ctrl.v1;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

/**
 * Created by chrweiss on 9/18/14.
 */
public class TestInformationCtrl {

    @Test
    public void testServerInfo()
    {
        InformationCtrl ctrl = new InformationCtrl();
        Map<String,Object> m = ctrl.serverInfo();

        assertNotNull(m.get("serverTime"));
    }
}
