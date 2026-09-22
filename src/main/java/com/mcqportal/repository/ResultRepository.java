package com.mcqportal.repository;

import com.mcqportal.entity.Result;
import com.mcqportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends MongoRepository<Result, String>, ResultRepositoryCustom {
    List<Result> findByUserOrderByExamDateDesc(User user);
    Page<Result> findByUserFullnameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(String fullname, String email, Pageable pageable);
    Page<Result> findByMalpracticeWarningsGreaterThan(int warnings, Pageable pageable);
    Page<Result> findByMalpracticeWarningsGreaterThanAndUserFullnameContainingIgnoreCaseOrMalpracticeWarningsGreaterThanAndUserEmailContainingIgnoreCase(
            int fullnameWarnings, String fullname, int emailWarnings, String email, Pageable pageable);
    Optional<Result> findFirstByUserOrderByExamDateDesc(User user);
    List<Result> findTop10ByOrderByScoreDescPercentageDescExamDateAsc();
    boolean existsByUserAndCategoryIgnoreCaseAndMalpracticeDetectedTrue(User user, String category);
    long countByMalpracticeWarningsGreaterThan(int warnings);
}
