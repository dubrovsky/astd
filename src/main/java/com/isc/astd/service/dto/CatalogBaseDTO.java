package com.isc.astd.service.dto;

import javax.validation.constraints.NotNull;

/**
 * @author p.dzeviarylin
 */
public class CatalogBaseDTO implements Comparable<CatalogBaseDTO>{

    private Long id;
    private String name;

    public CatalogBaseDTO() {
    }

    public CatalogBaseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public int compareTo(@NotNull CatalogBaseDTO that) {
        return this.id.compareTo(that.id);
    }
}
