package com.mcqportal.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "certificates")
public class Certificate {
    @Id
    private String id;

    @Indexed(unique = true)
    private String certificateId;

    @DBRef
    private User user;

    @DBRef
    private Result result;

    private String studentName;

    private double percentage;

    private LocalDate issueDate = LocalDate.now();

    private ResultStatus status = ResultStatus.PASS;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public ResultStatus getStatus() { return status; }
    public void setStatus(ResultStatus status) { this.status = status; }
}
