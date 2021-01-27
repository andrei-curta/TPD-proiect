package DTO;

import entities.DownloadHistoryEntity;
import entities.FileEntity;
import entities.UserEntity;

import java.sql.Timestamp;
import java.util.Collection;

public class FileVersionDto {
    private Long id;
    private Timestamp modifiedOn;
    private String contents;
    private Integer versionNumber;
    private String modifiedBy;
}
