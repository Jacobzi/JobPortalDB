// Create this class if you didn't already
package org.example.oopproject1.util;

import org.example.oopproject1.model.Job;
import org.example.oopproject1.model.Recruiter;
import org.example.oopproject1.repository.JobRepository;
import org.example.oopproject1.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Override
    public void run(String... args) {
        // Check if data already exists
        if (jobRepository.count() > 0) {
            return;
        }

        // Create a recruiter
        Recruiter recruiter = new Recruiter();
        recruiter.setName("John Smith");
        recruiter.setEmail("john@example.com");
        recruiter.setCompany("ABC Corp");
        recruiter.setPosition("HR Manager");
        recruiter.setPhone("123-456-7890");
        recruiter = recruiterRepository.save(recruiter);

        // Create 20 sample jobs
        for (int i = 1; i <= 20; i++) {
            Job job = new Job();
            job.setTitle("Software Engineer " + i);
            job.setCompany(i % 3 == 0 ? "ABC Corp" : (i % 3 == 1 ? "XYZ Inc" : "Tech Solutions"));
            job.setDescription("Job description for position " + i);
            job.setRequiredSkills(Arrays.asList("Java", "Spring", "MongoDB"));
            job.setMinSalary(50000.0 + (i * 1000));
            job.setMaxSalary(80000.0 + (i * 1000));
            job.setLocation(i % 2 == 0 ? "Remote" : "New York");
            job.setEmploymentType(i % 4 == 0 ? "Contract" : "Full-time");
            job.setPostDate(LocalDate.now().minusDays(i));
            job.setDeadlineDate(LocalDate.now().plusMonths(1));
            job.setActive(true);
            job.setRecruiterId(recruiter.getId());

            jobRepository.save(job);
        }
    }
}