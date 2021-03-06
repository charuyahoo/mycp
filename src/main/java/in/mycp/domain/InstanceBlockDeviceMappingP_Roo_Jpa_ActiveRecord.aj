// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package in.mycp.domain;

import in.mycp.domain.InstanceBlockDeviceMappingP;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect InstanceBlockDeviceMappingP_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager InstanceBlockDeviceMappingP.entityManager;
    
    public static final EntityManager InstanceBlockDeviceMappingP.entityManager() {
        EntityManager em = new InstanceBlockDeviceMappingP().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long InstanceBlockDeviceMappingP.countInstanceBlockDeviceMappingPs() {
        return entityManager().createQuery("SELECT COUNT(o) FROM InstanceBlockDeviceMappingP o", Long.class).getSingleResult();
    }
    
    public static List<InstanceBlockDeviceMappingP> InstanceBlockDeviceMappingP.findAllInstanceBlockDeviceMappingPs() {
        return entityManager().createQuery("SELECT o FROM InstanceBlockDeviceMappingP o", InstanceBlockDeviceMappingP.class).getResultList();
    }
    
    public static InstanceBlockDeviceMappingP InstanceBlockDeviceMappingP.findInstanceBlockDeviceMappingP(Integer id) {
        if (id == null) return null;
        return entityManager().find(InstanceBlockDeviceMappingP.class, id);
    }
    
    public static List<InstanceBlockDeviceMappingP> InstanceBlockDeviceMappingP.findInstanceBlockDeviceMappingPEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM InstanceBlockDeviceMappingP o", InstanceBlockDeviceMappingP.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void InstanceBlockDeviceMappingP.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void InstanceBlockDeviceMappingP.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            InstanceBlockDeviceMappingP attached = InstanceBlockDeviceMappingP.findInstanceBlockDeviceMappingP(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void InstanceBlockDeviceMappingP.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void InstanceBlockDeviceMappingP.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public InstanceBlockDeviceMappingP InstanceBlockDeviceMappingP.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        InstanceBlockDeviceMappingP merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
