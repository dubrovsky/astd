package com.isc.astd.domain;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "user")
public class User extends AbstractAuditingEntity {

    @Id
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

    @Size(max = 24)
    @Column(name = "phone", length = 24)
    private String phone;

    @Size(max = 24)
    @Email
    @Column(name = "email", length = 24)
    private String email;

    @Column(name = "expired_date")
    private LocalDate expiredDate;

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

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    @Override
    public String toString() {
        return "User{" +
          "id='" + id + '\'' +
          ", name='" + name + '\'' +
//          ", position=" + position +     // session closed exception when delete user
//          ", rootCatId=" + (rootCatalog != null ? rootCatalog.getId() : "") +
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
