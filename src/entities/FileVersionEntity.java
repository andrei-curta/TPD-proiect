package entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "file_version", schema = "tpd", catalog = "")
public class FileVersionEntity {
    private long id;
    private Timestamp modifiedOn;
    private String contents;
    private Integer versionNumber;

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "modified_on", nullable = false)
    public Timestamp getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Basic
    @Column(name = "contents", nullable = true, length = -1)
    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Basic
    @Column(name = "version_number", nullable = true)
    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileVersionEntity that = (FileVersionEntity) o;
        return id == that.id &&
                Objects.equals(modifiedOn, that.modifiedOn) &&
                Objects.equals(contents, that.contents) &&
                Objects.equals(versionNumber, that.versionNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modifiedOn, contents, versionNumber);
    }
}
