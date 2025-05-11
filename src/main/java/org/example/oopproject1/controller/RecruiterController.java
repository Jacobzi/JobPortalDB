package org.example.oopproject1.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.example.oopproject1.model.Recruiter;
import org.example.oopproject1.model.User;
import org.example.oopproject1.service.RecruiterService;
import org.example.oopproject1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recruiters")
public class RecruiterController {

    @Autowired
    private RecruiterService recruiterService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Recruiter>> getAllRecruiters() {
        return ResponseEntity.ok(recruiterService.getAllRecruiters());
    }

    // Add this new endpoint to search by email
    @GetMapping("/byEmail")
    public ResponseEntity<Recruiter> getRecruiterByEmail(@RequestParam String email) {
        Optional<Recruiter> recruiter = recruiterService.getRecruiterByEmail(email);
        return recruiter.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Paginated endpoint
    @GetMapping("/paged")
    public ResponseEntity<Page<Recruiter>> getPagedRecruiters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(recruiterService.getAllRecruiters(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recruiter> getRecruiterById(@PathVariable String id) {
        return ResponseEntity.ok(recruiterService.getRecruiterById(id));
    }

    @PostMapping
    public ResponseEntity<Recruiter> createRecruiter(@Valid @RequestBody Recruiter recruiter) {
        // Force MongoDB to auto-generate ID
        recruiter.setId(null);

        // Create a user with RECRUITER role
        try {
            userService.registerUser(
                    recruiter.getName(), // Use name as username
                    recruiter.getEmail(),
                    "recruiter123", // Default password
                    "RECRUITER"
            );
        } catch (Exception e) {
            // If user already exists, continue anyway
            System.out.println("Note: User may already exist: " + e.getMessage());
        }

        // Then create the recruiter
        Recruiter createdRecruiter = recruiterService.createRecruiter(recruiter);

        return new ResponseEntity<>(createdRecruiter, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recruiter> updateRecruiter(
            @PathVariable String id,
            @Valid @RequestBody Recruiter recruiter) {
        return ResponseEntity.ok(recruiterService.updateRecruiter(id, recruiter));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecruiter(@PathVariable String id) {
        recruiterService.deleteRecruiter(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/company/{company}")
    public ResponseEntity<List<Recruiter>> getRecruitersByCompany(@PathVariable String company) {
        return ResponseEntity.ok(recruiterService.getRecruitersByCompany(company));
    }

    // Paginated version
    @GetMapping("/company/{company}/paged")
    public ResponseEntity<Page<Recruiter>> getPagedRecruitersByCompany(
            @PathVariable String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(recruiterService.getRecruitersByCompany(company, pageable));
    }
}