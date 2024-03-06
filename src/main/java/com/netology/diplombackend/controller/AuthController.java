package com.netology.diplombackend.controller;

import com.netology.diplombackend.domain.dto.request.SignOutRequest;
import com.netology.diplombackend.domain.dto.response.JwtAuthenticationResponse;
import com.netology.diplombackend.domain.dto.request.SignInRequest;
import com.netology.diplombackend.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut (@RequestBody SignOutRequest request){
        authenticationService.signOut(request);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
