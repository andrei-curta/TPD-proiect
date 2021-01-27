package DTO;

import entities.FileEntity;
import entities.FilePermissionEntity;
import entities.FileVersionEntity;
import entities.UserEntity;

import java.sql.Timestamp;
import java.util.Collection;

public class FileDto {
    private Long id;
    private String title;
    private Timestamp dateCreated;
    private String createdBy;
//    private Collection<FilePer> filePermissionsById;
//    private Collection<FileVersionDto> fileVersions;


    public FileDto(FileEntity fileEntity) {
        id = fileEntity.getId();
        title = fileEntity.getTitle();
        dateCreated = fileEntity.getDateCreated();
        createdBy = fileEntity.getUserByOwnerId().getUsername();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
