package com.myapp.model;
import java.sql.Date;

public class Application {
    private String jobTitle;
    private String companyName;
    private String appStatus;
    private Date dateApplied;

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getAppStatus() { return appStatus; }
    public void setAppStatus(String appStatus) { this.appStatus = appStatus; }
    public Date getDateApplied() { return dateApplied; }
    public void setDateApplied(Date dateApplied) { this.dateApplied = dateApplied; }
}