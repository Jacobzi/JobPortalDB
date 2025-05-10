package org.example.oopproject1.service;

import org.example.oopproject1.exception.ResourceNotFoundException;
import org.example.oopproject1.model.User;
import org.example.oopproject1.repository.ApplicationRepository;
import org.example.oopproject1.repository.JobRepository;
import org.example.oopproject1.repository.RecruiterRepository;
import org.example.oopproject1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruiterRepository recruiterRepository;

    // Updated constructor with all repositories
    @Autowired
    public UserService(
            UserRepository userRepository,
            @Lazy PasswordEncoder passwordEncoder,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            RecruiterRepository recruiterRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.recruiterRepository = recruiterRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User registerUser(String username, String email, String password, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setId(null);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));

        return userRepository.save(user);
    }

    // New methods for admin dashboard

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserStatus(String id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalJobs", jobRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("totalRecruiters", recruiterRepository.count());
        return stats;
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User updateUserWithPassword(String id, String newEmail, String newPassword) {
        User user = getUserById(id);

        if (newEmail != null && !newEmail.isEmpty()) {
            user.setEmail(newEmail);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }
}