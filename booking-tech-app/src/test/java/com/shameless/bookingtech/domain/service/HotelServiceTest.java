package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.HotelDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.LocationEntity;
import com.shameless.bookingtech.domain.factory.HotelFactory;
import com.shameless.bookingtech.domain.mapper.HotelMapper;
import com.shameless.bookingtech.domain.repository.HotelRepository;
import com.shameless.bookingtech.domain.repository.LocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class HotelServiceTest {

    private HotelService underTest;

    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private HotelFactory hotelFactory;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private SearchCriteriaService searchCriteriaService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new HotelService(hotelRepository, hotelFactory, locationRepository, searchCriteriaService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void canAddBulk() {
        LocationEntity location = LocationEntity.builder()
                .id(1L)
                .name("location")
                .build();
        HotelEntity hotel1 = HotelEntity.builder()
                .id(1L)
                .name("hotel1")
                .rating(6.7)
                .location(location)
                .build();
        HotelDto hotelDto1 = HotelMapper.INSTANCE.toDto(hotel1);
        HotelEntity hotel2 = HotelEntity.builder()
                .id(2L)
                .name("hotel2")
                .rating(7.5)
                .location(location)
                .build();
        HotelDto hotelDto2 = HotelMapper.INSTANCE.toDto(hotel2);
        List<HotelDto> hotelDtoList = List.of(hotelDto1, hotelDto2);

        when(hotelRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(locationRepository.findByName(anyString())).thenReturn(Optional.of(location));
        when(hotelFactory.from(hotelDto1, location)).thenReturn(hotel1);
        when(hotelFactory.from(hotelDto2, location)).thenReturn(hotel2);
        when(hotelRepository.save(hotel1)).thenReturn(hotel1);
        when(hotelRepository.save(hotel2)).thenReturn(hotel2);

        List<HotelDto> hotelDtoList1 = underTest.addBulk(hotelDtoList);

        //then
        verify(hotelRepository, times(hotelDtoList1.size())).findByName(anyString());
        verify(locationRepository, times(hotelDtoList1.size())).findByName(anyString());
        verify(hotelFactory, times(hotelDtoList1.size())).from(any(HotelDto.class), any(LocationEntity.class));
        verify(hotelRepository, times(hotelDtoList1.size())).save(any(HotelEntity.class));
    }
}