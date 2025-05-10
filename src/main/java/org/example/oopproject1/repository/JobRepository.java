package org.example.oopproject1.repository;

import org.example.oopproject1.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    // Add pagination support to existing methods
    Page<Job> findAll(Pageable pageable);
    Page<Job> findByCompany(String company, Pageable pageable);
    Page<Job> findByTitleContaining(String title, Pageable pageable);
    Page<Job> findByLocationAndIsActiveTrue(String location, Pageable pageable);
    Page<Job> findByDeadlineDateAfter(LocalDate date, Pageable pageable);
    Page<Job> findByRecruiterId(String recruiterId, Pageable pageable);

    // Keep existing methods for non-paginated queries
    List<Job> findByCompany(String company);
    List<Job> findByTitleContaining(String title);
    List<Job> findByLocationAndIsActiveTrue(String location);
    List<Job> findByDeadlineDateAfter(LocalDate date);
    List<Job> findByRecruiterId(String recruiterId);

    // Add these methods for keyword search
    @Query("{ $or: [ " +
            "{ 'title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'company': { $regex: ?0, $options: 'i' } }, " +
            "{ 'description': { $regex: ?0, $options: 'i' } } ] }")
    List<Job> searchByKeyword(String keyword);

    @Query("{ $or: [ " +
            "{ 'title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'company': { $regex: ?0, $options: 'i' } }, " +
            "{ 'description': { $regex: ?0, $options: 'i' } } ] }")
    Page<Job> searchByKeyword(String keyword, Pageable pageable);
}