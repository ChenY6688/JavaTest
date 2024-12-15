package com.example.xlsxprocessor.model;

public class RowData {
    private String benefit;
    private String coverage;
    private String category;
    private String planName;
    private String coverageValue;

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getCoverageValue() {
        return coverageValue;
    }

    public void setCoverageValue(String coverageValue) {
        this.coverageValue = coverageValue;
    }
}
