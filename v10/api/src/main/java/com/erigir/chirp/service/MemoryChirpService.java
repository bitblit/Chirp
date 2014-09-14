package com.erigir.chirp.service;

import com.erigir.chirp.model.Chirp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chrweiss on 9/13/14.
 */
public class MemoryChirpService implements ChirpService {
    private List<Chirp> cache = new LinkedList<>();

    @Override
    public List<Chirp> fetchChirps(int max) {
        List<Chirp> rval = null;
        if (max<cache.size())
        {
            rval = cache.subList(0,max);
        }
        else
        {
            rval = cache;
        }
        return Collections.unmodifiableList(rval);
    }

    @Override
    public void postChirp(Chirp c) {
        if (c!=null)
        {
            cache.add(0,c);
        }
    }
}
