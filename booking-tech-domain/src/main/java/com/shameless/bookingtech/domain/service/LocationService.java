package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.LocationDto;
import com.shameless.bookingtech.domain.entity.LocationEntity;
import com.shameless.bookingtech.domain.factory.LocationFactory;
import com.shameless.bookingtech.domain.mapper.LocationMapper;
import com.shameless.bookingtech.domain.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationFactory locationFactory;

    public LocationService(LocationRepository locationRepository, LocationFactory locationFactory) {
        this.locationRepository = locationRepository;
        this.locationFactory = locationFactory;
    }

    @Transactional
    public List<LocationDto> addBulk(List<String> locations) {
        List<LocationDto> locationDtoList = new ArrayList<>();
        locations.forEach(location -> {
            boolean existsByName = locationRepository.existsByName(location);
            if (!existsByName) {
                LocationDto locationDto = LocationDto.builder()
                        .name(location)
                        .build();
                LocationEntity from = locationFactory.from(locationDto);
                LocationEntity save = locationRepository.save(from);
                LocationDto createdLocationDto = LocationMapper.INSTANCE.toDto(save);
                locationDtoList.add(createdLocationDto);
            }
        });
        return locationDtoList;
    }

    public LocationDto getByName(String location) {
        return locationRepository.findByName(location).map(LocationMapper.INSTANCE::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Loocation not found! Name: "
                        + location));
    }
}
