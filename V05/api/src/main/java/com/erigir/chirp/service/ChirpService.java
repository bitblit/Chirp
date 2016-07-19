package com.erigir.chirp.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.erigir.chirp.model.Chirp;

import java.util.ArrayList;
import java.util.List;

public class ChirpService
{
    private DynamoDBMapper dynamoDBMapper;

    /**
     * Returns a count of all chirps
     * @return int containing the count
     */
    public int getChirpCount()
    {
       return getAllChirps().size();
    }

    public void save(Chirp chirp)
    {
        if (chirp!=null && chirp.getMessage()!=null)
        {
            dynamoDBMapper.save(chirp);
        }
    }

    public Chirp byId(String uid)
    {
        return dynamoDBMapper.load(Chirp.class, uid);
    }

    public List<Chirp> getAllChirps()
    {
        PaginatedScanList<Chirp> psl = dynamoDBMapper.scan(Chirp.class, new DynamoDBScanExpression());
        return new ArrayList<Chirp>(psl);
    }


    public void setDynamoDBMapper(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }
}
