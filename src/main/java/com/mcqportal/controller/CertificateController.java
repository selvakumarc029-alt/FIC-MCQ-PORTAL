package com.mcqportal.controller;

import com.mcqportal.entity.User;
import com.mcqportal.repository.CertificateRepository;
import com.mcqportal.repository.ResultRepository;
import com.mcqportal.repository.UserRepository;
import com.mcqportal.service.CertificateService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Controller
public class CertificateController extends ControllerSupport {
    private final CertificateRepository certificateRepository;
    private final ResultRepository resultRepository;
    private final CertificateService certificateService;

    public CertificateController(UserRepository userRepository, CertificateRepository certificateRepository,
                                 ResultRepository resultRepository, CertificateService certificateService) {
        super(userRepository);
        this.certificateRepository = certificateRepository;
        this.resultRepository = resultRepository;
        this.certificateService = certificateService;
    }

    @GetMapping("/certificates")
    public String certificates(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute("certificates", certificateRepository.findByUserOrderByIssueDateDesc(user));
        model.addAttribute("results", resultRepository.findByUserOrderByExamDateDesc(user));
        return "certificates/list";
    }

    @GetMapping("/certificates/{id}")
    public String certificate(@PathVariable String id, Authentication authentication, Model model) {
        User user = currentUser(authentication);
        var certificate = certificateRepository.findById(id).orElseThrow();
        if (!certificate.getUser().getId().equals(user.getId()) && user.getRole() != com.mcqportal.entity.Role.ROLE_ADMIN) {
            throw new AccessDeniedException("Access denied");
        }
        model.addAttribute("certificate", certificate);
        return "certificates/detail";
    }

    @GetMapping("/certificates/{id}/download")
    public void download(@PathVariable String id, Authentication authentication, HttpServletResponse response) throws IOException {
        User user = currentUser(authentication);
        var certificate = certificateRepository.findById(id).orElseThrow();
        if (!certificate.getUser().getId().equals(user.getId()) && user.getRole() != com.mcqportal.entity.Role.ROLE_ADMIN) {
            throw new AccessDeniedException("Access denied");
        }
        byte[] pdf = certificateService.createPdf(certificate);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + certificate.getCertificateId() + ".pdf");
        response.getOutputStream().write(pdf);
    }
}
