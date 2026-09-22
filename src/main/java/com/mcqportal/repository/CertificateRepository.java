package com.mcqportal.repository;

import com.mcqportal.entity.Certificate;
import com.mcqportal.entity.Result;
import com.mcqportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
    Optional<Certificate> findByCertificateId(String certificateId);
    Optional<Certificate> findByResult(Result result);
    List<Certificate> findByUserOrderByIssueDateDesc(User user);
    Page<Certificate> findByCertificateIdContainingIgnoreCaseOrStudentNameContainingIgnoreCase(String certificateId, String studentName, Pageable pageable);
}
