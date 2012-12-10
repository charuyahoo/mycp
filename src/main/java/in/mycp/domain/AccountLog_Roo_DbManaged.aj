// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package in.mycp.domain;

import in.mycp.domain.AccountLog;
import in.mycp.domain.User;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

privileged aspect AccountLog_Roo_DbManaged {
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User AccountLog.userId;
    
    @Column(name = "task", length = 90)
    private String AccountLog.task;
    
    @Column(name = "details", length = 255)
    private String AccountLog.details;
    
    @Column(name = "time_of_entry")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date AccountLog.timeOfEntry;
    
    @Column(name = "status")
    private Integer AccountLog.status;
    
    public User AccountLog.getUserId() {
        return userId;
    }
    
    public void AccountLog.setUserId(User userId) {
        this.userId = userId;
    }
    
    public String AccountLog.getTask() {
        return task;
    }
    
    public void AccountLog.setTask(String task) {
        this.task = task;
    }
    
    public String AccountLog.getDetails() {
        return details;
    }
    
    public void AccountLog.setDetails(String details) {
        this.details = details;
    }
    
    public Date AccountLog.getTimeOfEntry() {
        return timeOfEntry;
    }
    
    public void AccountLog.setTimeOfEntry(Date timeOfEntry) {
        this.timeOfEntry = timeOfEntry;
    }
    
    public Integer AccountLog.getStatus() {
        return status;
    }
    
    public void AccountLog.setStatus(Integer status) {
        this.status = status;
    }
    
}
