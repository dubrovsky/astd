package com.isc.astd.domain;

import com.isc.astd.domain.converter.EntityAuditActionConverter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "audit")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @NotNull
    @Size(max = 255)
    @Column(name = "entity_type", length = 256, nullable = false)
    private String entityType;

    @NotNull
    @Convert(converter = EntityAuditActionConverter.class)
    @Column(name = "action", length = 24, nullable = false)
    private Action action;

    @Lob
    @Column(name = "entity_value")
    private String entityValue;

    @Column(name = "commit_version")
    private Integer commitVersion;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 32, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public enum Action {
        CREATE("create", "создать"),
        UPDATE("update", "обновить"),
        DELETE("delete", "удалить"),
        CATALOG_CREATE("catalog_create", "создать каталог"),
        CATALOG_UPDATE("catalog_update", "обновить каталог"),
        CATALOG_DELETE("catalog_delete", "удалить каталог"),
        DOC_CREATE("doc_create", "создать документ"),
        DOC_UPDATE("doc_update", "обновить документ"),
        DOC_DELETE("doc_delete", "удалить документ"),
        FILE_CREATE("file_create", "создать файл"),
        FILE_UPDATE("file_update", "обновить файл"),
        FILE_DELETE("file_delete", "удалить файл"),
        FILE_NEW_VERSION("file_new_version", "новая версия файла"),
        FILE_SAVE_ECP("file_save_ecp", "подписать файл"),
        FILE_REJECT("file_reject", "отклонить файл"),
        FILE_REJECT_CANCEL("file_reject_cancel", "отменить отклонение файла"),
        FILE_ARCHIVE("file_archive", "архивировать файл"),
        FILE_ARCHIVE_CANCEL("file_archive_cancel", "отменить архивирование файла"),
        FILE_PAPER_COPY("file_paper_copy", "бумажная копия актуализирована"),
	    FILE_ORIGINAL_CHECKED("file_original_checked", "подлинник проверен"),
	    THEME_UPDATED("theme_updated", "тема обновлена"),
	    NOTE_UPDATED("note_updated", "комментарий обновлен")
        ;

        private final String code;
        private final String text;

        Action(String code, String text) {
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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getCommitVersion() {
        return commitVersion;
    }

    public void setCommitVersion(Integer commitVersion) {
        this.commitVersion = commitVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Audit)) return false;
        return getId() != null && getId().equals(((Audit) o).getId());
    }
}
