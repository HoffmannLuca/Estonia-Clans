package com.rust.estonia.discord.bot.clans.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Autowired
    private Environment env;

    @Bean
    public MongoClient mongoClient() {

        String username = env.getProperty("DB_USERNAME");
        String password = env.getProperty("DB_PASSWORD");
        String hostIp = env.getProperty("DB_HOST_IP");
        String port = env.getProperty("DB_PORT");
        String databaseName = env.getProperty("DB_DATABASE_NAME");

        return MongoClients.create("mongodb://"+username+":"+password+"@"+hostIp+":"+port+"/"+databaseName);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), "myMongoDB");

        return mongoTemplate;

    }
}
