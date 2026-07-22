package org.goodstay.service;

import lombok.RequiredArgsConstructor;
import org.goodstay.dto.RegisterRequestDTO;
import org.goodstay.exception.EmailAlreadyExistsException;
import org.goodstay.exception.PasswordMismatchException;
import org.goodstay.model.User;
import org.goodstay.model.UserRole;
import org.goodstay.respository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequestDTO request) {

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
}
