package com.mcqportal.repository;

import com.mcqportal.entity.Role;
import com.mcqportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRole(Role role);
    Page<User> findByFullnameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullname, String email, Pageable pageable);
}
