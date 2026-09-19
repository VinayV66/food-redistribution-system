package com.foodrescue.service;

import com.foodrescue.dto.request.LoginRequest;
import com.foodrescue.dto.request.RegisterRequest;
import com.foodrescue.dto.response.AuthResponse;

/** Service interface for authentication operations. */
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
