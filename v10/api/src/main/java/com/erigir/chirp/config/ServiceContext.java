package com.erigir.chirp.config;


import com.erigir.chirp.ChirpFilter;
import com.erigir.chirp.exception.ChirpExceptionWriter;
import com.erigir.chirp.service.ChirpService;
import com.erigir.chirp.service.MemoryChirpService;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by chrweiss on 7/12/14.
 */
@Configuration
@EnableAsync
@EnableScheduling
@EnableCaching
@PropertySource("classpath:application.properties")
public class ServiceContext {
    @Value("${error.code.documentation.prefix}")
    private String errorCodeDocumentationPrefix;
    @Value("${chirp.table.name}")
    private String chirpTableName;


    @Bean
    public static AWSCredentialsProvider awsCredentialsProvider() {
        return new DefaultAWSCredentialsProviderChain();
    }

    @Bean
    public static AmazonS3Client s3() {
        return new AmazonS3Client(awsCredentialsProvider());
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
    public ChirpExceptionWriter chirpExceptionWriter() {
        ChirpExceptionWriter sew = new ChirpExceptionWriter();
        sew.setApiDocUrlPrefix(errorCodeDocumentationPrefix);
        return sew;
    }

    @Bean
    public Date serverStartTime() {
        return new Date();
    }

    @Bean
    public ChirpService accountService() {
        MemoryChirpService bean = new MemoryChirpService();
        return bean;
    }

    @Bean
    public DynamoDBMapperConfig accountDynamoDBMapperConfig() {
        DynamoDBMapperConfig bean = new DynamoDBMapperConfig(new DynamoDBMapperConfig.TableNameOverride(chirpTableName));
        return bean;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ChirpFilter chirpFilter() {
        ChirpFilter bean = new ChirpFilter();
        bean.setCorsAllowHeaders("x-requested-with, Content-Type");
        bean.setCorsAllowMethods("GET, PUT, POST, OPTIONS");
        bean.setCorsMaxAge(600);
        bean.setChirpExceptionWriter(chirpExceptionWriter());
        bean.setValidVersions(Arrays.asList(1));
        return bean;
    }

    @Bean
    public TaskScheduler scheduler() {
        ThreadPoolTaskScheduler bean = new ThreadPoolTaskScheduler();
        bean.setPoolSize(10);
        return bean;
    }

    @Bean
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor bean = new ThreadPoolTaskExecutor();
        bean.setCorePoolSize(5);
        bean.setMaxPoolSize(15);
        return bean;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper bean = new ObjectMapper();
        bean.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        bean.configure(SerializationFeature.INDENT_OUTPUT,true);
        return bean;
    }


    @Bean
    public CacheManager cacheManager() {
        // configure and return an implementation of Spring's CacheManager SPI
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("chirpCache")));
        return cacheManager;
    }


}