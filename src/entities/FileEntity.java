package entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "file", schema = "tpd", catalog = "")
public class FileEntity {
    private Long id;
    private String title;
    private Timestamp dateCreated;
    private UserEntity userByOwnerId;
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
    @Column(name = "title", nullable = false, length = 50)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "date_created", nullable = false)
    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(dateCreated, that.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, dateCreated);
    }

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    public UserEntity getUserByOwnerId() {
        return userByOwnerId;
    }

    public void setUserByOwnerId(UserEntity userByOwnerId) {
        this.userByOwnerId = userByOwnerId;
    }

    @OneToMany(mappedBy = "fileByFileId")
    public Collection<FilePermissionEntity> getFilePermissionsById() {
        return filePermissionsById;
    }

    public void setFilePermissionsById(Collection<FilePermissionEntity> filePermissionsById) {
        this.filePermissionsById = filePermissionsById;
    }

    @OneToMany(mappedBy = "fileByFileId")
    public Collection<FileVersionEntity> getFileVersionsById() {
        return fileVersionsById;
    }

    public void setFileVersionsById(Collection<FileVersionEntity> fileVersionsById) {
        this.fileVersionsById = fileVersionsById;
    }
}
