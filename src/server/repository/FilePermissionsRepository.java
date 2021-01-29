package server.repository;

import entities.FileEntity;
import entities.FilePermissionEntity;
import entities.PermissionTypeEntity;
import entities.UserEntity;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;
import java.util.stream.Collectors;

public class FilePermissionsRepository extends BaseRepository<FilePermissionEntity> {
    public FilePermissionsRepository() {
        super(FilePermissionEntity.class);
    }

    public List<String> filePermissions(UserEntity user, FileEntity file) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        List<FilePermissionEntity> list = session.createQuery("from FilePermissionEntity where fileByFileId.id = :fileId and userByUserId.id = :userId").setParameter("fileId", file.getId()).setParameter("userId", user.getId()).list();
        return list.stream().map(f -> f.getPermissionTypeByPermissionTypeId().getName()).collect(Collectors.toList());
    }

    public void addFilePermissionsForUser(FileEntity file, UserEntity user, List<PermissionTypeEntity> permissions) {
        for (PermissionTypeEntity permission : permissions) {
            FilePermissionEntity filePermission = new FilePermissionEntity();
            filePermission.setFileByFileId(file);
            filePermission.setUserByUserId(user);
            filePermission.setPermissionTypeByPermissionTypeId(permission);

            create(filePermission);
        }
    }
}
