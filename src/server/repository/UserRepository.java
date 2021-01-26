package server.repository;

import entities.FileEntity;
import entities.UserEntity;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepository extends BaseRepository<UserEntity> {

    public UserRepository() {
        super(UserEntity.class);
    }

    public List getAllFilesCanAccessFrom(UserEntity requestUser, UserEntity owner) {
        Collection<FileEntity> files = owner.getFilesById();

        return files.stream().filter(f -> f.getFilePermissionsById().stream().anyMatch(p -> p.getUserByUserId().equals(requestUser))).collect(Collectors.toList());
    }

    public UserEntity getUserByUsername(String username){
        Session session = HibernateUtil.getSessionFactory().openSession();

       List list = session.createQuery("from UserEntity where username = :username").setParameter("username", username).list();

       if (list != null && !list.isEmpty() ){
           return (UserEntity) list.get(0);
       }
       else {
           return null;
       }
    }
}
