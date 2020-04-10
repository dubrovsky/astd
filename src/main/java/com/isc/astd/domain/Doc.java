package com.isc.astd.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.BatchSize;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "doc")
public class Doc extends AbstractBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id")
    private Catalog catalog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_cat_id")
    private Catalog rootCatalog;

    @Column(name = "npp", precision = 16, nullable = false)
    private long npp = 1;
    
    @NotNull
    @Size(min = 1, max = 24)
    @Column(name = "num", length = 24, nullable = false)
    private String num;

    @Size(max = 512)
    @Column(name = "descr", length = 512)
    private String descr;

    @Size(max = 512)
    @Column(name = "note_shl", length = 512)
    private String noteShl;

    @NotNull
    @Column(name = "read_only", nullable = false)
    private boolean readOnly = false;

    @NotNull
    @Column(name = "archive", nullable = false)
    private boolean archive = false;

    @NotNull
    @Column(name = "rejected", nullable = false)
    private boolean rejected = false;

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "doc",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<File> files = new HashSet<>();

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public long getNpp() {
        return npp;
    }

    public void setNpp(long npp) {
        this.npp = npp;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doc)) return false;
        return getId() != null && getId().equals(((Doc) o).getId());
    }

    public Set<File> getFiles() {
        return files;
    }

    public void setFiles(Set<File> files) {
        this.files = files;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    @Override
    public String toString() {
        return "Doc{" +
          "num='" + num + '\'' +
          ", descr='" + descr + '\'' +
          '}';
    }

    public Catalog getRootCatalog() {
        return rootCatalog;
    }

    public void setRootCatalog(Catalog rootCatalog) {
        this.rootCatalog = rootCatalog;
    }

    public String getNoteShl() {
        return noteShl;
    }

    public void setNoteShl(String noteShl) {
        this.noteShl = noteShl;
    }
}
