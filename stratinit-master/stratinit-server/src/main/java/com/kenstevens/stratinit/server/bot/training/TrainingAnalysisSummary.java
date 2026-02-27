package com.kenstevens.stratinit.server.bot.training;

import java.util.List;
import java.util.Map;

public class TrainingAnalysisSummary {
    private Map<String, Double> averageCities;
    private Map<String, Double> averageUnits;
    private Map<String, Double> averageTech;
    private Map<String, Double> averageExplored;
    private Map<String, Double> averageScore;
    private Map<String, Integer> actionFrequency;
    private Map<String, Map<String, Integer>> actionFrequencyByCategory;
    private Map<String, Double> averageMilestones;
    private List<String> failurePatterns;
    private Map<Integer, Map<String, Double>> stateProgression;

    public Map<String, Double> getAverageCities() { return averageCities; }
    public void setAverageCities(Map<String, Double> averageCities) { this.averageCities = averageCities; }

    public Map<String, Double> getAverageUnits() { return averageUnits; }
    public void setAverageUnits(Map<String, Double> averageUnits) { this.averageUnits = averageUnits; }

    public Map<String, Double> getAverageTech() { return averageTech; }
    public void setAverageTech(Map<String, Double> averageTech) { this.averageTech = averageTech; }

    public Map<String, Double> getAverageExplored() { return averageExplored; }
    public void setAverageExplored(Map<String, Double> averageExplored) { this.averageExplored = averageExplored; }

    public Map<String, Double> getAverageScore() { return averageScore; }
    public void setAverageScore(Map<String, Double> averageScore) { this.averageScore = averageScore; }

    public Map<String, Integer> getActionFrequency() { return actionFrequency; }
    public void setActionFrequency(Map<String, Integer> actionFrequency) { this.actionFrequency = actionFrequency; }

    public Map<String, Map<String, Integer>> getActionFrequencyByCategory() { return actionFrequencyByCategory; }
    public void setActionFrequencyByCategory(Map<String, Map<String, Integer>> actionFrequencyByCategory) { this.actionFrequencyByCategory = actionFrequencyByCategory; }

    public Map<String, Double> getAverageMilestones() { return averageMilestones; }
    public void setAverageMilestones(Map<String, Double> averageMilestones) { this.averageMilestones = averageMilestones; }

    public List<String> getFailurePatterns() { return failurePatterns; }
    public void setFailurePatterns(List<String> failurePatterns) { this.failurePatterns = failurePatterns; }

    public Map<Integer, Map<String, Double>> getStateProgression() { return stateProgression; }
    public void setStateProgression(Map<Integer, Map<String, Double>> stateProgression) { this.stateProgression = stateProgression; }
}
