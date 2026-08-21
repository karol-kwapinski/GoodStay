package org.goodstay.service;

import org.goodstay.dto.LoginRequestDto;
import org.goodstay.dto.CurrentUserDto;
import org.goodstay.dto.LoginResultDto;
import org.goodstay.dto.RegisterRequestDto;
import org.goodstay.exception.EmailAlreadyExistsException;
import org.goodstay.exception.PasswordMismatchException;
import org.goodstay.model.User;
import org.goodstay.model.UserRole;
import org.goodstay.repository.UserRepository;
import org.goodstay.security.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final long expirationTime;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JWTUtil jwtUtil,
            @Value("${jwt.expiration}") long expirationTime
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.expirationTime = expirationTime;
    }

    @Transactional
    public void register(RegisterRequestDto request) {

        if (!request.password().equals(request.confirmPassword())) {
            throw new PasswordMismatchException();
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();

        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCountry(request.country());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setRole(UserRole.USER);

        userRepository.save(user);

    }

    public LoginResultDto login(LoginRequestDto request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);

        CurrentUserDto currentUserDto = new CurrentUserDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getCountry(),
                user.getRole().name());

        return new LoginResultDto(
                token,
                currentUserDto,
                expirationTime
        );
    }

    public CurrentUserDto getCurrentUser(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        return new CurrentUserDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getCountry(),
                user.getRole().name()
        );
    }
}
