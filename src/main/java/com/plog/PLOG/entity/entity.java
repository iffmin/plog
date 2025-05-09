package com.plog.PLOG.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class entity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;
    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRoleType role;

    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardEntity> boardEntityList = new ArrayList<>();

    public void addBoardEntity(BoardEntity entity) {
        entity.setEntity(this);
        boardEntityList.add(entity);
    }

    public void removeBoardEntity(BoardEntity entity) {
        entity.setEntity(null);
        boardEntityList.remove(entity);
    }


}
