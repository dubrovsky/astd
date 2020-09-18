package com.isc.astd.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isc.astd.domain.converter.BranchTypeConverter;
import com.isc.astd.domain.converter.FileReviewStatusConverter;
import com.isc.astd.domain.converter.FileStatusConverter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "file")
public class File extends AbstractBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id")
    private Doc doc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_sign_position_id")
    private Position nextSignPosition;

    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Size(min = 1, max = 12)
    @Column(name = "list_num", precision = 4)
    private String listNum;

    @Column(name = "paper_shl", nullable = false)
    private boolean paperShL = false;

    @Column(name = "paper_shchtd", nullable = false)
    private boolean paperShChTD = false;

    @Size(max = 512)
    @Column(name = "note_shl", length = 512)
    private String noteShl;

    @Size(max = 512)
    @Column(name = "theme_shchtd", length = 512)
    private String themeShchtd;

    @Size(max = 512)
    @Column(name = "descr", length = 512)
    private String descr;

    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "content_type", length = 128, nullable = false)
    private String contentType;

    @NotNull
    @Min(0)
    @Max(999999999)
    @Column(name = "length", precision = 9, nullable = false)
    private long size = 0;

    @Size(max = 96)
    @Column(name = "hash", length = 96)
    private String hash;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_file_id")
    private File parentFile;

    @BatchSize(size = 50)
    @OneToOne(mappedBy = "parentFile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private File childFile;

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "id.file",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FilePosition> filePositions = new HashSet<>();

    @JsonIgnore
    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "file",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FileReview> fileReviews = new HashSet<>();

    @NotNull
    @Convert(converter = FileStatusConverter.class)
    @Column(name = "status", nullable = false)
    private Status status = Status.DEFAULT;

    @Convert(converter = FileStatusConverter.class)
    @Column(name = "status_prev")
    private Status statusPrev;

    @NotNull
    @Convert(converter = FileReviewStatusConverter.class)
    @Column(name = "status_review", nullable = false)
    private StatusReview statusReview = StatusReview.NO;

    @Size(max = 32)
    @Column(name = "status_modified_by", length = 32)
    private String statusModifiedBy;

    @NotNull
    @Convert(converter = BranchTypeConverter.class)
    @Column(name = "branch_type", nullable = false)
    private BranchType branchType = BranchType.DEFAULT;

    @Size(max = 32)
    @Column(name = "fio_sign_1", length = 32)
    private String fioSign1;

    @Size(max = 32)
    @Column(name = "fio_sign_2", length = 32)
    private String fioSign2;

    @Column(name = "date_sign_1")
    private LocalDate dateSign1;

    @Column(name = "date_sign_2")
    private LocalDate dateSign2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getListNum() {
        return listNum;
    }

    public void setListNum(String listNum) {
        this.listNum = listNum;
    }

    public boolean isPaperShL() {
        return paperShL;
    }

    public void setPaperShL(boolean paperShL) {
        this.paperShL = paperShL;
    }

    public boolean isPaperShChTD() {
        return paperShChTD;
    }

    public void setPaperShChTD(boolean paperShChTD) {
        this.paperShChTD = paperShChTD;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof File)) return false;
        return getId() != null && getId().equals(((File) o).getId());
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusText() {
        return status.getText();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BranchType getBranchType() {
        return branchType;
    }

    public void setBranchType(BranchType branchType) {
        this.branchType = branchType;
    }

    public Set<FilePosition> getFilePositions() {
        return filePositions;
    }

    public void setFilePositions(Set<FilePosition> filePositions) {
        this.filePositions = filePositions;
    }

    public File getParentFile() {
        return parentFile;
    }

    public void setParentFile(File parentFile) {
        this.parentFile = parentFile;
    }

    public File getChildFile() {
        return childFile;
    }

    public void setChildFile(File childFile) {
        this.childFile = childFile;
    }

    public String getStatusModifiedBy() {
        return statusModifiedBy;
    }

    public void setStatusModifiedBy(String statusModifiedBy) {
        this.statusModifiedBy = statusModifiedBy;
    }

    public Position getNextSignPosition() {
        return nextSignPosition;
    }

    public void setNextSignPosition(Position nextSignPosition) {
        this.nextSignPosition = nextSignPosition;
    }

    public String getFioSign1() {
        return fioSign1;
    }

    public void setFioSign1(String fioSign1) {
        this.fioSign1 = fioSign1;
    }

    public String getFioSign2() {
        return fioSign2;
    }

    public void setFioSign2(String fioSign2) {
        this.fioSign2 = fioSign2;
    }

    public LocalDate getDateSign1() {
        return dateSign1;
    }

    public void setDateSign1(LocalDate dateSign1) {
        this.dateSign1 = dateSign1;
    }

    public LocalDate getDateSign2() {
        return dateSign2;
    }

    public void setDateSign2(LocalDate dateSign2) {
        this.dateSign2 = dateSign2;
    }

    public String getNoteShl() {
        return noteShl;
    }

    public void setNoteShl(String noteShl) {
        this.noteShl = noteShl;
    }

    public String getThemeShchtd() {
        return themeShchtd;
    }

    public void setThemeShchtd(String themeShchtd) {
        this.themeShchtd = themeShchtd;
    }

    public Status getStatusPrev() {
        return statusPrev;
    }

    public void setStatusPrev(Status statusPrev) {
        this.statusPrev = statusPrev;
    }

    public Set<FileReview> getFileReviews() {
        return fileReviews;
    }

    public void setFileReviews(Set<FileReview> fileReviews) {
        this.fileReviews = fileReviews;
    }

    public StatusReview getStatusReview() {
        return statusReview;
    }

    public void setStatusReview(StatusReview statusReview) {
        this.statusReview = statusReview;
    }

   /* public void removeFilePosition(FilePosition filePosition) {
        filePositions.remove(filePosition);
//        filePosition.getId().setFile(null);
    }*/

    public enum Status {
        DEFAULT("default", "Загружен"),
        REJECTED("rejected", "Отклонено"),
        SIGNING("signing", "На согласовании"),
        APPROVED("approved", "Утверждено"),
        DAMAGED("damaged", "Повреждён"),
        INVALID("invalid", "Недействительный"),
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

    public enum StatusReview {
        NO("no", "Нет сверки"),
        SIGNED("signed", "Сверен"),
        INVALID("invalid", "Недействительный");

        private final String code;
        private final String text;

        StatusReview(String code, String text) {
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

    public enum BranchType {
        DEFAULT("default", "В работе"),
        APPROVED("approved", "Утверждено"),
        REJECTED("rejected", "Отклонено"),
        ARCHIVE("archive", "Архив");

        private final String code;
        private final String text;

        BranchType(String code, String text) {
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

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", id=" + getId() +
                ", listNum='" + listNum + '\'' +
                ", descr='" + descr + '\'' +
                '}';
    }
}
