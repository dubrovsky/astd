package com.isc.astd.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "user")
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Size(max = 64)
    @Column(name = "name", length = 64)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;
    
    @Size(max = 48)
    @Column(name = "organization", length = 48)
    private String organization;

    @Column(name = "expired_date")
    private Instant expiredDate;

    /*@Column(name = "root_cat_id")
    private long rootCatId;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_cat_id")
    private Catalog rootCatalog;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

   /* public long getRootCatId() {
        return rootCatId;
    }

    public void setRootCatId(long rootCatId) {
        this.rootCatId = rootCatId;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return getId() != null && !getId().isEmpty() && getId().equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Instant getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Instant expiredDate) {
        this.expiredDate = expiredDate;
    }

    @Override
    public String toString() {
        return "User{" +
          "id='" + id + '\'' +
          ", name='" + name + '\'' +
          ", position=" + position +
          ", rootCatId=" + (rootCatalog != null ? rootCatalog.getId() : "") +
          ", organization='" + organization + '\'' +
          ", expiredDate=" + expiredDate +
          '}';
    }

    public Catalog getRootCatalog() {
        return rootCatalog;
    }

    public void setRootCatalog(Catalog rootCatalog) {
        this.rootCatalog = rootCatalog;
    }
}
