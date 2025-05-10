package org.example.oopproject1.service;

import org.example.oopproject1.exception.ResourceNotFoundException;
import org.example.oopproject1.model.Application;
import org.example.oopproject1.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    // Non-paginated method
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // Paginated method - add this method
    public Page<Application> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    public Application getApplicationById(String id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    public Application createApplication(Application application) {
        // Ensure ID is null to force MongoDB to generate a new one
        application.setId(null);

        application.setApplicationDate(LocalDate.now());
        if (application.getStatus() == null) {
            application.setStatus(Application.ApplicationStatus.SUBMITTED);
        }
        return applicationRepository.save(application);
    }

    public Application updateApplication(String id, Application applicationDetails) {
        Application application = getApplicationById(id);

        application.setCandidateName(applicationDetails.getCandidateName());
        application.setEmail(applicationDetails.getEmail());
        application.setPhone(applicationDetails.getPhone());
        application.setResumeUrl(applicationDetails.getResumeUrl());
        application.setCoverLetterText(applicationDetails.getCoverLetterText());
        application.setStatus(applicationDetails.getStatus());

        return applicationRepository.save(application);
    }

    public void deleteApplication(String id) {
        Application application = getApplicationById(id);
        applicationRepository.delete(application);
    }

    // Non-paginated methods
    public List<Application> getApplicationsByJobId(String jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<Application> getApplicationsByEmail(String email) {
        return applicationRepository.findByEmail(email);
    }

    public List<Application> getApplicationsByStatus(Application.ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }

    // Paginated methods - add these methods
    public Page<Application> getApplicationsByJobId(String jobId, Pageable pageable) {
        return applicationRepository.findByJobId(jobId, pageable);
    }

    public Page<Application> getApplicationsByEmail(String email, Pageable pageable) {
        return applicationRepository.findByEmail(email, pageable);
    }

    public Page<Application> getApplicationsByStatus(Application.ApplicationStatus status, Pageable pageable) {
        return applicationRepository.findByStatus(status, pageable);
    }
}