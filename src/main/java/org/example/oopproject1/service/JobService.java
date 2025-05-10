package org.example.oopproject1.service;

import org.example.oopproject1.exception.ResourceNotFoundException;
import org.example.oopproject1.model.Job;
import org.example.oopproject1.model.Recruiter;
import org.example.oopproject1.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private RecruiterService recruiterService;

    // Add paginated methods
    public Page<Job> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    // Keep existing non-paginated method
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(String id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    // Method for recruiters with company validation
    public Job createJob(Job job, String recruiterEmail) {
        // Get recruiter information
        Recruiter recruiter = recruiterService.getRecruiterByEmail(recruiterEmail);
        if (recruiter == null) {
            throw new RuntimeException("Recruiter profile not found");
        }

        // Validate company match
        if (!recruiter.getCompany().equals(job.getCompany())) {
            throw new RuntimeException("Recruiters can only post jobs for their own company: " + recruiter.getCompany());
        }

        // Set recruiter ID
        job.setRecruiterId(recruiter.getId());

        // Continue with existing job creation logic
        job.setPostDate(LocalDate.now());
        if (job.getDeadlineDate() == null) {
            job.setDeadlineDate(LocalDate.now().plusMonths(1));
        }
        job.setActive(true);
        return jobRepository.save(job);
    }

    // Original method for admin use
    public Job createJob(Job job) {
        job.setPostDate(LocalDate.now());
        if (job.getDeadlineDate() == null) {
            job.setDeadlineDate(LocalDate.now().plusMonths(1));
        }
        job.setActive(true);
        return jobRepository.save(job);
    }

    public Job updateJob(String id, Job jobDetails) {
        Job job = getJobById(id);

        job.setTitle(jobDetails.getTitle());
        job.setCompany(jobDetails.getCompany());
        job.setDescription(jobDetails.getDescription());
        job.setRequiredSkills(jobDetails.getRequiredSkills());
        job.setMinSalary(jobDetails.getMinSalary());
        job.setMaxSalary(jobDetails.getMaxSalary());
        job.setLocation(jobDetails.getLocation());
        job.setEmploymentType(jobDetails.getEmploymentType());
        job.setDeadlineDate(jobDetails.getDeadlineDate());
        job.setActive(jobDetails.isActive());

        return jobRepository.save(job);
    }

    public void deleteJob(String id) {
        Job job = getJobById(id);
        jobRepository.delete(job);
    }

    // Add paginated methods
    public Page<Job> findJobsByCompany(String company, Pageable pageable) {
        return jobRepository.findByCompany(company, pageable);
    }

    public Page<Job> findJobsByTitle(String title, Pageable pageable) {
        return jobRepository.findByTitleContaining(title, pageable);
    }

    public Page<Job> findActiveJobsByLocation(String location, Pageable pageable) {
        return jobRepository.findByLocationAndIsActiveTrue(location, pageable);
    }

    public Page<Job> findJobsByRecruiter(String recruiterId, Pageable pageable) {
        return jobRepository.findByRecruiterId(recruiterId, pageable);
    }

    // Keep existing methods
    public List<Job> findJobsByCompany(String company) {
        return jobRepository.findByCompany(company);
    }

    public List<Job> findJobsByTitle(String title) {
        return jobRepository.findByTitleContaining(title);
    }

    public List<Job> findActiveJobsByLocation(String location) {
        return jobRepository.findByLocationAndIsActiveTrue(location);
    }

    public List<Job> findJobsByRecruiter(String recruiterId) {
        return jobRepository.findByRecruiterId(recruiterId);
    }

    // Add these methods for keyword search
    public List<Job> searchJobs(String keyword) {
        return jobRepository.searchByKeyword(keyword);
    }

    public Page<Job> searchJobs(String keyword, Pageable pageable) {
        return jobRepository.searchByKeyword(keyword, pageable);
    }
}