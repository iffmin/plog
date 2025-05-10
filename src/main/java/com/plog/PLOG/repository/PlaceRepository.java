package com.plog.PLOG.repository;

import com.plog.PLOG.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}