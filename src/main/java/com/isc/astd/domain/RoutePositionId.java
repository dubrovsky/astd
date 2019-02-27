package com.isc.astd.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author p.dzeviarylin
 */
@Embeddable
public class RoutePositionId implements Serializable{

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @NotNull
    @Column(name = "order", nullable = false)
    private int order;

    public RoutePositionId() {
    }

    public RoutePositionId(Route route, int order) {
        this.route = route;
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoutePositionId)) return false;
        RoutePositionId that = (RoutePositionId) o;
        return Objects.equals(getRoute(), that.getRoute()) &&
                Objects.equals(getOrder(), that.getOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoute(), getOrder());
    }

    public Route getRoute() {
        return route;
    }

    public int getOrder() {
        return order;
    }
}
