package com.mcqportal.service;

import com.mcqportal.entity.*;
import com.mcqportal.repository.AnswerRepository;
import com.mcqportal.repository.QuestionRepository;
import com.mcqportal.repository.ResultRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuizService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ResultRepository resultRepository;
    private final CertificateService certificateService;

    public QuizService(QuestionRepository questionRepository, AnswerRepository answerRepository,
                       ResultRepository resultRepository, CertificateService certificateService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.resultRepository = resultRepository;
        this.certificateService = certificateService;
    }

    public Result submit(User user, Map<String, String> selectedAnswers) {
        return submit(user, selectedAnswers, null);
    }

    public Result submit(User user, Map<String, String> selectedAnswers, String category) {
        return submit(user, selectedAnswers, category, false, 0, null);
    }

    public Result submit(User user, Map<String, String> selectedAnswers, String category,
                         boolean malpracticeDetected, int malpracticeWarnings, String malpracticeReason) {
        List<Question> questions = (category == null || category.isBlank())
                ? questionRepository.findAll()
                : questionRepository.findTop50ByCategoryIgnoreCaseOrderByIdAsc(category);
        int attempted = 0;
        int correct = 0;

        Result result = new Result();
        result.setUser(user);
        result.setCategory(category);
        result.setTotalQuestions(questions.size());
        result.setExamDate(LocalDateTime.now());
        result.setMalpracticeDetected(malpracticeDetected);
        result.setMalpracticeWarnings(Math.max(0, malpracticeWarnings));
        result.setMalpracticeReason(cleanMalpracticeReason(malpracticeReason));

        for (Question question : questions) {
            String selected = Optional.ofNullable(selectedAnswers.get(question.getId()))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .filter(value -> value.matches("[ABCD]"))
                    .orElse("");
            if (!selected.isBlank()) {
                attempted++;
                if (selected.equals(question.getCorrectAnswer())) {
                    correct++;
                }
            }
        }

        int wrong = attempted - correct;
        double percentage = questions.isEmpty() ? 0 : (correct * 100.0 / questions.size());
        result.setAttemptedQuestions(attempted);
        result.setCorrectAnswers(correct);
        result.setWrongAnswers(wrong);
        result.setScore(correct);
        result.setPercentage(Math.round(percentage * 100.0) / 100.0);
        result.setStatus(malpracticeDetected ? ResultStatus.MALPRACTICE : result.getPercentage() >= 50 ? ResultStatus.PASS : ResultStatus.FAIL);
        Result saved = resultRepository.save(result);

        for (Question question : questions) {
            Answer answer = new Answer();
            answer.setUser(user);
            answer.setQuestion(question);
            answer.setSelectedAnswer(selectedAnswers.get(question.getId()));
            answer.setResult(saved);
            answerRepository.save(answer);
        }
        if (saved.getStatus() == ResultStatus.PASS) {
            certificateService.generateForResult(saved);
        }
        return saved;
    }

    private String cleanMalpracticeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return null;
        }
        String trimmed = reason.trim();
        return trimmed.length() > 255 ? trimmed.substring(0, 255) : trimmed;
    }
}
