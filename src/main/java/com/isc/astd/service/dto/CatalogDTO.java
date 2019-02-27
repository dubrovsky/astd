package com.isc.astd.service.dto;

import com.isc.astd.domain.Catalog;
import com.isc.astd.domain.File;

import javax.validation.constraints.NotNull;
import java.util.TreeSet;

/**
 * @author p.dzeviarylin
 */
public class CatalogDTO implements Comparable<CatalogDTO>{

    private Long id;
    private Long realId;
    private Long parentCatalogId;
    private String name;
    private boolean readOnly;
    private int level;
    private Catalog.Type type = Catalog.Type.DEFAULT;
    private File.BranchType branchType = File.BranchType.DEFAULT;
    private boolean isCatalog = true;

//    private Set<CatalogDTO> childCatalogs = new HashSet<>();

    private String text;
    private boolean leaf = false;
    private boolean expanded = false;
    private TreeSet<CatalogDTO> children = new TreeSet<>();

    public CatalogDTO() {
    }

    public CatalogDTO(Long parentCatalogId, Catalog.Type type, Long id, Long dbId, File.BranchType branchType) {
        this.parentCatalogId = parentCatalogId;
        this.type = type;
        this.name = type.getName();
        this.text = type.getName();
        this.level = type.getLevel();
        this.readOnly = type.isReadOnly();
        this.id = id;
        this.realId = dbId;
        this.branchType = branchType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentCatalogId() {
        return parentCatalogId;
    }

    public void setParentCatalogId(Long parentCatalogId) {
        this.parentCatalogId = parentCatalogId;
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

    public Catalog.Type getType() {
        return type;
    }

    public void setType(Catalog.Type type) {
        this.type = type;
    }

    /*public Set<CatalogDTO> getChildCatalogs() {
        return childCatalogs;
    }

    public void setChildCatalogs(Set<CatalogDTO> childCatalogs) {
        this.childCatalogs = childCatalogs;
    }*/

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public TreeSet<CatalogDTO> getChildren() {
        return children;
    }

    public void setChildren(TreeSet<CatalogDTO> children) {
        this.children = children;
    }

    @Override
    public int compareTo(@NotNull CatalogDTO that) {
        return this.id.compareTo(that.id);
    }

    public Long getRealId() {
        return realId;
    }

    public void setRealId(Long realId) {
        this.realId = realId;
    }

    public File.BranchType getBranchType() {
        return branchType;
    }

    public void setBranchType(File.BranchType branchType) {
        this.branchType = branchType;
    }

    public boolean isCatalog() {
        return isCatalog;
    }

    public void setCatalog(boolean catalog) {
        this.isCatalog = catalog;
    }
}
