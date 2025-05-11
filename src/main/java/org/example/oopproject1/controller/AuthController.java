package org.example.oopproject1.controller;

import jakarta.validation.Valid;
import org.example.oopproject1.dto.JwtResponse;
import org.example.oopproject1.dto.LoginRequest;
import org.example.oopproject1.dto.MessageResponse;
import org.example.oopproject1.dto.SignupRequest;
import org.example.oopproject1.model.Recruiter;
import org.example.oopproject1.model.User;
import org.example.oopproject1.security.JwtUtils;
import org.example.oopproject1.service.RecruiterService;
import org.example.oopproject1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private RecruiterService recruiterService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getRoles()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            String role = (signupRequest.getRole() == null || signupRequest.getRole().isEmpty())
                    ? "USER"
                    : signupRequest.getRole();

            User user = userService.registerUser(
                    signupRequest.getUsername(),
                    signupRequest.getEmail(),
                    signupRequest.getPassword(),
                    role);

            // If this is a recruiter registration, create a recruiter profile too
            if (role.equals("RECRUITER")) {
                // Create a recruiter profile with the provided info
                Recruiter recruiter = new Recruiter();
                recruiter.setName(signupRequest.getUsername());
                recruiter.setEmail(signupRequest.getEmail());

                // Use provided company and phone or default values
                recruiter.setCompany(
                        signupRequest.getCompany() != null && !signupRequest.getCompany().isEmpty()
                                ? signupRequest.getCompany()
                                : "Your Company");

                // Use provided position or default
                recruiter.setPosition(
                        signupRequest.getPosition() != null && !signupRequest.getPosition().isEmpty()
                                ? signupRequest.getPosition()
                                : "Recruiter");

                recruiter.setPhone(
                        signupRequest.getPhone() != null
                                ? signupRequest.getPhone()
                                : "");

                // Save the recruiter profile
                recruiterService.createRecruiter(recruiter);
            }

            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}