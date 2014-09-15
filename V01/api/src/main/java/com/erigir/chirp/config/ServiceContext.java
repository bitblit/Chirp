package com.erigir.chirp.config;


import com.erigir.chirp.ChirpFilter;
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
@PropertySource("classpath:application.properties")
public class ServiceContext {
    @Value("${error.code.documentation.prefix}")
    private String errorCodeDocumentationPrefix;

    @Bean
    public Date serverStartTime() {
        return new Date();
    }

    @Bean
    public ChirpFilter chirpFilter() {
        ChirpFilter bean = new ChirpFilter();
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