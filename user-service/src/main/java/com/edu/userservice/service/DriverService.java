package com.edu.userservice.service;

import com.edu.userservice.dto.AuthResponse;
import com.edu.userservice.dto.CustomerRes;
import com.edu.userservice.dto.DriverRegRequest;
import com.edu.userservice.dto.DriverRes;

public interface DriverService {
    AuthResponse register(DriverRegRequest request);
    AuthResponse authenticate(String username, String password);
    DriverRes getUserByUsername(String username);
}
