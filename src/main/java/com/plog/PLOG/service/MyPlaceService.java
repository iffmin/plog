package com.plog.PLOG.service;

import com.plog.PLOG.entity.Place;
import com.plog.PLOG.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyPlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    public Place savePlace(Place Place) {
        return placeRepository.save(Place);
    }
}
