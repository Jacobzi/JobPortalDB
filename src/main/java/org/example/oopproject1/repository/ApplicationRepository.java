package org.example.oopproject1.repository;

import org.example.oopproject1.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    Page<Application> findAll(Pageable pageable);
    Page<Application> findByJobId(String jobId, Pageable pageable);
    Page<Application> findByEmail(String email, Pageable pageable);
    Page<Application> findByStatus(Application.ApplicationStatus status, Pageable pageable);

    // Keep existing methods
    List<Application> findByJobId(String jobId);
    List<Application> findByEmail(String email);
    List<Application> findByStatus(Application.ApplicationStatus status);
}