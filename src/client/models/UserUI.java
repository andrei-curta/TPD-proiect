package client.models;

import entities.DownloadHistoryEntity;
import entities.FileEntity;
import entities.FilePermissionEntity;
import entities.FileVersionEntity;

import java.util.Collection;

public class UserUI {
    private Long id;
    private String username;
    private Collection<DownloadHistoryEntity> downloadHistoriesById;
    private Collection<FileEntity> filesById;
    private Collection<FilePermissionEntity> filePermissionsById;
    private Collection<FileVersionEntity> fileVersionsById;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<DownloadHistoryEntity> getDownloadHistoriesById() {
        return downloadHistoriesById;
    }

    public void setDownloadHistoriesById(Collection<DownloadHistoryEntity> downloadHistoriesById) {
        this.downloadHistoriesById = downloadHistoriesById;
    }

    public Collection<FileEntity> getFilesById() {
        return filesById;
    }

    public void setFilesById(Collection<FileEntity> filesById) {
        this.filesById = filesById;
    }

    public Collection<FilePermissionEntity> getFilePermissionsById() {
        return filePermissionsById;
    }

    public void setFilePermissionsById(Collection<FilePermissionEntity> filePermissionsById) {
        this.filePermissionsById = filePermissionsById;
    }

    public Collection<FileVersionEntity> getFileVersionsById() {
        return fileVersionsById;
    }

    public void setFileVersionsById(Collection<FileVersionEntity> fileVersionsById) {
        this.fileVersionsById = fileVersionsById;
    }

    @Override
    public String toString(){
        return username;
    }
}
