package DTO;

import entities.DownloadHistoryEntity;
import entities.FileEntity;
import entities.FileVersionEntity;
import entities.UserEntity;

import java.sql.Timestamp;
import java.util.Collection;

public class FileVersionDto implements Comparable<FileVersionDto> {
    private Long id;
    private Timestamp modifiedOn;
    private String contents;
    private Integer versionNumber;
    private String modifiedBy;

    public FileVersionDto() {
    }

    public FileVersionDto(FileVersionEntity fileVersionEntity) {
        id = fileVersionEntity.getId();
        modifiedOn = fileVersionEntity.getModifiedOn();
        contents = fileVersionEntity.getContents();
        versionNumber = fileVersionEntity.getVersionNumber();
        modifiedBy = fileVersionEntity.getUserByModifiedBy().getUsername();
    }

    @Override
    public int compareTo(FileVersionDto o) {
        return Integer.compare(versionNumber, o.versionNumber);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
