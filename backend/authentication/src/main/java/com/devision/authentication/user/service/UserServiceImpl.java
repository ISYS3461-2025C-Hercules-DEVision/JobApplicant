package com.devision.authentication.user.service;

import com.devision.authentication.dto.HandleChangeStatusReqDto;
import com.devision.authentication.dto.LoginRequest;
import com.devision.authentication.dto.RegisterRequest;
import com.devision.authentication.user.entity.User;
import com.devision.authentication.user.entity.UserRole;
import com.devision.authentication.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + email
                ));
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public User registerLocalUser(RegisterRequest request) {

        userRepository.findByEmail(request.email())
                .ifPresent(u -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
                });

        User user = User.builder()
                .email(request.email())
                .fullName(request.fullName())
                .password(passwordEncoder.encode(request.password()))
                .provider("LOCAL")
                .role(UserRole.USER)
                .status(true)
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User loginLocalUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"
                ));

//        if (!"LOCAL".equals(user.getProvider())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This account is registered via Google");
//        }
//
//        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
//        }
//
//        if (Boolean.FALSE.equals(user.getStatus())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been banned.");
//        }
//
//        if (user.getRole() != UserRole.USER) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User only");
//        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User loginLocalAdmin(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"
                ));

//        if (!"LOCAL".equals(user.getProvider())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This account is registered via Google");
//        }
//
//        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
//        }
//
//        if (Boolean.FALSE.equals(user.getStatus())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been banned.");
//        }
//
//        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SUPER_ADMIN) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: admin only");
//        }

        return user;
    }

    @Override
    @Transactional
    public User handleGoogleLogin(Map<String, Object> attributes) {

        String email = (String) attributes.get("email");
        String name  = (String) attributes.get("name");
        String sub   = (String) attributes.get("sub");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .fullName(name)
                        .provider("GOOGLE")
                        .providerId(sub)
                        .role(UserRole.USER)
                        .status(true)
                        .build()
                );
        return userRepository.save(user);
    }

    @Override
    public void updateStatus(HandleChangeStatusReqDto dto) {
        userRepository.findByApplicantId(dto.id())
                .map(user -> {
                    user.setStatus(dto.status());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalStateException("User not found for applicantId=" + dto.id()));
    }

    @Override
    @Transactional
    public void attachApplicantToUser(String userId, String applicantId) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    user.setApplicantId(applicantId);
                    userRepository.save(user);
                }, () -> {
                    throw new IllegalStateException("User not found for id=" + userId);
                });
    }

    @Override
    @Transactional
    public void attachAdminToUser(String userId, String adminId) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    user.setAdminId(adminId);
                    userRepository.save(user);
                }, () -> {
                    throw new IllegalStateException("User not found for id=" + userId);
                });
    }
}
