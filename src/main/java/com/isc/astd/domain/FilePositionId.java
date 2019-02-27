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
public class FilePositionId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;
    
    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @NotNull
    @Column(name = "`order`", nullable = false)
    private int order;

    public FilePositionId() {
    }

    public FilePositionId(File file, Position position, int order) {
        this.file = file;
        this.position = position;
        this.order = order;
    }

    public File getFile() {
        return file;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilePositionId)) return false;
        FilePositionId that = (FilePositionId) o;
        return Objects.equals(getFile(), that.getFile()) &&
                Objects.equals(getPosition(), that.getPosition()) &&
                (getOrder() == that.getOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFile(), getPosition(), getOrder());
    }

    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "FilePositionId{" +
          "file=" + file +
          ", position=" + position +
          ", order=" + order +
          '}';
    }
}
