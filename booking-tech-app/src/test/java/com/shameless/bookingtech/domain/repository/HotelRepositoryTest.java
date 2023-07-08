package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.app.BookingtechApplication;
import com.shameless.bookingtech.app.service.ProcessService;
import com.shameless.bookingtech.domain.dto.HotelDto;
import com.shameless.bookingtech.domain.dto.LocationDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.LocationEntity;
import com.shameless.bookingtech.domain.factory.HotelFactory;
import com.shameless.bookingtech.domain.factory.LocationFactory;
import com.shameless.bookingtech.integration.automation.infra.AppDriverFactory;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {BookingtechApplication.class})
@MockBean(classes = {ProcessService.class, AppDriverFactory.class})
class HotelRepositoryTest {

    @Autowired
    private HotelRepository underTest;

    @Autowired
    private HotelFactory hotelFactory;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationFactory locationFactory;

    @Test
    void itShouldBeFindHotelByName() {
        LocationDto locationDto = LocationDto.builder()
                .name("Norwich")
                .build();

        LocationEntity location = locationFactory.from(locationDto);
        LocationEntity savedLocation = locationRepository.save(location);
        String given = "Riverside Guest Hotel";
        HotelDto hotelDto = HotelDto.builder()
                .name(given)
                .rating(6.8)
                .build();
        HotelEntity hotel = hotelFactory.from(hotelDto, savedLocation);
        underTest.save(hotel);

        Optional<HotelEntity> riversideGuestHotel = underTest.findByName(given);
        assertThat(riversideGuestHotel).isNotEmpty();
        assertThat(riversideGuestHotel.get().getName()).isEqualTo(given);

    }
}