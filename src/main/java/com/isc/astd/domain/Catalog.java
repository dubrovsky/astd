package com.isc.astd.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isc.astd.domain.converter.CatalogTypeConverter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "catalog")
public class Catalog extends AbstractBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_catalog_id")
    private Catalog parentCatalog;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Column(name = "read_only", nullable = false)
    private boolean readOnly = false;

    @NotNull
    @Min(0)
    @Max(99999)
    @Column(name = "level", precision = 5, nullable = false)
    private int level = 0;

    @NotNull
    @Convert(converter = CatalogTypeConverter.class)
    @Column(name = "type", nullable = false)
    private Type type = Type.DEFAULT;


    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "parentCatalog",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Catalog> childCatalogs = new HashSet<>();

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "catalog",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Doc> docs = new HashSet<>();

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "rootCatalog",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Doc> docsForRoot = new HashSet<>();

    public Set<Doc> getDocs() {
        return docs;
    }

    public void setDocs(Set<Doc> docs) {
        this.docs = docs;
    }

    public boolean isReadOnlyByDefault() {
        return type == Type.ROOT || type == Type.LINE || type == Type.STATION || type == Type.REJECTED || type == Type.ARCHIVE;
    }

    public Set<Doc> getDocsForRoot() {
        return docsForRoot;
    }

    public void setDocsForRoot(Set<Doc> docsForRoot) {
        this.docsForRoot = docsForRoot;
    }

    public enum Type{
        DEFAULT("default", "default", -1, false),
        ROOT("root", "root", 0, true),
        STATION("station", "Станция", 1, true),
        LINE("line", "Перегон", 1, true),
        ARCHIVE("archive", "Архив", 2, true) {
            @Override
            public long generateId(long id) {
                return id + 1000000;
            }
        },
        REJECTED("rejected", "Отклонено", 2, true) {
            @Override
            public long generateId(long id) {
                return id + 2000000;
            }
        },
        SCHEME("scheme", "scheme", -1, false);

        private final String code;
        private final String name;
        private final int level;
        private final boolean readOnly;

        Type(String code, String name, int level, boolean readOnly) {
            this.code = code;
            this.name = name;
            this.level = level;
            this.readOnly = readOnly;
        }

        public long generateId(long id) {
            return id;
        }

        public String  getCode() {
            return code;
        }
        public String  getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public boolean isReadOnly() {
            return readOnly;
        }

    }

    public Catalog(Catalog parentCatalog, @NotNull @Size(min = 1, max = 50) String name, @NotNull boolean readOnly, @NotNull @Min(0) @Max(99999) int level, @NotNull Type type) {
        this.parentCatalog = parentCatalog;
        this.name = name;
        this.readOnly = readOnly;
        this.level = level;
        this.type = type;
    }

    public Catalog() {
    }

    public Catalog getParentCatalog() {
        return parentCatalog;
    }

    public void setParentCatalog(Catalog parentCatalog) {
        this.parentCatalog = parentCatalog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Set<Catalog> getChildCatalogs() {
        return childCatalogs;
    }

    public void setChildCatalogs(Set<Catalog> childCatalogs) {
        this.childCatalogs = childCatalogs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Catalog)) return false;
        return getId() != null && getId().equals(((Catalog) o).getId());
    }
}
