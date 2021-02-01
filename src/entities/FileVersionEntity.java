package entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "file_version", schema = "tpd", catalog = "")
public class FileVersionEntity {
    private Long id;
    private Timestamp modifiedOn;
    private String contents;
    private Integer versionNumber;
    private Collection<DownloadHistoryEntity> downloadHistoriesById;
    private FileEntity fileByFileId;
    private UserEntity userByModifiedBy;

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
        return Objects.equals(id, that.id) &&
                Objects.equals(modifiedOn, that.modifiedOn) &&
                Objects.equals(contents, that.contents) &&
                Objects.equals(versionNumber, that.versionNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modifiedOn, contents, versionNumber);
    }

    @OneToMany(mappedBy = "fileVersionByFileVersionId")
    public Collection<DownloadHistoryEntity> getDownloadHistoriesById() {
        return downloadHistoriesById;
    }

    public void setDownloadHistoriesById(Collection<DownloadHistoryEntity> downloadHistoriesById) {
        this.downloadHistoriesById = downloadHistoriesById;
    }

    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id", nullable = false)
    @Fetch(FetchMode.SELECT)
    public FileEntity getFileByFileId() {
        return fileByFileId;
    }

    public void setFileByFileId(FileEntity fileByFileId) {
        this.fileByFileId = fileByFileId;
    }

    @ManyToOne
    @JoinColumn(name = "modified_by", referencedColumnName = "id", nullable = false)
    @Fetch(FetchMode.SELECT)
    public UserEntity getUserByModifiedBy() {
        return userByModifiedBy;
    }

    public void setUserByModifiedBy(UserEntity userByModifiedBy) {
        this.userByModifiedBy = userByModifiedBy;
    }
}
