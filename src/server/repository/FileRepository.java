package server.repository;

import entities.FileEntity;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;

public class FileRepository extends BaseRepository<FileEntity> {
    public FileRepository() {
        super(FileEntity.class);
    }

}
