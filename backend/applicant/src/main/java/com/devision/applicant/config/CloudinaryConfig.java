package com.devision.applicant.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    private final CloudinaryProperties properties; // Inject the new bean

    // Use constructor injection for the properties object
    public CloudinaryConfig(CloudinaryProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Cloudinary cloudinaryClient() {
        Map<String, Object> config = new HashMap<>();
        // Use the properties object to access the values
        config.put("cloud_name", properties.getCloudName());
        config.put("api_key", properties.getApiKey());
        config.put("api_secret", properties.getApiSecret());
        config.put("secure", true);

        return new Cloudinary(config);
    }
}
