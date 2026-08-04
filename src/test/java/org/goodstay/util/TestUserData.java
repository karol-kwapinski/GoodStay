package org.goodstay.util;

import org.goodstay.dto.LoginRequestDto;
import org.goodstay.model.User;

public record TestUserData(
        User user,
        LoginRequestDto request
) {}
