package entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "user", schema = "tpd", catalog = "")
public class UserEntity {
    private Long id;
    private String username;
    private Collection<DownloadHistoryEntity> downloadHistoriesById;
    private Collection<FileEntity> filesById;
    private Collection<FilePermissionEntity> filePermissionsById;
    private Collection<FileVersionEntity> fileVersionsById;

    @Id
    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "username", nullable = false, length = 50)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @OneToMany(mappedBy = "userByUserId")
    public Collection<DownloadHistoryEntity> getDownloadHistoriesById() {
        return downloadHistoriesById;
    }

    public void setDownloadHistoriesById(Collection<DownloadHistoryEntity> downloadHistoriesById) {
        this.downloadHistoriesById = downloadHistoriesById;
    }

    @OneToMany(mappedBy = "userByOwnerId")
    public Collection<FileEntity> getFilesById() {
        return filesById;
    }

    public void setFilesById(Collection<FileEntity> filesById) {
        this.filesById = filesById;
    }

    @OneToMany(mappedBy = "userByUserId")
    public Collection<FilePermissionEntity> getFilePermissionsById() {
        return filePermissionsById;
    }

    public void setFilePermissionsById(Collection<FilePermissionEntity> filePermissionsById) {
        this.filePermissionsById = filePermissionsById;
    }

    @OneToMany(mappedBy = "userByModifiedBy")
    public Collection<FileVersionEntity> getFileVersionsById() {
        return fileVersionsById;
    }

    public void setFileVersionsById(Collection<FileVersionEntity> fileVersionsById) {
        this.fileVersionsById = fileVersionsById;
    }
}
