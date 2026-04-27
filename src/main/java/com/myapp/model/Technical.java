package com.myapp.model;
import java.sql.Date;
import java.sql.Time;

public class Technical {
	private int assessmentId;
	private String assessmentTitle;
    private Date assignedDate;
    private Date dueDate;
    private String assessmentNotes;
    private String completionStatus;
    private String scoreOrPassFail;
    
    public int getAssessmentId() {
    	return assessmentId;
    }
    public void setAssessmentId(int assessmentId) {
    	this.assessmentId = assessmentId;
    }
	public String getAssessmentTitle() {
		return assessmentTitle;
	}
	public void setAssessmentTitle(String assessmentTitle) {
		this.assessmentTitle = assessmentTitle;
	}
	public Date getAssignedDate() {
		return assignedDate;
	}
	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public String getAssessmentNotes() {
		return assessmentNotes;
	}
	public void setAssessmentNotes(String assessmentNotes) {
		this.assessmentNotes = assessmentNotes;
	}
	public String getCompletionStatus() {
		return completionStatus;
	}
	public void setCompletionStatus(String completionStatus) {
		this.completionStatus = completionStatus;
	}
	public String getScoreOrPassFail() {
		return scoreOrPassFail;
	}
	public void setScoreOrPassFail(String scoreOrPassFail) {
		this.scoreOrPassFail = scoreOrPassFail;
	}
    
}

