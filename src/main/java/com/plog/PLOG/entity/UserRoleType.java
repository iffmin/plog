package com.plog.PLOG.entity;

import lombok.Getter;

@Getter
public enum UserRoleType {

    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String description;

    UserRoleType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
