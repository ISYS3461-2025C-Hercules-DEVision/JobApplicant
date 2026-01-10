package com.devision.applicant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShardRouter {
    private final Map<String, MongoTemplate> shardTemplates;
    private final MongoTemplate defaultTemplate;

    public MongoTemplate getTemplateForCountry(String country){
        if(country == null) return defaultTemplate;

        return shardTemplates.getOrDefault(country.toUpperCase(), defaultTemplate);
    }
}
