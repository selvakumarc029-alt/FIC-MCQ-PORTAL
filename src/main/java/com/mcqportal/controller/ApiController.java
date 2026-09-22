package com.mcqportal.controller;

import com.mcqportal.entity.Question;
import com.mcqportal.repository.CertificateRepository;
import com.mcqportal.repository.QuestionRepository;
import com.mcqportal.repository.ResultRepository;
import com.mcqportal.repository.UserRepository;
import com.mcqportal.service.CertificateService;
import com.mcqportal.service.QuestionService;
import com.mcqportal.service.QuizService;
import com.mcqportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final QuizService quizService;
    private final ResultRepository resultRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateService certificateService;

    public ApiController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder,
                         QuestionRepository questionRepository, QuestionService questionService, QuizService quizService,
                         ResultRepository resultRepository, CertificateRepository certificateRepository,
                         CertificateService certificateService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.questionRepository = questionRepository;
        this.questionService = questionService;
        this.quizService = quizService;
        this.resultRepository = resultRepository;
        this.certificateRepository = certificateRepository;
        this.certificateService = certificateService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        var user = userService.register(request.fullname(), request.email(), request.password(), request.confirmPassword());
        return ResponseEntity.ok(Map.of("id", user.getId(), "email", user.getEmail(), "message", "Registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.email().toLowerCase())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(Map.of("message", "Login successful", "role", user.getRole())))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }

    @PostMapping("/admin/questions")
    public Question addQuestion(@RequestBody Question question) {
        return questionService.save(question);
    }

    @GetMapping("/questions")
    public java.util.List<Question> getQuestions() {
        return questionRepository.findAll();
    }

    @PostMapping("/quiz/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody SubmitQuizRequest request) {
        var user = userRepository.findById(request.userId()).orElseThrow();
        var result = quizService.submit(user, request.answers());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    public Object getResults(@RequestParam(required = false) String userId) {
        if (userId == null) {
            return resultRepository.findAll();
        }
        var user = userRepository.findById(userId).orElseThrow();
        return resultRepository.findByUserOrderByExamDateDesc(user);
    }

    @GetMapping("/leaderboard")
    public Object leaderboard() {
        return resultRepository.findTop10ByOrderByScoreDescPercentageDescExamDateAsc();
    }

    @PostMapping("/certificates/generate/{resultId}")
    public ResponseEntity<?> generateCertificate(@PathVariable String resultId) {
        var result = resultRepository.findById(resultId).orElseThrow();
        return ResponseEntity.ok(certificateService.generateForResult(result));
    }

    @GetMapping("/certificates/verify/{certificateId}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certificateId) {
        return certificateRepository.findByCertificateId(certificateId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Certificate Not Found")));
    }

    public record RegisterRequest(String fullname, String email, String password, String confirmPassword) {}
    public record LoginRequest(String email, String password) {}
    public record SubmitQuizRequest(String userId, Map<String, String> answers) {}
}
