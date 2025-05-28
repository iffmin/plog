package com.plog.PLOG.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {
    private Long id;
    private String locationName;
    private String content;
    private Double longitude; //경도
    private Double latitude; // 위도
}
