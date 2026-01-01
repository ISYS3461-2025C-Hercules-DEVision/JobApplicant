package com.devision.authentication.controller;

import com.devision.authentication.config.KafkaConstant;
import com.devision.authentication.connection.AuthToApplicantEvent;
import com.devision.authentication.dto.*;
import com.devision.authentication.jwt.JwtService;
import com.devision.authentication.kafka.kafka_producer.KafkaGenericProducer;
import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
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
        //  Create local user
        User user = userService.registerLocalUser(request);
        // create a correlationId
        String correlationId = UUID.randomUUID().toString();
        //  Send Kafka event to Applicant service
        AuthToApplicantEvent event = new AuthToApplicantEvent(
                correlationId,
                user.getEmail(),
                user.getFullName(),
                request.phoneNumber(),
                request.country(),
                request.city(),
                request.streetAddress()
        );
        kafkaProducer.sendMessage(KafkaConstant.AUTHENTICATION_APPLICANT_TOPIC, event);
        jwtUserDto jwtUser = new jwtUserDto(
                user.getId(),
                user.getEmail(),
                user.getApplicantId(),
                user.getRole()
        );
        // 3. Generate JWT
        String token = jwtService.generateToken(jwtUser);

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

        jwtUserDto jwtUser = new jwtUserDto(
                user.getId(),
                user.getEmail(),
                user.getApplicantId(),
                user.getRole()
        );
        // 3. Generate JWT
        String token = jwtService.generateToken(jwtUser);

        return new AuthResponse(
                token,
                user.getId(),
                user.getApplicantId(),
                user.getEmail(),
                user.getFullName()
        );
    }
    @PostMapping("/admin/login")
    public AuthAdminResponse adminLogin(@RequestBody LoginRequest request) {
        User user = userService.loginLocalAdmin(request);

        jwtUserDto jwtUser = new jwtUserDto(
                user.getId(),
                user.getEmail(),
                user.getApplicantId(),
                user.getRole()
        );
        // 3. Generate JWT
        String token = jwtService.generateToken(jwtUser);

        return new AuthAdminResponse(
                token,
                user.getId(),
                user.getAdminId(),
                user.getEmail()
        );
    }
}
