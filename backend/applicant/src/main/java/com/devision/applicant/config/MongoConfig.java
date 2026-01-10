//package com.devision.applicant.config;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class MongoConfig {
//    @Value("${spring.mongodb.uri}")
//    private String defaultUri;
//
//    @Value("${mongodb.us.uri}")
//    private String usUri;
//
//    @Value("${mongodb.uk.uri}")
//    private String ukUri;
//
//    @Value("${mongodb.au.uri}")
//    private String auUri;
//
//    @Bean
//    public MongoTemplate defaultTemplate() {
//        MongoClient defaultMongoClient = MongoClients.create(defaultUri);
//        return new MongoTemplate(defaultMongoClient, "ja_applicant_vn");
//    }
//
//    @Bean
//    public MongoTemplate usTemplate() {
//        MongoClient usMongoClient = MongoClients.create(usUri);
//        return new MongoTemplate(usMongoClient, "ja_applicant_us");
//    }
//
//    @Bean
//    public MongoTemplate ukTemplate() {
//        MongoClient ukMongoClient = MongoClients.create(ukUri);
//        return new MongoTemplate(ukMongoClient, "ja_applicant_uk");
//    }
//
//    @Bean
//    public MongoTemplate auTemplate() {
//        MongoClient auMongoClient = MongoClients.create(auUri);
//        return new MongoTemplate(auMongoClient, "ja_applicant_au");
//    }
//
//    @Bean
//    public Map<String, MongoTemplate> shardTemplates(MongoTemplate defaultTemplate,
//                                                     MongoTemplate usTemplate,
//                                                     MongoTemplate ukTemplate,
//                                                     MongoTemplate auTemplate) {
//        Map<String, MongoTemplate> templates = new HashMap<>();
//        templates.put("VN", defaultTemplate);
//        templates.put("AU", auTemplate);
//        templates.put("UK", ukTemplate);
//        templates.put("US", usTemplate);
//        return templates;
//    }
//}
