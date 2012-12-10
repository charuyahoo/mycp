// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package in.mycp.domain;

import in.mycp.domain.AvailabilityZoneP;
import in.mycp.domain.Infra;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

privileged aspect AvailabilityZoneP_Roo_DbManaged {
    
    @ManyToOne
    @JoinColumn(name = "infra_id", referencedColumnName = "id")
    private Infra AvailabilityZoneP.infraId;
    
    @Column(name = "name", length = 45)
    private String AvailabilityZoneP.name;
    
    @Column(name = "state", length = 255)
    private String AvailabilityZoneP.state;
    
    @Column(name = "regionName", length = 45)
    private String AvailabilityZoneP.regionName;
    
    @Column(name = "messages", length = 255)
    private String AvailabilityZoneP.messages;
    
    public Infra AvailabilityZoneP.getInfraId() {
        return infraId;
    }
    
    public void AvailabilityZoneP.setInfraId(Infra infraId) {
        this.infraId = infraId;
    }
    
    public String AvailabilityZoneP.getName() {
        return name;
    }
    
    public void AvailabilityZoneP.setName(String name) {
        this.name = name;
    }
    
    public String AvailabilityZoneP.getState() {
        return state;
    }
    
    public void AvailabilityZoneP.setState(String state) {
        this.state = state;
    }
    
    public String AvailabilityZoneP.getRegionName() {
        return regionName;
    }
    
    public void AvailabilityZoneP.setRegionName(String regionName) {
        this.regionName = regionName;
    }
    
    public String AvailabilityZoneP.getMessages() {
        return messages;
    }
    
    public void AvailabilityZoneP.setMessages(String messages) {
        this.messages = messages;
    }
    
}
