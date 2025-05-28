package com.plog.PLOG.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LocationEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String locationName;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private Double longitude; //경도

    @Column
    private Double latitude; // 위도

    @ManyToOne
    private BoardEntity boardEntity;

}
