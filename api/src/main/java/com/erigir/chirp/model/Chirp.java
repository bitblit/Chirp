package com.erigir.chirp.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 * An account submission plus all the fields we generate
 * Created by chrweiss on 6/28/14.
 */
@DynamoDBTable(tableName = "placeholder")
public class Chirp {
    /**
     * The 'owner' of this chirp
     */
    @NotEmpty(message = "Contact email is required")
    @Email(message = "Contact email is must be a valid email address")
    private String contactEmail;

    /**
     * The message
     */
    @NotEmpty(message = "Message is required")
    private String message;


    /**
     * The time of creation
     */
    @NotEmpty(message = "timestamp is required")
    private Date timestamp;


    @DynamoDBHashKey
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @DynamoDBAttribute
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @DynamoDBRangeKey
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
