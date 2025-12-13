package com.devdivision.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionTester implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    public DatabaseConnectionTester(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            // Simple ping command to check connectivity
            mongoTemplate.executeCommand("{ ping: 1 }");

            String dbName = mongoTemplate.getDb().getName();
            System.out.println("✅ SUCCESSFULLY CONNECTED TO MONGODB!");
            System.out.println("Database: " + dbName);
        } catch (Exception e) {
            System.err.println("❌ FAILED TO CONNECT TO MONGODB:");
            e.printStackTrace();
        }
    }
}
