package org.example.oopproject1.service;

import org.example.oopproject1.exception.ResourceNotFoundException;
import org.example.oopproject1.model.Recruiter;
import org.example.oopproject1.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecruiterService {

    @Autowired
    private RecruiterRepository recruiterRepository;

    // Non-paginated method
    public List<Recruiter> getAllRecruiters() {
        return recruiterRepository.findAll();
    }

    // Paginated method - add this method
    public Page<Recruiter> getAllRecruiters(Pageable pageable) {
        return recruiterRepository.findAll(pageable);
    }

    public Recruiter getRecruiterById(String id) {
        return recruiterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with id: " + id));
    }

    public Recruiter getRecruiterByEmail(String email) {
        return recruiterRepository.findByEmail(email)
                .orElse(null);
    }

    public Recruiter createRecruiter(Recruiter recruiter) {

        recruiter.setId(null);

        return recruiterRepository.save(recruiter);
    }

    public Recruiter updateRecruiter(String id, Recruiter recruiterDetails) {
        Recruiter recruiter = getRecruiterById(id);

        recruiter.setName(recruiterDetails.getName());
        recruiter.setEmail(recruiterDetails.getEmail());
        recruiter.setCompany(recruiterDetails.getCompany());
        recruiter.setPosition(recruiterDetails.getPosition());
        recruiter.setPhone(recruiterDetails.getPhone());

        return recruiterRepository.save(recruiter);
    }

    public void deleteRecruiter(String id) {
        Recruiter recruiter = getRecruiterById(id);
        recruiterRepository.delete(recruiter);
    }

    // Non-paginated method
    public List<Recruiter> getRecruitersByCompany(String company) {
        return recruiterRepository.findByCompany(company);
    }

    // Paginated method - add this method
    public Page<Recruiter> getRecruitersByCompany(String company, Pageable pageable) {
        return recruiterRepository.findByCompany(company, pageable);
    }
}