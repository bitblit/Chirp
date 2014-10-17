package com.erigir.chirp.service;

import com.erigir.chirp.model.Chirp;

import java.util.List;

/**
 * Created by chrweiss on 7/1/14.
 */
public interface ChirpService {

    List<Chirp> fetchChirps(int max);
    void postChirp(Chirp c);

}
