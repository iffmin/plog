package com.plog.PLOG.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardDTO {

    private Long id;
    private String title;
    private List<LocationDTO> locations;
}