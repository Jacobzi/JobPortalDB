package org.example.oopproject1.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.example.oopproject1.model.Job;
import org.example.oopproject1.model.User;
import org.example.oopproject1.service.JobService;
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

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // Add paginated endpoint
    @GetMapping("/paged")
    public ResponseEntity<Page<Job>> getPagedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(jobService.getAllJobs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable String id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    public ResponseEntity<Job> createJob(@Valid @RequestBody Job job, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        // If admin, bypass company validation
        if (currentUser.getRoles().contains("ADMIN")) {
            return new ResponseEntity<>(jobService.createJob(job), HttpStatus.CREATED);
        }
        // For recruiters, enforce company validation
        else {
            return new ResponseEntity<>(jobService.createJob(job, currentUser.getEmail()), HttpStatus.CREATED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable String id, @Valid @RequestBody Job job) {
        return ResponseEntity.ok(jobService.updateJob(id, job));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteJob(@PathVariable String id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/company/{company}")
    public ResponseEntity<List<Job>> getJobsByCompany(@PathVariable String company) {
        return ResponseEntity.ok(jobService.findJobsByCompany(company));
    }

    // Add paginated version
    @GetMapping("/company/{company}/paged")
    public ResponseEntity<Page<Job>> getPagedJobsByCompany(
            @PathVariable String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(jobService.findJobsByCompany(company, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobsByTitle(@RequestParam String title) {
        return ResponseEntity.ok(jobService.findJobsByTitle(title));
    }

    // Add paginated version
    @GetMapping("/search/paged")
    public ResponseEntity<Page<Job>> searchPagedJobsByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(jobService.findJobsByTitle(title, pageable));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Job>> getActiveJobsByLocation(@PathVariable String location) {
        return ResponseEntity.ok(jobService.findActiveJobsByLocation(location));
    }

    // Add paginated version
    @GetMapping("/location/{location}/paged")
    public ResponseEntity<Page<Job>> getPagedActiveJobsByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(jobService.findActiveJobsByLocation(location, pageable));
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<Job>> getJobsByRecruiter(@PathVariable String recruiterId) {
        return ResponseEntity.ok(jobService.findJobsByRecruiter(recruiterId));
    }

    // Add paginated version
    @GetMapping("/recruiter/{recruiterId}/paged")
    public ResponseEntity<Page<Job>> getPagedJobsByRecruiter(
            @PathVariable String recruiterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(jobService.findJobsByRecruiter(recruiterId, pageable));
    }
}