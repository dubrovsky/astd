package com.isc.astd.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "file_position")
public class FilePosition extends AbstractAuditingEntity{

    @EmbeddedId
    private FilePositionId id;

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

    public FilePosition(FilePositionId id, byte[] ecp) {
        this.id = id;
        this.ecp = ecp;
    }

    public FilePosition() {
    }

    public FilePosition(FilePositionId id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public FilePositionId getId() {
        return id;
    }

    public void setId(FilePositionId id) {
        this.id = id;
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
    public String toString() {
        return "FilePosition{" +
          "id=" + id +
          ", msg='" + msg + '\'' +
          ", invalid=" + invalid +
          '}';
    }
}
