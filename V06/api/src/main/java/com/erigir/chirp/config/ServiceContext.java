package com.erigir.chirp.config;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.erigir.chirp.ChirpFilter;
import com.erigir.chirp.service.ChirpService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by chrweiss on 7/12/14.
 */
@Configuration
public class ServiceContext {
    @Value("${error.code.documentation.prefix}")
    private String errorCodeDocumentationPrefix;

    @Bean
    public Date serverStartTime() {
        return new Date();
    }

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new DefaultAWSCredentialsProviderChain();
    }

    @Bean
    public AmazonDynamoDB dynamoDB() {
        return new AmazonDynamoDBClient(awsCredentialsProvider());
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(dynamoDB());
    }




    @Bean
    public ChirpFilter chirpFilter() {
        ChirpFilter bean = new ChirpFilter();
        return bean;
    }

    @Bean
    public ChirpService chirpService()
    {
        ChirpService bean = new ChirpService();
        bean.setDynamoDBMapper(dynamoDBMapper());
        return bean;
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper bean = new ObjectMapper();
        bean.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        bean.configure(SerializationFeature.INDENT_OUTPUT,true);
        return bean;
    }

}