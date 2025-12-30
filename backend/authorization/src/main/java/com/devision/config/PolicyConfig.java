package com.devision.config;

import com.devision.authorization.enums.Permission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class PolicyConfig {

    /**
     * Role → Permissions mapping
     * Dùng EnumSet để type-safe và nhanh hơn
     */
    @Bean
    public Map<String, Set<Permission>> rolePolicies() {
        return Map.of(
                "ROLE_APPLICANT", EnumSet.of(
                        Permission.APPLICATION_CREATE,
                        Permission.APPLICATION_VIEW_SELF
                ),

                "ROLE_COMPANY", EnumSet.of(
                        Permission.APPLICATION_VIEW_ALL,
                        Permission.APPLICATION_UPDATE_STATUS
                ),

                "ROLE_ADMIN", EnumSet.of(Permission.ALL)
        );
    }
}
