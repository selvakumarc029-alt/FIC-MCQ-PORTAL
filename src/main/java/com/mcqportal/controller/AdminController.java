package com.mcqportal.controller;

import com.mcqportal.entity.Question;
import com.mcqportal.entity.Role;
import com.mcqportal.repository.CertificateRepository;
import com.mcqportal.repository.QuestionRepository;
import com.mcqportal.repository.ResultRepository;
import com.mcqportal.repository.UserRepository;
import com.mcqportal.service.CertificateService;
import com.mcqportal.service.QuestionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final List<String> QUESTION_CATEGORIES = List.of("Cybersecurity", "Web Development", "UI/UX", "Java");

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final ResultRepository resultRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateService certificateService;

    public AdminController(UserRepository userRepository, QuestionRepository questionRepository,
                           QuestionService questionService, ResultRepository resultRepository,
                           CertificateRepository certificateRepository, CertificateService certificateService) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.questionService = questionService;
        this.resultRepository = resultRepository;
        this.certificateRepository = certificateRepository;
        this.certificateService = certificateService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userRepository.countByRole(Role.ROLE_USER));
        model.addAttribute("totalQuestions", questionRepository.count());
        model.addAttribute("totalExams", resultRepository.count());
        model.addAttribute("totalCertificates", certificateRepository.count());
        model.addAttribute("malpracticeCases", resultRepository.countByMalpracticeWarningsGreaterThan(0));
        model.addAttribute("averageScore", resultRepository.averagePercentage());
        return "admin/dashboard";
    }

    @GetMapping("/answer-key")
    public String answerKey(@RequestParam(defaultValue = "Cybersecurity") String answerCategory, Model model) {
        model.addAttribute("categories", QUESTION_CATEGORIES);
        model.addAttribute("answerCategory", answerCategory);
        model.addAttribute("answerKeyQuestions", questionRepository.findTop50ByCategoryIgnoreCaseOrderByIdAsc(answerCategory));
        return "admin/answer-key";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") int page, Model model) {
        var pageable = PageRequest.of(page, 10, Sort.by("fullname"));
        model.addAttribute("users", q.isBlank() ? userRepository.findAll(pageable) :
                userRepository.findByFullnameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable));
        model.addAttribute("q", q);
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/questions")
    public String questions(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("questions", questionService.search(q, PageRequest.of(page, 10, Sort.by("id").descending())));
        model.addAttribute("q", q);
        model.addAttribute("questionForm", new Question());
        model.addAttribute("categories", QUESTION_CATEGORIES);
        return "admin/questions";
    }

    @GetMapping("/questions/{id}/edit")
    public String editQuestion(@PathVariable String id, Model model) {
        model.addAttribute("questionForm", questionRepository.findById(id).orElseThrow());
        model.addAttribute("questions", questionRepository.findAll(PageRequest.of(0, 10, Sort.by("id").descending())));
        model.addAttribute("q", "");
        model.addAttribute("categories", QUESTION_CATEGORIES);
        return "admin/questions";
    }

    @PostMapping("/questions")
    public String saveQuestion(@ModelAttribute Question question, RedirectAttributes redirectAttributes) {
        try {
            questionService.save(question);
            redirectAttributes.addFlashAttribute("success", "Question saved successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/questions";
    }

    @PostMapping("/questions/{id}/delete")
    public String deleteQuestion(@PathVariable String id) {
        questionRepository.deleteById(id);
        return "redirect:/admin/questions";
    }

    @GetMapping("/results")
    public String results(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") int page, Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("examDate").descending());
        model.addAttribute("results", q.isBlank() ? resultRepository.findAll(pageable) :
                resultRepository.findByUserFullnameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(q, q, pageable));
        model.addAttribute("q", q);
        return "admin/results";
    }

    @GetMapping("/malpractice")
    public String malpractice(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") int page, Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("examDate").descending());
        model.addAttribute("results", q.isBlank() ? resultRepository.findByMalpracticeWarningsGreaterThan(0, pageable) :
                resultRepository.findByMalpracticeWarningsGreaterThanAndUserFullnameContainingIgnoreCaseOrMalpracticeWarningsGreaterThanAndUserEmailContainingIgnoreCase(
                        0, q, 0, q, pageable));
        model.addAttribute("q", q);
        return "admin/malpractice";
    }

    @GetMapping("/certificates")
    public String certificates(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") int page, Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("issueDate").descending());
        model.addAttribute("certificates", q.isBlank() ? certificateRepository.findAll(pageable) :
                certificateRepository.findByCertificateIdContainingIgnoreCaseOrStudentNameContainingIgnoreCase(q, q, pageable));
        model.addAttribute("q", q);
        return "admin/certificates";
    }

    @PostMapping("/certificates/{id}/reissue")
    public String reissue(@PathVariable String id) {
        var certificate = certificateRepository.findById(id).orElseThrow();
        certificateService.generateForResult(certificate.getResult());
        return "redirect:/admin/certificates";
    }

    @PostMapping("/certificates/{id}/delete")
    public String deleteCertificate(@PathVariable String id) {
        certificateRepository.deleteById(id);
        return "redirect:/admin/certificates";
    }
}
