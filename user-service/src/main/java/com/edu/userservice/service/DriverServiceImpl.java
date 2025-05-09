package com.edu.userservice.service;

import com.edu.userservice.dto.AuthResponse;
import com.edu.userservice.dto.CustomerRes;
import com.edu.userservice.dto.DriverRegRequest;
import com.edu.userservice.dto.DriverRes;
import com.edu.userservice.model.Customer;
import com.edu.userservice.model.Driver;
import com.edu.userservice.repository.DriverRepository;
import com.edu.userservice.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private final DriverRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(DriverRegRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Create and save the new user
        var user = Driver.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .nic(request.getNic())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Generate JWT token
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .build();
    }

    @Override
    public AuthResponse authenticate(String username, String password) {
        // Use the AuthenticationManager to authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // If authentication is successful, retrieve the user
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Generate JWT token
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .build();
    }

    @Override
    public DriverRes getUserByUsername(String username) {
        Driver driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        BigDecimal numberAsBigDecimal = BigDecimal.ZERO;
        try {
            numberAsBigDecimal = new BigDecimal(driver.getPhoneNumber()); // assuming 'phoneNumber' is the 'number' field
        } catch (NumberFormatException e) {
            // Handle invalid number format if needed
        }

        return DriverRes.builder()
                .id(driver.getId())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .nic(driver.getNic())
                .build();
    }
}
