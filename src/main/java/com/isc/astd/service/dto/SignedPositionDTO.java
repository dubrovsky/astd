package com.isc.astd.service.dto;

import com.isc.astd.domain.Position;

import java.util.Objects;

/**
 * @author p.dzeviarylin
 */
public class SignedPositionDTO {

    private Position position;
    private int order;
    private String createdBy;

    public SignedPositionDTO(Position position, int order, String createdBy) {
        this.position = position;
        this.order = order;
        this.createdBy = createdBy;
    }

    public SignedPositionDTO(Position position, int order) {
        this.position = position;
        this.order = order;
    }

    public Position getPosition() {
        return position;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedPositionDTO that = (SignedPositionDTO) o;
        return order == that.order &&
                Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {

        return Objects.hash(position, order);
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
