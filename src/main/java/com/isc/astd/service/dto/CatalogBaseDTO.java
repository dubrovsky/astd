package com.isc.astd.service.dto;

import com.isc.astd.domain.Catalog;

import javax.validation.constraints.NotNull;

/**
 * @author p.dzeviarylin
 */
public class CatalogBaseDTO implements Comparable<CatalogBaseDTO> {

    private Long id;
    private String name;
    private Catalog.Type type = Catalog.Type.DEFAULT;

    public CatalogBaseDTO() {
    }

    public CatalogBaseDTO(Long id, String name, Catalog.Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Catalog.Type getType() {
        return type;
    }

    public void setType(Catalog.Type type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NotNull CatalogBaseDTO that) {
        if (this.type == Catalog.Type.SCHEME) {
            return this.getName().compareTo(that.getName());
        } else {
            return this.id.compareTo(that.id);
        }
    }
}
