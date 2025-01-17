package entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "permission_type", schema = "tpd", catalog = "")
public class PermissionTypeEntity {
    private Long id;
    private String name;
    private Collection<FilePermissionEntity> filePermissionsById;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionTypeEntity that = (PermissionTypeEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @OneToMany(mappedBy = "permissionTypeByPermissionTypeId")
    public Collection<FilePermissionEntity> getFilePermissionsById() {
        return filePermissionsById;
    }

    public void setFilePermissionsById(Collection<FilePermissionEntity> filePermissionsById) {
        this.filePermissionsById = filePermissionsById;
    }
}
