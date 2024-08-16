package com.agroproserver.serveragropro.controller;

import org.springframework.web.bind.annotation.RestController;

import com.agroproserver.serveragropro.payload.request.ChangePasswordRequest;
import com.agroproserver.serveragropro.payload.request.LoginRequest;
import com.agroproserver.serveragropro.payload.request.SignUpRequest;
import com.agroproserver.serveragropro.service.AuthService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;
    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody List<SignUpRequest> signUpRequests, BindingResult bindingResult){
        return authService.signup(signUpRequests, bindingResult);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        return authService.login(loginRequest, bindingResult);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, BindingResult bindingResult) {
        return authService.changePassword(changePasswordRequest, bindingResult);
    }

}
