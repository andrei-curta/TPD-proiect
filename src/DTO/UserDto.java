package DTO;

import entities.UserEntity;

public class UserDto {
    private Long id;
    private String username;

    public UserDto(UserEntity userEntity) {
        id = userEntity.getId();
        username = userEntity.getUsername();
    }

    public UserDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString(){
        return username;
    }
}
