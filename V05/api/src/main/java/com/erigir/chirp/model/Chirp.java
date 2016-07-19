package com.erigir.chirp.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by chrweiss on 10/22/14.
 */
@DynamoDBTable(tableName = "chirps")
public class Chirp {
    private String uid = UUID.randomUUID().toString();
    private Date created = new Date();
    private String message;

    @DynamoDBHashKey
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @DynamoDBAttribute
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @DynamoDBAttribute
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
