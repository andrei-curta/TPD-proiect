package server.repository;

import entities.FileEntity;
import entities.UserEntity;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;
import java.util.stream.Collectors;

public class FileRepository extends BaseRepository<FileEntity> {
    public FileRepository() {
        super(FileEntity.class);
    }

    public List<FileEntity> getFilesByUser(UserEntity user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<FileEntity> results = session.createQuery("from FileEntity where userByOwnerId = :usr").setParameter("usr", user).list();


        return results;
    }

    public List<FileEntity> getAllFilesCanAccessFrom(UserEntity requestUser, UserEntity owner) {
        try {

            Session session = HibernateUtil.getSessionFactory().openSession();
            List<FileEntity> files = session.createQuery("from FileEntity where userByOwnerId = :usr").setParameter("usr", owner).list();

            List<FileEntity> returnFiles = files.stream().filter(f -> f.getFilePermissionsById().stream().anyMatch(p -> p.getUserByUserId().equals(requestUser))).collect(Collectors.toList());
            session.close();

            return returnFiles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
