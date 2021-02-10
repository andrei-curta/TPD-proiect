package DTO;

import entities.FilePermissionEntity;

public class UserFilePermissionDto {
    private Long fileId;
    private String username;
    private Long userId;

    private String permissionTypeName;
    private Long permissionTypeId;

    public UserFilePermissionDto() {
    }

    public UserFilePermissionDto(FilePermissionEntity filePermissionEntity) {
        fileId = filePermissionEntity.getFileByFileId().getId();
        username = filePermissionEntity.getUserByUserId().getUsername();
        userId = filePermissionEntity.getUserByUserId().getId();

        permissionTypeName = filePermissionEntity.getPermissionTypeByPermissionTypeId().getName();
        permissionTypeId = filePermissionEntity.getPermissionTypeByPermissionTypeId().getId();
    }


    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPermissionTypeName() {
        return permissionTypeName;
    }

    public void setPermissionTypeName(String permissionName) {
        this.permissionTypeName = permissionName;
    }

    public Long getPermissionTypeId() {
        return permissionTypeId;
    }

    public void setPermissionTypeId(Long permissionTypeId) {
        this.permissionTypeId = permissionTypeId;
    }
}
