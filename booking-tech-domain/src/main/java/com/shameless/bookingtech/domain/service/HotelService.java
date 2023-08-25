package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.HotelDto;
import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.LocationEntity;
import com.shameless.bookingtech.domain.factory.HotelFactory;
import com.shameless.bookingtech.domain.mapper.HotelMapper;
import com.shameless.bookingtech.domain.repository.HotelRepository;
import com.shameless.bookingtech.domain.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelFactory hotelFactory;
    private final LocationRepository locationRepository;
    private final SearchCriteriaService searchCriteriaService;

    public HotelService(HotelRepository hotelRepository, HotelFactory hotelFactory, LocationRepository locationRepository, SearchCriteriaService searchCriteriaService) {
        this.hotelRepository = hotelRepository;
        this.hotelFactory = hotelFactory;
        this.locationRepository = locationRepository;
        this.searchCriteriaService = searchCriteriaService;
    }

    public List<HotelDto> addBulk(List<HotelDto> hotelDtoList) {
        List<HotelDto> hotelDtoListInThisProcess = new ArrayList<>();
        hotelDtoList.forEach(newHotelDto -> {
            Optional<HotelEntity> hotel = hotelRepository.findByName(newHotelDto.getName());
            if (hotel.isPresent()) {
                HotelEntity hotelEntity = hotel.get();
                if (!Objects.equals(newHotelDto.getRating(), hotelEntity.getRating()))
                    hotelEntity.update(newHotelDto);
                if (!Objects.equals(hotelEntity.getLocation().getName(), newHotelDto.getLocation().getName())) {
                    LocationEntity location = locationRepository.findByName(newHotelDto.getLocation().getName())
                            .orElseThrow(() -> new IllegalArgumentException("Location could not found! Location is " + newHotelDto.getName()));
                    hotelEntity.update(location);
                    HotelDto updatedDto = HotelMapper.INSTANCE.toDto(hotelEntity);
                    hotelDtoListInThisProcess.add(updatedDto);
                }
            } else {
                LocationEntity location = locationRepository.findByName(newHotelDto.getLocation().getName())
                        .orElseThrow(() -> new IllegalArgumentException("Location could not found! Location is " + newHotelDto.getName()));
                HotelEntity from = hotelFactory.from(newHotelDto, location);
                HotelEntity save = hotelRepository.save(from);
                HotelDto createdDto = HotelMapper.INSTANCE.toDto(save);
                hotelDtoListInThisProcess.add(createdDto);
            }
        });
        return hotelDtoListInThisProcess;
    }

    public List<HotelDto> findAllWithLastPriceInfo() {
        SearchCriteriaDto scDto = searchCriteriaService.getCriteriaByParams();
        return hotelRepository.findAllWithLastPriceInfo(scDto.getId()).stream()
                .map(HotelMapper.INSTANCE::toDtoWithPrices).collect(Collectors.toList());
    }
}
