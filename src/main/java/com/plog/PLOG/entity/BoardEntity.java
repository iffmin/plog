package com.plog.PLOG.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;


    @ManyToOne
    private entity entity;

    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocationEntity> locationEntityList = new ArrayList<>();

    public void addlocationEntity(LocationEntity entity) {
        entity.setBoardEntity(this);
        locationEntityList.add(entity);
    }

    public void removelocationEntity(BoardEntity entity) {
        entity.setEntity(null);
        locationEntityList.remove(entity);
    }


}