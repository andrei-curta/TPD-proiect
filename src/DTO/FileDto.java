package DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import entities.FileEntity;
import entities.FilePermissionEntity;
import entities.FileVersionEntity;
import entities.UserEntity;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class FileDto {
    private Long id;
    private String title;
    private Timestamp dateCreated;
    private String createdBy;
    private FileVersionDto latestVersion;


    private List<String> permissions;
//    private Collection<FilePer> filePermissionsById;
//    private Collection<FileVersionDto> fileVersions;

    public FileDto() {

    }

    public FileDto(FileEntity fileEntity) {
        id = fileEntity.getId();
        title = fileEntity.getTitle();
        dateCreated = fileEntity.getDateCreated();
        createdBy = fileEntity.getUserByOwnerId().getUsername();

        List<FileVersionDto> versions= fileEntity.getFileVersionsById().stream().map(f -> new FileVersionDto(f)).sorted(Comparator.comparing(FileVersionDto::getVersionNumber)).collect(Collectors.toList());
        if(versions.size() > 0) {
            latestVersion = versions.get(versions.size() - 1);
        }
        permissions = fileEntity.getFilePermissionsById().stream().map(f -> f.getPermissionTypeByPermissionTypeId().getName()).collect(Collectors.toList());

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

    public FileVersionDto getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(FileVersionDto latestVersion) {
        this.latestVersion = latestVersion;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

}
