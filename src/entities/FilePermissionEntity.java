package entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "file_permission", schema = "tpd", catalog = "")
public class FilePermissionEntity {
    private Long id;
    private FileEntity fileByFileId;
    private UserEntity userByUserId;
    private PermissionTypeEntity permissionTypeByPermissionTypeId;

    @Id
    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilePermissionEntity that = (FilePermissionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id", nullable = false)
    public FileEntity getFileByFileId() {
        return fileByFileId;
    }

    public void setFileByFileId(FileEntity fileByFileId) {
        this.fileByFileId = fileByFileId;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    public UserEntity getUserByUserId() {
        return userByUserId;
    }

    public void setUserByUserId(UserEntity userByUserId) {
        this.userByUserId = userByUserId;
    }

    @ManyToOne
    @JoinColumn(name = "permission_type_id", referencedColumnName = "id", nullable = false)
    public PermissionTypeEntity getPermissionTypeByPermissionTypeId() {
        return permissionTypeByPermissionTypeId;
    }

    public void setPermissionTypeByPermissionTypeId(PermissionTypeEntity permissionTypeByPermissionTypeId) {
        this.permissionTypeByPermissionTypeId = permissionTypeByPermissionTypeId;
    }
}
