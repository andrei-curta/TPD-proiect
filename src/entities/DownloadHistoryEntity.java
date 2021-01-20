package entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "download_history", schema = "tpd", catalog = "")
public class DownloadHistoryEntity {
    private Long id;
    private Timestamp date;
    private FileVersionEntity fileVersionByFileVersionId;
    private UserEntity userByUserId;

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
    @Column(name = "date", nullable = false)
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadHistoryEntity that = (DownloadHistoryEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date);
    }

    @ManyToOne
    @JoinColumn(name = "file_version_id", referencedColumnName = "id", nullable = false)
    public FileVersionEntity getFileVersionByFileVersionId() {
        return fileVersionByFileVersionId;
    }

    public void setFileVersionByFileVersionId(FileVersionEntity fileVersionByFileVersionId) {
        this.fileVersionByFileVersionId = fileVersionByFileVersionId;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    public UserEntity getUserByUserId() {
        return userByUserId;
    }

    public void setUserByUserId(UserEntity userByUserId) {
        this.userByUserId = userByUserId;
    }
}
