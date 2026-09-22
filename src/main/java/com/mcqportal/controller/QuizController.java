package com.mcqportal.controller;

import com.mcqportal.entity.User;
import com.mcqportal.repository.QuestionRepository;
import com.mcqportal.repository.ResultRepository;
import com.mcqportal.repository.UserRepository;
import com.mcqportal.service.QuizService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class QuizController extends ControllerSupport {
    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final QuizService quizService;

    @Value("${fic.quiz.duration-minutes:30}")
    private int durationMinutes;

    public QuizController(UserRepository userRepository, QuestionRepository questionRepository,
                          ResultRepository resultRepository, QuizService quizService) {
        super(userRepository);
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.quizService = quizService;
    }

    @GetMapping("/quiz/start")
    public String start(@RequestParam(required = false) String category, Authentication authentication,
                        Model model, RedirectAttributes redirectAttributes) {
        User user = currentUser(authentication);
        String selectedCategory = (category == null || category.isBlank()) ? "Cybersecurity" : category;
        if (resultRepository.existsByUserAndCategoryIgnoreCaseAndMalpracticeDetectedTrue(user, selectedCategory)) {
            redirectAttributes.addFlashAttribute("error",
                    "You cannot attend the " + selectedCategory + " quiz again because it was auto-submitted for malpractice.");
            return "redirect:/dashboard";
        }
        model.addAttribute("questions", questionRepository.findTop50ByCategoryIgnoreCaseOrderByIdAsc(selectedCategory));
        model.addAttribute("category", selectedCategory);
        model.addAttribute("durationMinutes", durationMinutes);
        return "quiz/start";
    }

    @PostMapping("/quiz/submit")
    public String submit(Authentication authentication, @RequestParam Map<String, String> requestParams) {
        User user = currentUser(authentication);
        Map<String, String> answers = new HashMap<>();
        requestParams.forEach((key, value) -> {
            if (key.startsWith("answer_")) {
                answers.put(key.substring(7), value);
            }
        });
        String category = requestParams.get("category");
        if (resultRepository.existsByUserAndCategoryIgnoreCaseAndMalpracticeDetectedTrue(user, category)) {
            throw new org.springframework.security.access.AccessDeniedException("This quiz is blocked after malpractice auto-submit.");
        }
        boolean malpracticeDetected = Boolean.parseBoolean(requestParams.getOrDefault("malpracticeDetected", "false"));
        int malpracticeWarnings = parseInt(requestParams.get("malpracticeWarnings"));
        var result = quizService.submit(user, answers, category, malpracticeDetected, malpracticeWarnings,
                requestParams.get("malpracticeReason"));
        return "redirect:/results/" + result.getId();
    }

    private int parseInt(String value) {
        try {
            return value == null ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    @GetMapping("/results")
    public String results(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute("results", resultRepository.findByUserOrderByExamDateDesc(user));
        return "results/history";
    }

    @GetMapping("/results/{id}")
    public String result(@org.springframework.web.bind.annotation.PathVariable String id, Authentication authentication, Model model) {
        User user = currentUser(authentication);
        var result = resultRepository.findById(id).orElseThrow();
        if (!result.getUser().getId().equals(user.getId()) && user.getRole() != com.mcqportal.entity.Role.ROLE_ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        model.addAttribute("result", result);
        return "results/detail";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        model.addAttribute("results", resultRepository.findTop10ByOrderByScoreDescPercentageDescExamDateAsc());
        return "results/leaderboard";
    }
}
