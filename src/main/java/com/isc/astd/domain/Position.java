package com.isc.astd.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.BatchSize;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "position")
public class Position extends AbstractBaseEntity {

    @Size(min = 1, max = 48)
    @Column(name = "name", length = 48, nullable = false)
    private String name;

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "position",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<User> users = new HashSet<>();

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "position",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<RoutePosition> routePositions = new HashSet<>();

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "id.position",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FilePosition> docPositions = new HashSet<>();

	@JsonIgnore
	@BatchSize(size = 50)
	@OneToMany(
			mappedBy = "position",
			cascade = CascadeType.ALL,
			orphanRemoval = true
	)
	private Set<Route> routes = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "nextSignPosition",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<File> filesToSign = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        return getId() != null && getId().equals(((Position) o).getId());
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<RoutePosition> getRoutePositions() {
        return routePositions;
    }

    public void setRoutePositions(Set<RoutePosition> routePositions) {
        this.routePositions = routePositions;
    }

    public Set<FilePosition> getDocPositions() {
        return docPositions;
    }

    public void setDocPositions(Set<FilePosition> docPositions) {
        this.docPositions = docPositions;
    }

    @Override
    public String toString() {
        return "Position{" +
          "id=" + getId() +
          ", name='" + name + '\'' +
          '}';
    }

    public Set<File> getFilesToSign() {
        return filesToSign;
    }

    public void setFilesToSign(Set<File> filesToSign) {
        this.filesToSign = filesToSign;
    }

	public Set<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(Set<Route> routes) {
		this.routes = routes;
	}
}
