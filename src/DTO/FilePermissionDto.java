package DTO;

import entities.FileEntity;
import entities.PermissionTypeEntity;
import entities.UserEntity;

public class FilePermissionDto {
    private Long id;
    private FileEntity fileByFileId;
   // private UserDto userByUserId;
    private PermissionTypeEntity permissionTypeByPermissionTypeId;
}
