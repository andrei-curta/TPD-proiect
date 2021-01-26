package server.repository;

import entities.FilePermissionEntity;
import entities.FileVersionEntity;

public class FileVersionRepository extends BaseRepository<FileVersionEntity> {
    public FileVersionRepository() {
        super(FileVersionEntity.class);
    }
}
