package org.example.oopproject1.repository;

import org.example.oopproject1.model.Recruiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterRepository extends MongoRepository<Recruiter, String> {
    Page<Recruiter> findAll(Pageable pageable);
    Page<Recruiter> findByCompany(String company, Pageable pageable);

    // Keep existing methods
    List<Recruiter> findByCompany(String company);
    Optional<Recruiter> findByEmail(String email);
}