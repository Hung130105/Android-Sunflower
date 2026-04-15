package com.example.sunflower.api.request;

import java.util.List;
import java.util.Map;

public class SubmitRequest {
    private Map<String, Integer> answers;
    private List<Integer> selectedParts;
    private int timeSpent;

    public SubmitRequest(Map<String, Integer> answers, List<Integer> selectedParts, int timeSpent) {
        this.answers = answers;
        this.selectedParts = selectedParts;
        this.timeSpent = timeSpent;
    }

    public Map<String, Integer> getAnswers() { return answers; }
    public void setAnswers(Map<String, Integer> answers) { this.answers = answers; }
    public List<Integer> getSelectedParts() { return selectedParts; }
    public void setSelectedParts(List<Integer> selectedParts) { this.selectedParts = selectedParts; }
    public int getTimeSpent() { return timeSpent; }
    public void setTimeSpent(int timeSpent) { this.timeSpent = timeSpent; }
}