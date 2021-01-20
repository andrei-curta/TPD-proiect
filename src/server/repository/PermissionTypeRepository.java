package server.repository;

import entities.PermissionTypeEntity;

public class PermissionTypeRepository extends BaseRepository<PermissionTypeEntity> {


    public PermissionTypeRepository() {
        super(PermissionTypeEntity.class);
    }
}
