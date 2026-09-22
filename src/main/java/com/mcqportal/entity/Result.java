package com.mcqportal.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "results")
public class Result {
    @Id
    private String id;

    @DBRef
    private User user;

    private String userFullname;
    private String userEmail;

    private int score;
    private int totalQuestions;
    private int attemptedQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private double percentage;
    private String category;
    private boolean malpracticeDetected = false;
    private int malpracticeWarnings = 0;
    private String malpracticeReason;
    private ResultStatus status;
    private LocalDateTime examDate = LocalDateTime.now();

    @DBRef
    private Certificate certificate;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userFullname = user.getFullname();
            this.userEmail = user.getEmail();
        }
    }
    public String getUserFullname() { return userFullname != null ? userFullname : (user != null ? user.getFullname() : null); }
    public void setUserFullname(String userFullname) { this.userFullname = userFullname; }
    public String getUserEmail() { return userEmail != null ? userEmail : (user != null ? user.getEmail() : null); }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public int getAttemptedQuestions() { return attemptedQuestions; }
    public void setAttemptedQuestions(int attemptedQuestions) { this.attemptedQuestions = attemptedQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
    public int getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(int wrongAnswers) { this.wrongAnswers = wrongAnswers; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isMalpracticeDetected() { return malpracticeDetected; }
    public void setMalpracticeDetected(boolean malpracticeDetected) { this.malpracticeDetected = malpracticeDetected; }
    public int getMalpracticeWarnings() { return malpracticeWarnings; }
    public void setMalpracticeWarnings(int malpracticeWarnings) { this.malpracticeWarnings = malpracticeWarnings; }
    public String getMalpracticeReason() { return malpracticeReason; }
    public void setMalpracticeReason(String malpracticeReason) { this.malpracticeReason = malpracticeReason; }
    public ResultStatus getStatus() { return status; }
    public void setStatus(ResultStatus status) { this.status = status; }
    public LocalDateTime getExamDate() { return examDate; }
    public void setExamDate(LocalDateTime examDate) { this.examDate = examDate; }
    public Certificate getCertificate() { return certificate; }
    public void setCertificate(Certificate certificate) { this.certificate = certificate; }
}
