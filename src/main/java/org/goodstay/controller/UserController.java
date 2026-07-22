package org.goodstay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goodstay.dto.RegisterRequestDTO;
import org.goodstay.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDTO request) {

        userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}