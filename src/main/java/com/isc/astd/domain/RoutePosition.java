package com.isc.astd.domain;

import com.isc.astd.domain.converter.RoutePositionStatusConverter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "route_position")
public class RoutePosition {

    @EmbeddedId
    private RoutePositionId id;

    @NotNull
    @Min(0)
    @Max(999)
    @Column(name = "group", precision = 3, nullable = false)
    private int group;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @NotNull
    @Convert(converter = RoutePositionStatusConverter.class)
    @Column(name = "status", nullable = false)
    private Status status = Status.MAKE;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status{
        MAKE("make", "Разработал"),
        CHECK("check", "Проверил"),
        COORDINATE("coordinate", "Согласовал"),
        APPROVE("approve", "Утвердил"),
	    REFERENCE("reference", "Для справки");

        private final String code;
        private final String text;

        Status(String code, String text) {
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }

    public RoutePositionId getId() {
        return id;
    }

    public void setId(RoutePositionId id) {
        this.id = id;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
