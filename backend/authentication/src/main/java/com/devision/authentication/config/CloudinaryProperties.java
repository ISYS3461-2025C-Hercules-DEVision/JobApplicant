package com.devision.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data // Lombok to generate getters/setters
@Component // Make it a Spring bean
// VVV Maps properties starting with 'cloudinary.' VVV
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryProperties {

    private String cloudName;
    private String apiKey;
    private String apiSecret;

    // Note: The variable names (cloudName, apiKey) must match
    // the property names in application.properties exactly.
}
