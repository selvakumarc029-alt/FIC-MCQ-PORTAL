package com.mcqportal.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "answers")
public class Answer {
    @Id
    private String id;

    @DBRef
    private User user;

    @DBRef
    private Question question;

    private String selectedAnswer;

    @DBRef
    private Result result;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer == null ? null : selectedAnswer.trim().toUpperCase(); }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
}
