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
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "route")
public class Route extends AbstractBaseEntity {

    @Size(min = 1, max = 48)
    @Column(name = "name", length = 48, nullable = false)
    private String name;

	@Column(name = "expired_date")
	private Instant expiredDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "position_id")
	private Position position;

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "route",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<File> files = new HashSet<>();

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "id.route",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<RoutePosition> routePositions = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        return getId() != null && getId().equals(((Route) o).getId());
    }

    public Set<RoutePosition> getRoutePositions() {
        return routePositions;
    }

    public void setRoutePositions(Set<RoutePosition> routePositions) {
        this.routePositions = routePositions;
    }

    public Set<File> getFiles() {
        return files;
    }

    public void setFiles(Set<File> files) {
        this.files = files;
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
}
