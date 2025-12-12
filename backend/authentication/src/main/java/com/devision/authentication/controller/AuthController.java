package com.devision.authentication.controller;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AuthToApplicantEvent;
import com.devision.authentication.dto.*;
import com.devision.authentication.jwt.JwtService;
import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.user.User;
import com.devision.authentication.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer;

    public AuthController(UserService userService,
                          JwtService jwtService,
                          KafkaGenericProducer<AuthToApplicantEvent> kafkaProducer) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.kafkaProducer = kafkaProducer;
    }

    // -------- REGISTER (LOCAL) --------
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest request) {
        // 1. Create local user
        User user = userService.registerLocalUser(request);

        // 2. Send Kafka event to Applicant service
        AuthToApplicantEvent event = new AuthToApplicantEvent(
                user.getCorrelationId(),
                user.getId(),
                user.getEmail(),
                user.getFullName()
        );
        kafkaProducer.sendMessage(KafkaConstant.AUTHENTICATION_TOPIC, event);

        // 3. Generate JWT
        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getApplicantId(),  // might be null until Kafka response arrives
                user.getEmail(),
                user.getFullName()
        );
    }

    // -------- LOGIN (LOCAL) --------
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userService.loginLocalUser(request);

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getApplicantId(),
                user.getEmail(),
                user.getFullName()
        );
    }
}
