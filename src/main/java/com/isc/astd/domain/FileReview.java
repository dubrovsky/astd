package com.isc.astd.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "file_review")
public class FileReview extends AbstractBaseEntity {

    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Size(min = 1, max = 512)
    @Column(name = "msg", length = 512)
    private String msg;

    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ecp")
    private byte[] ecp;

    @NotNull
    @Column(name = "invalid", nullable = false)
    private boolean invalid = false;

    public FileReview() {
    }

    public FileReview(File file, Position position, byte[] ecp) {
        this.file = file;
        this.position = position;
        this.ecp = ecp;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public byte[] getEcp() {
        return ecp;
    }

    public void setEcp(byte[] ecp) {
        this.ecp = ecp;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileReview)) return false;
        return getId() != null && getId().equals(((FileReview) o).getId());
    }
}
