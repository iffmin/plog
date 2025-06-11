package com.plog.PLOG.repository;

import com.plog.PLOG.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    List<LocationEntity> findByBoardEntityId(Long id);

    List<Long> findAllByBoardEntityId(Long id);

    List<LocationEntity> findByLatitudeBetweenAndLongitudeBetween(double latStart, double latEnd, double lonStart, double lonEnd);

}
