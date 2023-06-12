package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.HotelDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.LocationEntity;
import com.shameless.bookingtech.domain.factory.HotelFactory;
import com.shameless.bookingtech.domain.mapper.HotelMapper;
import com.shameless.bookingtech.domain.repository.HotelRepository;
import com.shameless.bookingtech.domain.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelFactory hotelFactory;
    private final LocationRepository locationRepository;

    public HotelService(HotelRepository hotelRepository, HotelFactory hotelFactory, LocationRepository locationRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelFactory = hotelFactory;
        this.locationRepository = locationRepository;
    }

    public List<HotelDto> addBulk(List<HotelDto> hotelDtoList) {
        List<HotelDto> hotelDtoListInThisProcess = new ArrayList<>();
        hotelDtoList.forEach(hotelDto -> {
            Optional<HotelEntity> hotel = hotelRepository.findByName(hotelDto.getName());
            if (hotel.isPresent()) {
                HotelEntity hotelEntity = hotel.get();
                if (hotelEntity.getRating().equals(hotelDto.getRating()))
                    hotelEntity.update(hotelDto);
                if (hotelEntity.getLocation().getName().equals(hotelDto.getLocation().getName())) {
                    LocationEntity location = locationRepository.findByName(hotelDto.getLocation().getName())
                            .orElseThrow(() -> new IllegalArgumentException("Location could not found! Location is " + hotelDto.getName()));
                    hotelEntity.update(location);
                    HotelDto updatedDto = HotelMapper.INSTANCE.toDto(hotelEntity);
                    hotelDtoListInThisProcess.add(updatedDto);
                }
            } else {
                LocationEntity location = locationRepository.findByName(hotelDto.getLocation().getName())
                        .orElseThrow(() -> new IllegalArgumentException("Location could not found! Location is " + hotelDto.getName()));
                HotelEntity from = hotelFactory.from(hotelDto, location);
                HotelEntity save = hotelRepository.save(from);
                HotelDto createdDto = HotelMapper.INSTANCE.toDto(save);
                hotelDtoListInThisProcess.add(createdDto);
            }
        });
        return hotelDtoListInThisProcess;
    }
}
