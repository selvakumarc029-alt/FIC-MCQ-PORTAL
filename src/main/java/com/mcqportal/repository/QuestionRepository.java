package com.mcqportal.repository;

import com.mcqportal.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {
    Page<Question> findByQuestionContainingIgnoreCase(String query, Pageable pageable);
    List<Question> findTop50ByCategoryIgnoreCaseOrderByIdAsc(String category);
    long countByCategoryIgnoreCase(String category);
}
