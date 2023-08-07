package com.shameless.bookingtech.app.api;

import com.shameless.bookingtech.app.model.response.UserResponse;
import com.shameless.bookingtech.app.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/auth")
public class AuthRestController {

    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> token(Authentication authentication){
        String token = tokenService.generateToken(authentication);
        UserResponse userResponse = UserResponse.builder()
                .accessToken(token)
                .username(authentication.getName())
                .build();
        return ResponseEntity.ok().body(userResponse);
    }

}
